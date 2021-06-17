package com.facilio.bmsconsole.instant.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.PublishData;
import com.facilio.bmsconsole.context.PublishMessage;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.bmsconsole.util.IoTMessageAPI.IotCommandType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.InstantJob;

public class PublishedDataCheckerJob extends InstantJob {
	
	private static final long THREAD_SLEEP_BUFFER = 20000; //In Millis
	private static final int MAX_RETRIES = 5;
	private static final Logger LOGGER = LogManager.getLogger(PublishedDataCheckerJob.class.getName());
	
	@Override
	public void execute(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		Thread.sleep(THREAD_SLEEP_BUFFER); //Wait at first for ack
		
		PublishData data = (PublishData) context.get(FacilioConstants.ContextNames.PUBLISH_DATA);
		LOGGER.debug(data);
		if (data.getCommandEnum() == IotCommandType.PING) {
			checkPingStatus(data);
			return;
		}
		
		Map<Long, PublishMessage> msgMap = data.getMessages().stream().collect(Collectors.toMap(PublishMessage::getId, Function.identity()));
		checkPublishedMsg(data, msgMap, context);
		handleSuccessFailure(data, msgMap, context);
	}
	
	private void checkPingStatus(PublishData pingData){
		try {
			PublishData data = IoTMessageAPI.getPublishData(pingData.getId(), true);
			if (data.getPingAckTime() == -1) {
				LOGGER.info("Agent not active. Controller Id: " + data.getControllerId() + ", Data Id: " + data.getId());
				JSONObject msgData = data.getMessages().get(0).getData();
				msgData.put("isPing", true);
				msgData.put("pingAgent", pingData.getMessages().get(0).getData().get("pingAgent"));
				IoTMessageAPI.handlePublishMessageFailure(data);
			}
		}
		catch (Exception e) {
			LOGGER.error("Error while handling publish message failure", e);
		}
	}
	
	public void checkPublishedMsg(PublishData data, Map<Long, PublishMessage> msgMap, FacilioContext context) throws Exception {
		FacilioModule module = ModuleFactory.getPublishMessageModule();
		List<FacilioField> fields = FieldFactory.getPublishMessageFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField parentIdField = fieldMap.get("parentId");
		
		List<FacilioField> selectFields = new ArrayList<>();
		selectFields.add(fieldMap.get("id"));
		selectFields.add(fieldMap.get("acknowledgeTime"));
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(selectFields)
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(parentIdField, String.valueOf(data.getId()), PickListOperators.IS))
														;
		
		int retries = 0;
		long timeOut = (long) context.getOrDefault(ContextNames.TIMEOUT, -1l);
		timeOut = timeOut != -1 ? timeOut : THREAD_SLEEP_BUFFER;
				
		while (!msgMap.isEmpty()) {
			LOGGER.info("Msg Map for retry "+retries+" : "+msgMap);
			
			List<Map<String, Object>> props = new GenericSelectRecordBuilder(selectBuilder)
													.andCondition(CriteriaAPI.getIdCondition(msgMap.keySet(), module))
													.get();
			if (props != null && !props.isEmpty()) {
				for (Map<String, Object> prop : props) {
					Long id = (Long) prop.get("id");
					Long acknowledgeTime = (Long) prop.get("acknowledgeTime");
					
					if (acknowledgeTime != null) {
						msgMap.remove(id);
					}
				}
			}
			else {
				break;
			}
			
			if (++retries == MAX_RETRIES) {
				break;
			}
			
			try {
				IoTMessageAPI.publishMessagesDirectly(msgMap.values());
				Thread.sleep(timeOut);
			}
			catch (Exception e) {
				LOGGER.error("Error occurred during retrying publish message", e);
			}
		}
	}
	
	public void handleSuccessFailure (PublishData data, Map<Long, PublishMessage> msgMap, FacilioContext context) throws Exception {
		LOGGER.info("Msg Map : "+msgMap);
		if (msgMap.isEmpty()) {
			IoTMessageAPI.acknowdledgeData(data.getId(), "acknowledgeTime");
		}
		else {
			IoTMessageAPI.handlePublishMessageFailure(data);
		}
	}
	
}

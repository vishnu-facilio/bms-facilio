package com.facilio.bmsconsole.instant.jobs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.PublishData;
import com.facilio.bmsconsole.context.PublishMessage;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.InstantJob;

public class PublishedDataCheckerJob extends InstantJob {
	
	private static final long THREAD_SLEEP_BUFFER = 10000;
	private static final int MAX_RETRIES = 5;
	private static final Logger LOGGER = LogManager.getLogger(PublishedDataCheckerJob.class.getName());
	
	@Override
	public void execute(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		Thread.sleep(THREAD_SLEEP_BUFFER); //Wait at first for ack
		
		PublishData data = (PublishData) context.get(FacilioConstants.ContextNames.PUBLISH_DATA);
		Map<Long, PublishMessage> msgMap = data.getMessages().stream().collect(Collectors.toMap(PublishMessage::getId, Function.identity()));
		checkPublishedMsg(data, msgMap);
		handleSuccessFailure(data, msgMap, context);
	}
	
	public void checkPublishedMsg(PublishData data, Map<Long, PublishMessage> msgMap) throws Exception {
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
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(parentIdField, String.valueOf(data.getId()), PickListOperators.IS))
														;
		
		int retries = 0;
		while (!msgMap.isEmpty()) {
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
				Thread.sleep(THREAD_SLEEP_BUFFER);
			}
			catch (Exception e) {
				LOGGER.error("Error occurred during retrying publish message", e);
			}
		}
	}
	
	public void handleSuccessFailure (PublishData data, Map<Long, PublishMessage> msgMap, FacilioContext context) throws SQLException {
		String key = null;
		if (msgMap.isEmpty()) {
			IoTMessageAPI.acknowdledgeData(data.getId());
			key = FacilioConstants.ContextNames.PUBLISH_SUCCESS;
		}
		else {
			key = FacilioConstants.ContextNames.PUBLISH_FAILURE;
		}
		Consumer<PublishData> consumer = (Consumer<PublishData>) context.get(key);
		if (consumer != null) {
			consumer.accept(data);
		}
	}
}

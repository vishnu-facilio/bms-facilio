package com.facilio.events.tasker.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.events.context.EventToAlarmFieldMapping;
import com.facilio.events.util.EventAPI;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class EventToAlarmJob extends FacilioJob{

	private static Logger logger = Logger.getLogger(EventToAlarmJob.class.getName());
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobContext jc) 
	{
		if(AwsUtil.getConfig("enableeventjob").equals("true"))
		{
			try 
			{
				long orgId = AccountUtil.getCurrentOrg().getOrgId();
				GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.select(EventConstants.EventFieldFactory.getEventFields())
														.table("Event")
														.andCustomWhere("ORGID = ? AND EVENT_STATE = ? AND (INTERNAL_STATE = ? OR (INTERNAL_STATE = ? AND EVENT_RULE_ID IS NULL))", orgId, EventState.READY.getIntVal(), EventInternalState.THRESHOLD_DONE.getIntVal(), EventInternalState.FILTERED.getIntVal());
				List<Map<String, Object>> props = builder.get();
				for(Map<String, Object> prop : props)
				{
					alarm(orgId, prop);
				}
			} 
			catch (Exception e) 
			{
				logger.log(Level.SEVERE, "Exception while executing EventToAlarmJob :::"+e.getMessage(), e);
			}
		}
	}

	public static void alarm (long orgId, Map<String, Object> prop) throws Exception {
		EventContext event = null;
		List<EventToAlarmFieldMapping> fieldMappings = EventRulesAPI.getEventToAlarmFieldMappings(orgId);
		if(fieldMappings != null && !fieldMappings.isEmpty()) {
			for(EventToAlarmFieldMapping mapping : fieldMappings) {
				switch(mapping.getTypeEnum()) {
					case ADD_CONSTANT:
						if(mapping.getToField() != null && !mapping.getToField().isEmpty() && mapping.getConstantValue() != null && !mapping.getConstantValue().isEmpty()) {
							prop.put(mapping.getToField(), mapping.getConstantValue());
						}
						break;
					case FIELD_MAPPING:
						if(mapping.getFromField() != null && !mapping.getFromField().isEmpty()) {
							if(prop.containsKey(mapping.getFromField())) {
								Object fieldValue = prop.get(mapping.getFromField());
								if(mapping.getMappingPairsJson().containsKey(fieldValue)) {
									prop.put(mapping.getToField(), mapping.getMappingPairsJson().get(fieldValue));
								}
							}
						}
						break;
				}
			}
		}
		event = FieldUtil.getAsBeanFromMap(prop, EventContext.class);
		if(event.getSeverity().equals(FacilioConstants.Alarm.INFO_SEVERITY)) {
			event.setEventState(EventState.IGNORED);
		}
		else {
			GenericSelectRecordBuilder eventSelectBuilder = new GenericSelectRecordBuilder()
					.select(EventConstants.EventFieldFactory.getEventFields())
					.table("Event")
					.limit(1)
					.andCustomWhere("ORGID = ? AND MESSAGE_KEY = ? AND ALARM_ID IS NOT NULL", orgId, event.getMessageKey())
					.orderBy("CREATED_TIME DESC");

			List<Map<String, Object>> props1 = eventSelectBuilder.get();
			boolean createAlarm = true;
			Long entityId = null;
			if(props1 != null && !props1.isEmpty())
			{
				createAlarm = false;
				
				long alarmId = (long) props1.get(0).get("alarmId");
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				List<FacilioField> alarmFields = modBean.getAllFields(FacilioConstants.ContextNames.ALARM);
				
				SelectRecordsBuilder<AlarmContext> builder = new SelectRecordsBuilder<AlarmContext>()
						.table("Alarms")
						.moduleName(FacilioConstants.ContextNames.ALARM)
						.beanClass(AlarmContext.class)
						.select(alarmFields)
						.maxLevel(0);
				builder.andCustomWhere("Alarms.ID = ?", alarmId);

				List<AlarmContext> alarms = builder.get();
				long severityId = alarms.get(0).getSeverity().getId();
				if(AlarmAPI.getAlarmSeverity(severityId).getSeverity().equals("Clear"))
				{
					createAlarm = true;
					entityId = alarms.get(0).getEntityId();
				}
			}
			else
			{
				createAlarm = true;
			}
			
			if(!createAlarm)
			{
				//TODO update alarm
				long alarmId = (long) props1.get(0).get("alarmId");
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				List<FacilioField> alarmFields = modBean.getAllFields(FacilioConstants.ContextNames.ALARM);
				
				JSONObject alarm = new JSONObject();
				JSONArray ids = new JSONArray();
				ids.add(alarmId);
				alarm.put("severityString", event.getSeverity());
				alarm.put("orgId", event.getOrgId());

				JSONObject content = new JSONObject();
				content.put("alarm", alarm);
				content.put("id", ids);

				Map<String, String> headers = new HashMap<>();
				headers.put("Content-Type","application/json");
				String server = AwsUtil.getConfig("servername");
				String url = "http://" + server + "/internal/updateAlarmFromEvent";
				AwsUtil.doHttpPost(url, headers, null, content.toJSONString());

				event.setAlarmId(alarmId);
				event.setEventState(EventState.ALARM_UPDATED);
			}
			else
			{
				JSONObject json = new JSONObject();
				json.put("orgId", event.getOrgId());
				json.put("source", event.getSource());
				json.put("node", event.getNode());
				json.put("subject", event.getEventMessage());
				json.put("description", event.getDescription());
				json.put("severityString", event.getSeverity());
				json.put("alarmPriority", event.getPriority());
				json.put("alarmClass", event.getAlarmClass());
				json.put("state", event.getState());
				json.put("alarmType", 6);
				if(entityId != null)
				{
					json.put("entityId", entityId);
				}

				if(event.getAssetId() != -1) {
					JSONObject asset = new JSONObject();
					asset.put("id", event.getAssetId());
					json.put("asset", asset);
				}

				JSONObject additionalInfo = event.getAdditionInfo();
				if(additionalInfo != null && additionalInfo.size() > 0) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					List<FacilioField> alarmFields = modBean.getAllFields(FacilioConstants.ContextNames.ALARM);

					JSONObject data = new JSONObject();
					for(FacilioField field : alarmFields) {
						if(additionalInfo.containsKey(field.getName())) {
							if(field.isDefault()) {
								json.put(field.getName(), additionalInfo.remove(field.getName()));
							}
							else {
								data.put(field.getName(), additionalInfo.remove(field.getName()));
							}
						}
					}
					if(data.size() > 0) {
						json.put("data", data);
					}
				}
				json.put("additionInfo", additionalInfo);

				JSONObject content = new JSONObject();
				content.put("alarm", json);

				Map<String, String> headers = new HashMap<>();
				headers.put("Content-Type","application/json");
				String server = AwsUtil.getConfig("servername");
				String url = "http://" + server + "/internal/addAlarm";

				JSONParser parser = new JSONParser();
				String response = AwsUtil.doHttpPost(url, headers, null, content.toJSONString());
				System.out.println(response);
				JSONObject res = (JSONObject) parser.parse(response);
				event.setAlarmId((long) res.get("alarmId"));
				event.setEventState(EventState.ALARM_CREATED);
			}
		}
		event.setInternalState(EventInternalState.COMPLETED);
		EventAPI.updateEvent(event, orgId);
	}
}

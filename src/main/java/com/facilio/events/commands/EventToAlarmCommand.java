package com.facilio.events.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.events.context.EventToAlarmFieldMapping;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class EventToAlarmCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EventContext event = (EventContext) context.get(EventConstants.EventContextNames.EVENT);
		if(event.getEventStateEnum() != EventState.IGNORED) {
			doFieldMapping(event);
			if(event.getSeverity().equals(FacilioConstants.Alarm.INFO_SEVERITY)) {
				event.setEventState(EventState.IGNORED);
			}
			else {
				long orgId = AccountUtil.getCurrentOrg().getOrgId();
				GenericSelectRecordBuilder eventSelectBuilder = new GenericSelectRecordBuilder()
						.select(EventConstants.EventFieldFactory.getEventFields())
						.table("Event")
						.limit(1)
						.andCustomWhere("ORGID = ? AND MESSAGE_KEY = ? AND ALARM_ID IS NOT NULL", orgId, event.getMessageKey())
						.orderBy("CREATED_TIME DESC");

				List<Map<String, Object>> props = eventSelectBuilder.get();
				boolean createAlarm = true;
				long entityId = -1;
				if(props != null && !props.isEmpty())
				{
					createAlarm = false;
					long alarmId = (long) props.get(0).get("alarmId");
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
				else {
					createAlarm = true;
				}
				
				if(!createAlarm) {
					//TODO update alarm
					long alarmId = (long) props.get(0).get("alarmId");
					updateAlarm(alarmId, event);
				}
				else {
					addAlarm(entityId, event);
				}
			}
			event.setInternalState(EventInternalState.COMPLETED);
			context.put(EventConstants.EventContextNames.EVENT, event);
		}
		return false;
	}
	
	private void doFieldMapping(EventContext event) throws Exception {
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		Map<String, Object> eventProp = FieldUtil.getAsProperties(event);
		List<EventToAlarmFieldMapping> fieldMappings = EventRulesAPI.getEventToAlarmFieldMappings(orgId);
		if(fieldMappings != null && !fieldMappings.isEmpty()) {
			for(EventToAlarmFieldMapping mapping : fieldMappings) {
				switch(mapping.getTypeEnum()) {
					case ADD_CONSTANT:
						if(mapping.getToField() != null && !mapping.getToField().isEmpty() && mapping.getConstantValue() != null && !mapping.getConstantValue().isEmpty()) {
							eventProp.put(mapping.getToField(), mapping.getConstantValue());
						}
						break;
					case FIELD_MAPPING:
						if(mapping.getFromField() != null && !mapping.getFromField().isEmpty()) {
							if(eventProp.containsKey(mapping.getFromField())) {
								Object fieldValue = eventProp.get(mapping.getFromField());
								if(mapping.getMappingPairsJson().containsKey(fieldValue)) {
									eventProp.put(mapping.getToField(), mapping.getMappingPairsJson().get(fieldValue));
								}
							}
						}
						break;
				}
			}
		}
		event = FieldUtil.getAsBeanFromMap(eventProp, EventContext.class);
	}
	
	private void updateAlarm(long alarmId, EventContext event) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> alarmFields = modBean.getAllFields(FacilioConstants.ContextNames.ALARM);
		
		JSONObject alarm = new JSONObject();
		JSONArray ids = new JSONArray();
		ids.add(alarmId);
		alarm.put("severityString", event.getSeverity());
		alarm.put("orgId", event.getOrgId());
		
		if (event.getAdditionInfo() != null) {
			Long sourceType = (Long) FieldUtil.castOrParseValueAsPerType(FieldType.NUMBER, event.getAdditionInfo().get("sourceType"));
			if (sourceType != null && sourceType == SourceType.THRESHOLD_ALARM.getIntVal()) {
				alarm.put("sourceType", sourceType);
				alarm.put("startTime", event.getAdditionInfo().get("startTime"));
				alarm.put("endTime", event.getAdditionInfo().get("endTime"));
				alarm.put("readingMessage", event.getAdditionInfo().get("readingMessage"));
			}
		}

		JSONObject content = new JSONObject();
		content.put("alarmInfo", alarm);
		content.put("id", ids);

		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type","application/json");
		String server = AwsUtil.getConfig("servername");
		String url = "http://" + server + "/internal/updateAlarmFromEvent";
		AwsUtil.doHttpPost(url, headers, null, content.toJSONString());

		event.setAlarmId(alarmId);
		event.setEventState(EventState.ALARM_UPDATED);
	}
	
	private void addAlarm(long entityId, EventContext event) throws Exception {
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
		if(entityId != -1)
		{
			json.put("entityId", entityId);
		}

		if(event.getResourceId() != -1) {
			JSONObject resource = new JSONObject();
			resource.put("id", event.getResourceId());
			json.put("resource", resource);
		}

		JSONObject additionalInfo = event.getAdditionInfo();
		if(additionalInfo != null && additionalInfo.size() > 0) {
			json.putAll(additionalInfo);
		}

		JSONObject content = new JSONObject();
		content.put("alarmInfo", json);

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

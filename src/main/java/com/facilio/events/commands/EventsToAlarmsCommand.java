package com.facilio.events.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.AlarmContext.AlarmType;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.events.context.EventToAlarmFieldMapping;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.google.common.base.Strings;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

;

public class EventsToAlarmsCommand implements Command {
	private static final Logger LOGGER = LogManager.getLogger(EventsToAlarmsCommand.class.getName());
	private long getAlarmId (EventContext event) throws Exception {
		if (event.getAlarmId() == -1) {
			FacilioModule module = EventConstants.EventModuleFactory.getEventModule();
			List<FacilioField> fields = EventConstants.EventFieldFactory.getEventFields();
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			FacilioField messageKeyField = fieldMap.get("messageKey");
			FacilioField alarmIdField = fieldMap.get("alarmId");
			FacilioField createdTimeField = fieldMap.get("createdTime");
			
			setMessageKey(event);
			GenericSelectRecordBuilder eventSelectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(module.getTableName())
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCondition(CriteriaAPI.getCondition(messageKeyField, event.getMessageKey(), StringOperators.IS))
					.andCondition(CriteriaAPI.getCondition(alarmIdField, CommonOperators.IS_NOT_EMPTY))
//					.andCondition(CriteriaAPI.getCondition(createdTimeField, String.valueOf(event.getCreatedTime()), NumberOperators.LESS_THAN_EQUAL))
					.orderBy("CREATED_TIME DESC")
					.limit(1)
					;

			List<Map<String, Object>> props = eventSelectBuilder.get();
			
			if(props != null && !props.isEmpty())
			{
				if(props.get(0).get("createdTime") != null) {
					long createdTime = (long) props.get(0).get("createdTime");
					if(createdTime > event.getCreatedTime()) {
						LOGGER.error("this alarm came before current alarm -- "+props);
					}
				}
				
				long alarmId = (long) props.get(0).get("alarmId");
				return alarmId;
			}
			else {
				return -1; //Create new Alarm
			}
		}
		return event.getAlarmId();
	}
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<EventContext> events = (List<EventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST);

		if (CollectionUtils.isNotEmpty(events)) {
			for (EventContext event : events) {
				processEventToAlarm(event);
			}
		}
		return false;
	}

	private void processEventToAlarm (EventContext event) throws Exception {
		if(event.getEventStateEnum() != EventState.IGNORED) {
			doFieldMapping(event);
			if(event.getSeverity().equals(FacilioConstants.Alarm.INFO_SEVERITY)) {
				event.setEventState(EventState.IGNORED);
			}
			else {
				long alarmId = getAlarmId(event);
				LOGGER.debug("Alarm ID for event : "+event.getId()+" : "+alarmId);
				boolean createAlarm = true;
				long entityId = -1;
				if (alarmId != -1) {
					createAlarm = false;
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
					if(AlarmAPI.getAlarmSeverity(severityId).getSeverity().equals(FacilioConstants.Alarm.CLEAR_SEVERITY))
					{
						createAlarm = true;
						entityId = alarms.get(0).getEntityId();
					}
				}
				if(!createAlarm) {
					//TODO update alarm
					updateAlarm(alarmId, event);
				}
				else {
					addAlarm(entityId, event);
				}
			}
			event.setInternalState(EventInternalState.COMPLETED);
		}
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
	
	private void setMessageKey (EventContext event) {
		Long sourceType = (Long) FieldUtil.castOrParseValueAsPerType(FieldType.NUMBER, event.getAdditionInfo().get("sourceType"));
		if (sourceType != null) {
			SourceType type = SourceType.getType(sourceType.intValue());
			String msgKey = null;
			switch (type) {
				case THRESHOLD_ALARM:
				case ML_ALARM:
					msgKey = event.getResourceId()+"_"+event.getAdditionInfo().get("ruleId");
					break;
				case ANOMALY_ALARM:
					msgKey = event.getResourceId()+"_"+Strings.nullToEmpty(event.getCondition());
					break;
				default:
					msgKey = Strings.nullToEmpty(event.getSource())+"_"+Strings.nullToEmpty(event.getCondition())+"_"+(event.getControllerId() == -1 ? "" : event.getControllerId());
					break;
			}
			event.setMessageKey(msgKey);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updateAlarm(long alarmId, EventContext event) throws Exception {
		JSONObject alarm = new JSONObject();
		List<Long> ids = new ArrayList<>();
		ids.add(alarmId);
		AlarmContext oldAlarmContext = AlarmAPI.getAlarm(alarmId);
		alarm.put("severityString", event.getSeverity());
		alarm.put("subject", event.getEventMessage());
		alarm.put("orgId", event.getOrgId());
		alarm.put("modifiedTime", event.getCreatedTime());
		
		if (event.getComment() != null && !event.getComment().isEmpty()) {
			alarm.put("comment", event.getComment());
		}
		
		if (event.getAdditionInfo() != null) {
			Long sourceType = (Long) FieldUtil.castOrParseValueAsPerType(FieldType.NUMBER, event.getAdditionInfo().get("sourceType"));
			if (sourceType != null) {
				SourceType type = SourceType.getType(sourceType.intValue());
				switch (type) {
					case THRESHOLD_ALARM:
					case ANOMALY_ALARM:
						alarm.put("sourceType", sourceType);
						alarm.put("startTime", event.getAdditionInfo().get("startTime"));
						alarm.put("endTime", event.getAdditionInfo().get("endTime"));
						alarm.put("readingMessage", event.getAdditionInfo().get("readingMessage"));
						alarm.put("readingDataId", event.getAdditionInfo().get("readingDataId"));
						alarm.put("readingVal", event.getAdditionInfo().get("readingVal"));
						alarm.put("ruleId", event.getAdditionInfo().get("ruleId"));
						alarm.put("metricValues", event.getAdditionInfo().get("metricValues"));
						
//						appendMessage(oldAlarmContext, event, alarm);
						
						break;
					default:
						alarm.put("sourceType", sourceType);
						break;
				}
			}
			alarm.put("autoClear", event.getAdditionInfo().get("autoClear"));
		}

//		JSONObject content = new JSONObject();
//		content.put("alarmInfo", alarm);
//		content.put("id", ids);
//
//		Map<String, String> headers = new HashMap<>();
//		headers.put("Content-Type","application/json");
//		String server = AwsUtil.getConfig("servername");
//		String url = "http://" + server + "/internal/updateAlarmFromEvent";
//		AwsUtil.doHttpPost(url, headers, null, content.toJSONString());
		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
		bean.updateAlarmFromJson(alarm, ids);

		event.setAlarmId(alarmId);
		event.setEventState(EventState.ALARM_UPDATED);
	}
	
	private void appendMessage(AlarmContext oldAlarmContext,EventContext event,JSONObject alarm) {
		String possibleCauseString = "";
		if(oldAlarmContext != null && oldAlarmContext.getPossibleCauses() != null) {
			possibleCauseString += oldAlarmContext.getPossibleCauses();
		}
		if(event.getAdditionInfo().get("possibleCauses") != null && !possibleCauseString.contains(event.getAdditionInfo().get("possibleCauses").toString())) {
			possibleCauseString = possibleCauseString + "\n" + event.getAdditionInfo().get("possibleCauses").toString();
		}
		if(!possibleCauseString.isEmpty()) {
			alarm.put("possibleCauses", possibleCauseString);
		}
		
		String recommendation = "";
		if(oldAlarmContext != null && oldAlarmContext.getRecommendation() != null) {
			recommendation += oldAlarmContext.getRecommendation();
		}
		if(event.getAdditionInfo().get("recommendation") != null && !recommendation.contains(event.getAdditionInfo().get("recommendation").toString())) {
			recommendation = recommendation + "\n" + event.getAdditionInfo().get("recommendation").toString();
		}
		if(!recommendation.isEmpty()) {
			alarm.put("recommendation", recommendation);
		}
		
		String problem = "";
		if(oldAlarmContext != null && oldAlarmContext.getProblem() != null) {
			problem += oldAlarmContext.getProblem();
		}
		if(event.getAdditionInfo().get("problem") != null && !problem.contains(event.getAdditionInfo().get("problem").toString())) {
			problem = problem + "\n" + event.getAdditionInfo().get("problem").toString();
		}
		if(!problem.isEmpty()) {
			alarm.put("problem", problem);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addAlarm(long entityId, EventContext event) throws Exception {
		JSONObject json = new JSONObject();
		json.put("orgId", event.getOrgId());
		json.put("condition", event.getCondition());
		json.put("source", event.getSource());
		json.put("subject", event.getEventMessage());
		json.put("description", event.getDescription());
		json.put("severityString", event.getSeverity());
		json.put("alarmPriority", event.getPriority());
		json.put("alarmClass", event.getAlarmClass());
		json.put("state", event.getState());
		json.put("createdTime", event.getCreatedTime());
		
		if (event.getComment() != null && !event.getComment().isEmpty()) {
			json.put("comment", event.getComment());
		}
		
		if (event.getSiteId() != -1) {
			json.put("siteId", event.getSiteId());
		}
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

		if (json.get("alarmType") == null) {
			AlarmType alarmType = AlarmAPI.getAlarmTypeFromResource(event.getResourceId());
			if (alarmType != null) {
				json.put("alarmType", alarmType.getIntVal());
			}
		}
		
//		JSONObject content = new JSONObject();
//		content.put("alarmInfo", json);
//
//		Map<String, String> headers = new HashMap<>();
//		headers.put("Content-Type","application/json");
//		String server = AwsUtil.getConfig("servername");
//		String url = "http://" + server + "/internal/addAlarm";
//
//		JSONParser parser = new JSONParser();
//		String response = AwsUtil.doHttpPost(url, headers, null, content.toJSONString());
//		System.out.println(response);
//		JSONObject res = (JSONObject) parser.parse(response);
		
		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
		AlarmContext alarm = bean.processAlarm(json);
		
		event.setAlarmId(alarm.getId());
		event.setEventState(EventState.ALARM_CREATED);
		
	}
}

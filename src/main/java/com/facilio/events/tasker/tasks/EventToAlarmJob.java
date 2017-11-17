package com.facilio.events.tasker.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.events.context.EventToAlarmFieldMapping;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class EventToAlarmJob extends FacilioJob{

	private static Logger logger = Logger.getLogger(EventToAlarmJob.class.getName());
	
	@Override
	public void execute(JobContext jc) 
	{
		if(AwsUtil.getConfig("enableeventjob").equals("true"))
		{
			try 
			{
				long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
				GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.select(EventConstants.EventFieldFactory.getEventFields())
														.table("Event")
														.andCustomWhere("ORGID = ? AND STATE = ? AND (INTERNAL_STATE = ? OR (INTERNAL_STATE = ? AND EVENT_RULE_ID IS NULL))", orgId, EventState.READY.getIntVal(), EventInternalState.THRESHOLD_DONE.getIntVal(), EventInternalState.FILTERED.getIntVal());
				List<Map<String, Object>> props = builder.get();
				for(Map<String, Object> prop : props)
				{
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
					GenericSelectRecordBuilder eventSelectBuilder = new GenericSelectRecordBuilder()
																.select(EventConstants.EventFieldFactory.getEventFields())
																.table("Event")
																.limit(1)
																.andCustomWhere("ORGID = ? AND MESSAGE_KEY = ? AND ALARM_ID IS NOT NULL", orgId, event.getMessageKey())
																.orderBy("CREATED_TIME DESC");

					List<Map<String, Object>> props1 = eventSelectBuilder.get();
					if(props1 != null && !props1.isEmpty())
					{
						//TODO update alarm
						long alarmId = (long) props1.get(0).get("alarmId");
						JSONObject json = new JSONObject();
						JSONArray ids = new JSONArray();
						ids.add(alarmId);
						json.put("priority", event.getSeverity());
						json.put("id", ids);
						json.put("orgId", event.getOrgId());
						//Update severity
						
						Map<String, String> headers = new HashMap<>();
						headers.put("Content-Type","application/json");
						String server = AwsUtil.getConfig("servername");
						String url = "http://" + server + "/internal/updateAlarmPriority";
						AwsUtil.doHttpPost(url, headers, null, json.toJSONString());
						
						event.setAlarmId(alarmId);
					}
					else
					{
						JSONObject json = new JSONObject();
						json.put("orgId", event.getOrgId());
						json.put("source", event.getSource());
						json.put("node", event.getNode());
						//json.put("status", event.getState());
						json.put("subject", event.getEventType());
						//json.put("priority", event.getEventType());
						json.put("description", event.getDescription());
						
						JSONObject content = new JSONObject();
						content.put("alarm", json);
						
						Map<String, String> headers = new HashMap<>();
						headers.put("Content-Type","application/json");
						String server = AwsUtil.getConfig("servername");
						String url = "http://" + server + "/internal/addAlarm";
						
						JSONParser parser = new JSONParser();
						JSONObject response = (JSONObject) parser.parse(AwsUtil.doHttpPost(url, headers, null, content.toJSONString())); 
						event.setAlarmId((long) response.get("alarmId"));
					}
					
					event.setInternalState(EventInternalState.COMPLETED);
					event.setState(EventState.PROCESSED);
					prop = FieldUtil.getAsProperties(event);
					
					GenericUpdateRecordBuilder updatebuilder = new GenericUpdateRecordBuilder()
															.table("Event")
															.fields(EventConstants.EventFieldFactory.getEventFields())
															.andCustomWhere("ORGID = ? AND ID = ?", orgId, event.getId());
					updatebuilder.update(prop);
				}
			} 
			catch (Exception e) 
			{
				logger.log(Level.SEVERE, "Exception while executing EventToAlarmJob :::"+e.getMessage(), e);
			}
		}
	}
}

package com.facilio.events.tasker.tasks;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.transaction.FacilioConnectionPool;

public class EventToAlarmJob extends FacilioJob{

	private static Logger logger = Logger.getLogger(EventToAlarmJob.class.getName());
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public void execute(JobContext jc) 
	{
		if(AwsUtil.getConfig("enableeventjob").equals("true"))
		{
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) 
			{
				GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.connection(conn)
														.select(EventConstants.getEventFields())
														.table("Event")
														.andCustomWhere("ORGID = ? AND STATE = ? AND (INTERNAL_STATE = 4 OR (INTERNAL_STATE = 2 AND EVENT_RULE_ID IS NULL))", jc.getOrgId(), "Ready");
				List<Map<String, Object>> props = builder.get();
				for(Map<String, Object> prop : props)
				{
					EventContext event = FieldUtil.getAsBeanFromMap(prop, EventContext.class);
					GenericSelectRecordBuilder mappingbuilder = new GenericSelectRecordBuilder()
												.connection(conn)
												.select(EventConstants.getEventMappingRuleFields())
												.table("Event_Field_Mapping")
												.andCustomWhere("ORGID = ?", jc.getOrgId())
												.orderBy("MAPPING_ORDER");
					List<Map<String, Object>> mappingprops = mappingbuilder.get();
					for(Map<String, Object> mappingprop : mappingprops)
					{
						int mappingType = (Integer) mappingprop.get("mappingType");
						if(mappingType == 1)
						{
							if(prop.containsKey((String)mappingprop.get("fromField")))
							{
								String value = (String) prop.get((String)mappingprop.get("fromField"));
								
								JSONParser parser = new JSONParser();
								JSONObject mappingPairs = (JSONObject) parser.parse((String) mappingprop.get("mappingPairs"));
								if(mappingPairs.containsKey(value))
								{
									prop.put((String)mappingprop.get("toField"), mappingPairs.get(value));
								}
							}
						}
						else if(mappingType == 2)
						{
							prop.put((String)mappingprop.get("toField"), mappingprop.get("constantValue"));
						}
						event = FieldUtil.getAsBeanFromMap(prop, EventContext.class);
					}
					
					GenericSelectRecordBuilder builder1 = new GenericSelectRecordBuilder()
							.connection(conn)
							.select(EventConstants.getEventFields())
							.table("Event")
							.limit(1)
							.andCustomWhere("ORGID = ? AND MESSAGE_KEY = ? AND ALARM_ID IS NOT NULL", jc.getOrgId(), event.getMessageKey())
							.orderBy("CREATED_TIME DESC");

					List<Map<String, Object>> props1 = builder1.get();
					if(props1.size() > 0)
					{
						//TODO update alarm
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
						AwsUtil.doHttpPost(url, headers, null, content.toString());
					}
					
					event.setInternalState(5);
					event.setState("Processed");
					prop = FieldUtil.getAsProperties(event);
					
					GenericUpdateRecordBuilder updatebuilder = new GenericUpdateRecordBuilder()
															.connection(conn)
															.table("Event")
															.fields(EventConstants.getEventFields())
															.andCustomWhere("ORGID = ?", OrgInfo.getCurrentOrgInfo().getOrgid());
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

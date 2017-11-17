package com.facilio.events.commands;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class AddOrUpdateAlarmCommand implements Command {

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public boolean execute(Context context) throws Exception {
		boolean ignoreEvent = (Boolean) context.get(EventConstants.EventContextNames.IGNORE_EVENT);
		if(!ignoreEvent)
		{
			EventContext event = (EventContext) context.get(EventConstants.EventContextNames.EVENT);
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) 
			{
				GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.connection(conn)
														.select(EventConstants.EventFieldFactory.getEventFields())
														.table("Event")
														.andCustomWhere("ORGID = ? AND MESSAGE_KEY = ?", 1, event.getMessageKey());	//Org Id
				
				List<Map<String, Object>> props = builder.get();
				if(props.size() > 0)
				{
					//TODO update alarm
				}
				//else
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
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				throw e;
			}
		}
		return false;
	}
}

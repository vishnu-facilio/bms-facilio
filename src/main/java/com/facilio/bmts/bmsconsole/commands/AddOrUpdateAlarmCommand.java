package com.facilio.bmts.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmts.bmsconsole.context.EventContext;
import com.facilio.bmts.constants.BmtsConstants;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class AddOrUpdateAlarmCommand implements Command {

	@SuppressWarnings("deprecation")
	@Override
	public boolean execute(Context context) throws Exception {
		boolean ignoreEvent = (Boolean) context.get(BmtsConstants.IGNORE_EVENT);
		if(!ignoreEvent)
		{
			EventContext event = (EventContext) context.get(BmtsConstants.EVENT);
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) 
			{
				GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.connection(conn)
														.select(BmtsConstants.getEventFields())
														.table("Event")
														.andCustomWhere("ORGID = ? AND MESSAGE_KEY = ?", 1, event.getMessageKey());	//Org Id
				
				List<Map<String, Object>> props = builder.get();
				if(props.size() > 0)
				{
					//TODO update alarm
				}
				else
				{
					String server = AwsUtil.getConfig("servername");
					String url = "http://" + server + "/internal/addAlarm";
					String bodyContent = "{}";
					
			        AwsUtil.doHttpPost(url, null, null, bodyContent);
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

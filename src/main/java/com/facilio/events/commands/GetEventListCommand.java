package com.facilio.events.commands;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.events.context.EventContext;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class GetEventListCommand implements Command {

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<EventContext> events = new ArrayList<>();
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
					.connection(conn)
					.select(EventConstants.getEventFields())
					.table("Event")
					.andCustomWhere("Event.ORGID = ?", OrgInfo.getCurrentOrgInfo().getOrgid());
	
			List<Map<String, Object>> eventList = selectBuider.get();
			
			for(Map<String, Object> eventMap : eventList)
			{
				EventContext event = new EventContext();
				BeanUtils.populate(event, eventMap);
				events.add(event);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		context.put(EventConstants.EVENT_LIST, events);
		return false;
	}
}

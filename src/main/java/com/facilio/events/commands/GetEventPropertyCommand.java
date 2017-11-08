package com.facilio.events.commands;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventProperty;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class GetEventPropertyCommand implements Command {

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		EventProperty eventProperty = new EventProperty();
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
					.connection(conn)
					.select(EventConstants.getEventPropertyFields())
					.table("Event_Property")
					.andCustomWhere("Event_Property.ORGID = ?", OrgInfo.getCurrentOrgInfo().getOrgid());
	
			List<Map<String, Object>> eventProperties = selectBuider.get();
			BeanUtils.populate(eventProperty, eventProperties.get(0));
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		context.put(EventConstants.EVENT_PROPERTY, eventProperty);
		return false;
	}
}

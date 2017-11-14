package com.facilio.events.commands;

import java.sql.Connection;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.context.EventContext;
import com.facilio.fw.OrgInfo;
import com.facilio.events.constants.EventConstants;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class AddEventCommand implements Command {

	@SuppressWarnings("deprecation")
	@Override
	public boolean execute(Context context) throws Exception {
		EventContext event = (EventContext) context.get(EventConstants.EVENT);
		event.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
	    event.setCreatedTime(System.currentTimeMillis());
	    event.setState("Ready");
	    event.setInternalState(1);
		
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			Map<String, Object> props = FieldUtil.getAsProperties(event);
			
			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
																.connection(conn)
																.table("Event")
																.fields(EventConstants.getEventFields())
																.addRecord(props);
			
			builder.save();
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return false;
	}
}

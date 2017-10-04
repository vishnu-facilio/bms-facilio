package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.UserInfo;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddTicketActivityCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		TicketContext ticket = (TicketContext) context.get(FacilioConstants.ContextNames.TICKET);
		if(ticket != null) 
		{
			Long ticketId = ticket.getId();
			
			Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			
			Map<String, Object> map = new HashMap<>();
			map.put("ticketId", ticketId);
			map.put("modifiedTime", System.currentTimeMillis());
			map.put("modifiedBy", UserInfo.getCurrentUser().getOrgId());
			map.put("activityType", 1);
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.connection(conn)
					.table("Ticket_Activity")
					.fields(FieldFactory.getTicketActivityFields())
					.addRecord(map);
			
			//insertBuilder.save();
		}
		else 
		{
			throw new IllegalArgumentException("Ticket Object cannot be null");
		}
		return false;
	}
}

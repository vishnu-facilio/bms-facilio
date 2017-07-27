package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;

public class GetTicketCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long ticketId = (long) context.get(FacilioConstants.ContextNames.TICKET_ID);
		
		if( ticketId > 0) {
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			
			SelectRecordsBuilder<TicketContext> builder = new SelectRecordsBuilder<TicketContext>()
																.connection(conn)
																.dataTableName(dataTableName)
																.beanClass(TicketContext.class)
																.select(fields)
																.where("ticketId = ?", ticketId)
																.orderBy("ticketId");
			
			List<TicketContext> tickets = builder.get();
			if(tickets.size() > 0) {
				TicketContext ticket = tickets.get(0);
				context.put(FacilioConstants.ContextNames.TICKET, ticket);
				
				ticket.setTasks(TicketAPI.getRelatedTasks(ticket.getTicketId(), conn));
				ticket.setNotes(TicketAPI.getRelatedNotes(ticket.getTicketId(), conn));
				ticket.setAttachments(TicketAPI.getRelatedAttachments(ticket.getTicketId(), conn));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid Ticket ID : "+ticketId);
		}
		
		return false;
	}

}

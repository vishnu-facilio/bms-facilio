package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetTicketCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long ticketId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if( ticketId > 0) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			SelectRecordsBuilder<TicketContext> builder = new SelectRecordsBuilder<TicketContext>()
																.table(dataTableName)
																.moduleName(moduleName)
																.beanClass(TicketContext.class)
																.select(fields)
																.andCustomWhere("ID = ?", ticketId)
																.orderBy("ID");
			
			List<TicketContext> tickets = builder.get();
			if(tickets.size() > 0) {
				TicketContext ticket = tickets.get(0);
				context.put(FacilioConstants.ContextNames.TICKET, ticket);
				ticket.setAttachments(TicketAPI.getRelatedAttachments(ticket.getId()));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid Ticket ID : "+ticketId);
		}
		
		return false;
	}

}

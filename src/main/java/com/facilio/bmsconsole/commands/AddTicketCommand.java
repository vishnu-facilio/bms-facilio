package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class AddTicketCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		TicketContext ticket = (TicketContext) context.get(FacilioConstants.ContextNames.TICKET);
		
		if(ticket != null) {
			if(ticket.getSourceType() == -1)
			{
				ticket.setSourceType(TicketContext.SourceType.WEB_ORDER);
			}
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			
			if(ticket.getStatus() == null)
			{
				ticket.setStatus(TicketAPI.getStatus("Submitted"));
			}
			
			InsertRecordBuilder<TicketContext> builder = new InsertRecordBuilder<TicketContext>()
																.moduleName(moduleName)
																.table(dataTableName)
																.fields(fields);
																
			long ticketId = builder.insert(ticket);
			ticket.setId(ticketId);
			
			context.put(FacilioConstants.ContextNames.RECORD_ID, ticketId);
		}
		else {
			throw new IllegalArgumentException("Ticket Object cannot be null");
		}
		return false;
	}

}

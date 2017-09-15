package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;

public class AddTicketCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		TicketContext ticket = (TicketContext) context.get(FacilioConstants.ContextNames.TICKET);
		
		if(ticket != null) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			
			if(ticket.getStatus() == null)
			{
				ticket.setStatus(TicketAPI.getStatus(OrgInfo.getCurrentOrgInfo().getOrgid(), "Submitted"));
			}
			InsertRecordBuilder<TicketContext> builder = new InsertRecordBuilder<TicketContext>()
																.moduleName(moduleName)
																.dataTableName(dataTableName)
																.fields(fields)
																.connection(conn);
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

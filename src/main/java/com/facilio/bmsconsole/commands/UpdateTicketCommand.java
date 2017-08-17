package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;

public class UpdateTicketCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		TicketContext ticket = (TicketContext) context.get(FacilioConstants.ContextNames.TICKET);
		if(ticket != null) 
		{
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			UpdateRecordBuilder<TicketContext> builder = new UpdateRecordBuilder<TicketContext>()
														.moduleName(moduleName)
														.dataTableName(dataTableName)
														.connection(conn)
														.fields(fields)
														.where("ID = ?", ticket.getId());
			builder.update(ticket);
		}
		else 
		{
			throw new IllegalArgumentException("Ticket Object cannot be null");
		}
		return false;
	}

}

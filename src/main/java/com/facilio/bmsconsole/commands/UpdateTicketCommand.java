package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class UpdateTicketCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		TicketContext ticket = (TicketContext) context.get(FacilioConstants.ContextNames.TICKET);
		if(ticket != null) 
		{
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			UpdateRecordBuilder<TicketContext> builder = new UpdateRecordBuilder<TicketContext>()
														.moduleName(moduleName)
														.table(dataTableName)
														.fields(fields)
														.andCustomWhere("ID = ?", ticket.getId());
			builder.update(ticket);
		}
		else 
		{
			throw new IllegalArgumentException("Ticket Object cannot be null");
		}
		return false;
	}

}

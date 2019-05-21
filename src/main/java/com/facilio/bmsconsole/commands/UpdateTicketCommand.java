package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class UpdateTicketCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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

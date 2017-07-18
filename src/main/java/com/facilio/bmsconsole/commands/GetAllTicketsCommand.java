package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;

public class GetAllTicketsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		
		SelectRecordsBuilder<TicketContext> builder = new SelectRecordsBuilder<TicketContext>()
														.connection(conn)
														.dataTableName(dataTableName)
														.beanClass(TicketContext.class)
														.select(fields)
														.orderBy("ticketId");

		List<TicketContext> tickets = builder.get();
		context.put(FacilioConstants.ContextNames.TICKET_LIST, tickets);
		
		return false;
	}

}

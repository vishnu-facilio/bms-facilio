package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.TicketPriorityContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetTicketPriorityListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		
		SelectRecordsBuilder<TicketPriorityContext> builder = new SelectRecordsBuilder<TicketPriorityContext>()
														.table(dataTableName)
														.moduleName(moduleName)
														.beanClass(TicketPriorityContext.class)
														.select(fields)
														.orderBy("ID");
		List<TicketPriorityContext> statuses = builder.get();
		context.put(FacilioConstants.ContextNames.TICKET_PRIORITY_LIST, statuses);
		
		return false;
	}

}

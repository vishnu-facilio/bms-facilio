package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetTicketStatusListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		
//		SelectRecordsBuilder<TicketStatusContext> builder = new SelectRecordsBuilder<TicketStatusContext>()
//														.table(dataTableName)
//														.moduleName(moduleName)
//														.beanClass(TicketStatusContext.class)
//														.select(fields)
//														.orderBy("ID");
		List<TicketStatusContext> statuses = TicketAPI.getAllStatus(false);
		context.put(FacilioConstants.ContextNames.TICKET_STATUS_LIST, statuses);
		
		return false;
	}

}

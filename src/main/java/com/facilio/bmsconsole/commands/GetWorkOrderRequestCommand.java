package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;

public class GetWorkOrderRequestCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long workOrderRequestId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(workOrderRequestId > 0) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			
			SelectRecordsBuilder<WorkOrderRequestContext> builder = new SelectRecordsBuilder<WorkOrderRequestContext>()
																.connection(conn)
																.table(dataTableName)
																.moduleName(moduleName)
																.beanClass(WorkOrderRequestContext.class)
																.select(fields)
																.andCustomWhere("ID = ?", workOrderRequestId)
																.orderBy("ID");
			
			List<WorkOrderRequestContext> workOrderRequests = builder.get();
			if(workOrderRequests.size() > 0) {
				WorkOrderRequestContext workOrderRequest = workOrderRequests.get(0);
				
				context.put(FacilioConstants.ContextNames.WORK_ORDER_REQUEST, workOrderRequest);
				
				TicketAPI.loadRelatedModules(workOrderRequest.getTicket(), conn);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid Work Order Request ID : "+workOrderRequestId);
		}
		
		return false;
	}
	
}

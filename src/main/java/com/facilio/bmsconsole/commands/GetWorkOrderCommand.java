package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.ScheduleObjectAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;

public class GetWorkOrderCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long workOrderId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(workOrderId > 0) {
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			
			SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
																.connection(conn)
																.dataTableName(dataTableName)
																.beanClass(WorkOrderContext.class)
																.select(fields)
																.where("ID = ?", workOrderId)
																.orderBy("ID");
			
			List<WorkOrderContext> workOrders = builder.getAsBean();
			if(workOrders.size() > 0) {
				WorkOrderContext workOrder = workOrders.get(0);
				
				workOrder.getTicket().setSchedule(ScheduleObjectAPI.getScheduleObject(workOrder.getTicket().getScheduleId(), conn));
				context.put(FacilioConstants.ContextNames.WORK_ORDER, workOrder);
				
				workOrder.setTasks(WorkOrderAPI.getRelatedTasks(workOrderId, conn));
				workOrder.setNotes(WorkOrderAPI.getRelatedNotes(workOrderId, conn));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid Work Order ID : "+workOrderId);
		}
		
		return false;
	}

}

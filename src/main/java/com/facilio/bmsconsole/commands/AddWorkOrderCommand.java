package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.EventContext;
import com.facilio.bmsconsole.workflow.EventContext.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ActivityType;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;

public class AddWorkOrderCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		if(workOrder != null) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			workOrder.setCreatedTime(System.currentTimeMillis());
			
			updateTicketStatus(workOrder.getTicket(), conn);
			
			InsertRecordBuilder<WorkOrderContext> builder = new InsertRecordBuilder<WorkOrderContext>()
																.moduleName(moduleName)
																.dataTableName(dataTableName)
																.fields(fields)
																.connection(conn);
			long workOrderId = builder.insert(workOrder);
			workOrder.setId(workOrderId);
			context.put(FacilioConstants.ContextNames.RECORD, workOrder);
			context.put(FacilioConstants.ContextNames.RECORD_ID, workOrderId);
			context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventContext.EventType.CREATE);
		}
		else {
			throw new IllegalArgumentException("WorkOrder Object cannot be null");
		}
		return false;
	}

	private void updateTicketStatus(TicketContext ticket, Connection conn) throws Exception {
		TicketStatusContext status = ticket.getStatus();
		
		if(status.getStatus() == null) {
			status = TicketAPI.getStatus(OrgInfo.getCurrentOrgInfo().getOrgid(), status.getId());
		}
		
		if(ticket.getAssignedTo() != null && (status == null || !status.getStatus().equals("Assigned"))) {
			TicketContext updatedTicket = new TicketContext();
			updatedTicket.setStatus(TicketAPI.getStatus(OrgInfo.getCurrentOrgInfo().getOrgid(), "Assigned"));
			List<FacilioField> fields = new ArrayList<>();
			fields.add(((ModuleBean) BeanFactory.lookup("ModuleBean")).getField("status", FacilioConstants.ContextNames.TICKET));
			
			UpdateRecordBuilder<TicketContext> builder = new UpdateRecordBuilder<TicketContext>()
					.moduleName(FacilioConstants.ContextNames.TICKET)
					.table("Tickets")
					.connection(conn)
					.fields(fields)
					.andCustomWhere("ID = ?", ticket.getId());
			builder.update(updatedTicket);
		}
	}
}

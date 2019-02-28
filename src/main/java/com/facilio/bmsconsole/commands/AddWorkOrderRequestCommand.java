package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;

public class AddWorkOrderRequestCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderRequestContext workOrderRequest = (WorkOrderRequestContext) context.get(FacilioConstants.ContextNames.WORK_ORDER_REQUEST);
		if(workOrderRequest != null) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		//	Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			
			TicketAPI.validateSiteSpecificData(workOrderRequest);
			
			workOrderRequest.setCreatedTime(System.currentTimeMillis());
			workOrderRequest.setCreatedBy(AccountUtil.getCurrentUser());
			
			if(workOrderRequest.getRequestStatus() == -1) {
				workOrderRequest.setRequestStatus(WorkOrderRequestContext.RequestStatus.OPEN);
			}
			User requester = workOrderRequest.getRequester();
			if (requester == null || requester.getEmail() == null || "".equals(requester.getEmail())) {
				workOrderRequest.setRequester(AccountUtil.getCurrentUser());
			}
			TicketAPI.updateTicketStatus(workOrderRequest);
			TicketAPI.updateTicketStatus(workOrderRequest);
			
			InsertRecordBuilder<WorkOrderRequestContext> builder = new InsertRecordBuilder<WorkOrderRequestContext>()
																.moduleName(moduleName)
																.table(dataTableName)
																.withLocalId()
																.fields(fields);
			long workOrderId = builder.insert(workOrderRequest);
			workOrderRequest.setId(workOrderId);
			context.put(FacilioConstants.ContextNames.RECORD, workOrderRequest);
			context.put(FacilioConstants.ContextNames.RECORD_ID, workOrderId);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(workOrderId));
//			context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CREATE_WORK_REQUEST);
		}
		else {
			throw new IllegalArgumentException("WorkOrder Object cannot be null");
		}
		return false;
	}
	
}

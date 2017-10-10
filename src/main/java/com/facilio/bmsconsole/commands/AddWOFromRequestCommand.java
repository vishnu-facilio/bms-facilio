package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.workflow.WorkflowEventContext.EventType;
import com.facilio.constants.FacilioConstants;

public class AddWOFromRequestCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(EventType.APPROVE_WORK_ORDER_REQUEST == eventType && recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			String ids = StringUtils.join(recordIds, ",");
			Condition idCondition = new Condition();
			idCondition.setField(FieldFactory.getIdField(dataTableName));
			idCondition.setOperator(NumberOperators.EQUALS);
			idCondition.setValue(ids);
			
			SelectRecordsBuilder<WorkOrderRequestContext> builder = new SelectRecordsBuilder<WorkOrderRequestContext>()
																	.connection(((FacilioContext) context).getConnectionWithoutTransaction())
																	.table(dataTableName)
																	.moduleName(moduleName)
																	.beanClass(WorkOrderRequestContext.class)
																	.select(fields)
																	.andCondition(idCondition)
																	.orderBy("ID");
			
			List<WorkOrderRequestContext> workOrderRequests = builder.get();
			List<Long> woIds = new ArrayList<>();
			if(workOrderRequests != null && !workOrderRequests.isEmpty()) {
				for(WorkOrderRequestContext request : workOrderRequests) {
					woIds.add(addWorkOrder(request));
				}
			}
		}
		return false;
	}
	
	private long addWorkOrder(WorkOrderRequestContext request) throws Exception {
		WorkOrderContext wo = new WorkOrderContext();
		wo.setTicket(request.getTicket());
		wo.setRequester(request.getRequester());
		wo.setCreatedTime(System.currentTimeMillis());
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
		
		Command addWorkOrder = FacilioChainFactory.getAddWorkOrderChain();
		addWorkOrder.execute(context);
		
		return wo.getId();
	}

}

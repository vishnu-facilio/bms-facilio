package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddWOFromRequestCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ActivityType eventType = (ActivityType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(ActivityType.APPROVE_WORK_ORDER_REQUEST == eventType && recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			String ids = StringUtils.join(recordIds, ",");
			Condition idCondition = new Condition();
			idCondition.setField(FieldFactory.getIdField(module));
			idCondition.setOperator(NumberOperators.EQUALS);
			idCondition.setValue(ids);
			
			SelectRecordsBuilder<WorkOrderRequestContext> builder = new SelectRecordsBuilder<WorkOrderRequestContext>()
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
		BeanUtils.copyProperties(wo, request);
		wo.setCreatedTime(System.currentTimeMillis());
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
		context.put(FacilioConstants.ContextNames.INSERT_LEVEL, 2);
		if (wo.getAssignedTo() != null || wo.getAssignmentGroup() != null) {
			wo.setStatus(TicketAPI.getStatus("Assigned"));
			context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.ASSIGN_TICKET);
		}
		else {
			wo.setStatus(TicketAPI.getStatus("Submitted"));
		}
		
		Command addWorkOrder = FacilioChainFactory.getAddWorkOrderChain();
		addWorkOrder.execute(context);
		
		return wo.getId();
	}

}

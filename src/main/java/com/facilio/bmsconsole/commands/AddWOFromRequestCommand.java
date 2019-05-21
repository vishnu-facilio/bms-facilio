package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AddWOFromRequestCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(EventType.APPROVE_WORK_ORDER_REQUEST == eventType && recordIds != null && !recordIds.isEmpty()) {
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
			context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.ASSIGN_TICKET);
		}
		else {
			wo.setStatus(TicketAPI.getStatus("Submitted"));
		}
		
		Command addWorkOrder = TransactionChainFactory.getAddWorkOrderChain();
		addWorkOrder.execute(context);
		
		return wo.getId();
	}

}

package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;

public class UpdateWorkOrderCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(workOrder != null && recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			if(workOrder.getAssignedTo() != null || workOrder.getAssignmentGroup() != null) {
				workOrder.setStatus(TicketAPI.getStatus(OrgInfo.getCurrentOrgInfo().getOrgid(), "Assigned"));
			}
			
			if(workOrder.getStatus() != null) {
				if(workOrder.getStatus().getId() == TicketAPI.getStatus(OrgInfo.getCurrentOrgInfo().getOrgid(), "Closed").getId()) {
					workOrder.setActualWorkEnd(System.currentTimeMillis());
				}
			}
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			String ids = StringUtils.join(recordIds, ",");
			Condition idCondition = new Condition();
			idCondition.setField(FieldFactory.getIdField(module));
			idCondition.setOperator(NumberOperators.EQUALS);
			idCondition.setValue(ids);
			
			context.put(FacilioConstants.TicketActivity.OLD_TICKETS, getOldWOs(idCondition, fields));
			
			UpdateRecordBuilder<WorkOrderContext> updateBuilder = new UpdateRecordBuilder<WorkOrderContext>()
																		.moduleName(moduleName)
																		.table(dataTableName)
																		.fields(fields)
																		.andCondition(idCondition);
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updateBuilder.update(workOrder));
			
			if(ActivityType.ASSIGN_TICKET == (ActivityType)context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE) || ActivityType.CLOSE_WORK_ORDER == (ActivityType)context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE)) {
				SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
						.table(dataTableName)
						.moduleName(moduleName)
						.beanClass(WorkOrderContext.class)
						.select(fields)
						.andCustomWhere(module.getTableName()+".ID = ?", recordIds.get(0))
						.orderBy("ID");

				List<WorkOrderContext> workOrders = builder.get();
				context.put(FacilioConstants.ContextNames.RECORD, workOrders.get(0));
			}
		}
		return false;
	}
	
	private List<WorkOrderContext> getOldWOs(Condition ids, List<FacilioField> fields) throws Exception {
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
															.moduleName(FacilioConstants.ContextNames.WORK_ORDER)
															.beanClass(WorkOrderContext.class)
															.select(fields)
															.andCondition(ids)
															.orderBy("ID");

		return builder.get();
	}
	
}

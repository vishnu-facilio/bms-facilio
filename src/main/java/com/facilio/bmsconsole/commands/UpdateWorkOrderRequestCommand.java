package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

//import java.sql.Connection;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

;

public class UpdateWorkOrderRequestCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderRequestContext workOrderRequest = (WorkOrderRequestContext) context.get(FacilioConstants.ContextNames.WORK_ORDER_REQUEST);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(workOrderRequest != null && recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		//	Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			
			SelectRecordsBuilder<WorkOrderRequestContext> selectBuilder = new SelectRecordsBuilder<WorkOrderRequestContext>()
					.select(fields)
					.beanClass(WorkOrderRequestContext.class)
					.moduleName(FacilioConstants.ContextNames.WORK_ORDER_REQUEST)
					.andCondition(CriteriaAPI.getIdCondition(recordIds, module));
			
			List<WorkOrderRequestContext> oldWorkOrderRequests = selectBuilder.get(); 
			
			if (oldWorkOrderRequests == null || oldWorkOrderRequests.isEmpty()) {
				return false;
			}
			
			if (workOrderRequest.getSiteId() == -1) {
				TicketAPI.validateSiteSpecificData(workOrderRequest, oldWorkOrderRequests);
			} else if (AccountUtil.getCurrentSiteId() == -1){
				workOrderRequest.setAssignedTo(new User());
				workOrderRequest.getAssignedTo().setId(-1);
				workOrderRequest.setAssignmentGroup(new Group());
				workOrderRequest.getAssignmentGroup().setId(-1);
				workOrderRequest.setResource(new ResourceContext());
				workOrderRequest.getResource().setId(-1L);
			}

			String ids = StringUtils.join(recordIds, ",");
			Condition idCondition = new Condition();
			idCondition.setField(FieldFactory.getIdField(module));
			idCondition.setOperator(NumberOperators.EQUALS);
			idCondition.setValue(ids);
			
			if((workOrderRequest.getAssignedTo() != null && workOrderRequest.getAssignedTo().getId() > 0) || (workOrderRequest.getAssignmentGroup() != null && workOrderRequest.getAssignmentGroup().getId() > 0)) {
				workOrderRequest.setStatus(TicketAPI.getStatus("Assigned"));
			}
			
			UpdateRecordBuilder<WorkOrderRequestContext> updateBuilder = new UpdateRecordBuilder<WorkOrderRequestContext>()
																		.moduleName(moduleName)
																		.table(dataTableName)
																		.fields(fields)
																		.andCondition(idCondition);
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updateBuilder.update(workOrderRequest));
		}
		return false;
	}
}

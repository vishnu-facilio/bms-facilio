package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;

public class GetWorkOrderRequestCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long workOrderRequestId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(workOrderRequestId > 0) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			SelectRecordsBuilder<WorkOrderRequestContext> builder = new SelectRecordsBuilder<WorkOrderRequestContext>()
																.table(dataTableName)
																.moduleName(moduleName)
																.beanClass(WorkOrderRequestContext.class)
																.select(fields)
																.andCustomWhere(module.getTableName()+".ID = ?", workOrderRequestId)
																.orderBy("ID");
			
			List<WorkOrderRequestContext> workOrderRequests = builder.get();
			if(workOrderRequests.size() > 0) {
				WorkOrderRequestContext workOrderRequest = workOrderRequests.get(0);
				
				context.put(FacilioConstants.ContextNames.WORK_ORDER_REQUEST, workOrderRequest);
				
				TicketAPI.loadRelatedModules(workOrderRequest);
				TicketAPI.loadTicketLookups(Collections.singleton(workOrderRequest));
				if (workOrderRequest.getRequester() != null) {
					List<User> users = AccountUtil.getUserBean().getUsers(null, Collections.singletonList(workOrderRequest.getRequester().getId()));
					if (users != null && !users.isEmpty()) {
						workOrderRequest.setRequester(users.get(0));
					}
				}
			}
		}
		else {
			throw new IllegalArgumentException("Invalid Work Order Request ID : "+workOrderRequestId);
		}
		
		return false;
	}
	
}

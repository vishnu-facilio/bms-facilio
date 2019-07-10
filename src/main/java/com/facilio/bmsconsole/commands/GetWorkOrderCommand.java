package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class GetWorkOrderCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(GetWorkOrderCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long workOrderId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(workOrderId > 0) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
																.table(dataTableName)
																.moduleName(moduleName)
																.beanClass(WorkOrderContext.class)
																.select(fields)
																.andCustomWhere(module.getTableName()+".ID = ?", workOrderId)
																.orderBy("ID");
			
			List<WorkOrderContext> workOrders = builder.get();
			if(workOrders.size() > 0) {
				WorkOrderContext workOrder = workOrders.get(0);
				
				context.put(FacilioConstants.ContextNames.WORK_ORDER, workOrder);
				context.put(FacilioConstants.ContextNames.RECORD, workOrder);
				
				TicketAPI.loadRelatedModules(workOrder);
				TicketAPI.loadTicketLookups(Collections.singleton(workOrder));
				if (workOrder.getRequester() != null) {
					List<User> users = AccountUtil.getUserBean().getUsers(null, Collections.singletonList(workOrder.getRequester().getId()));
					if (users != null && !users.isEmpty()) {
						workOrder.setRequester(users.get(0));
					}
				}
				if (workOrder.getRequestedBy() != null) {
					List<User> users = AccountUtil.getUserBean().getUsers(null, Collections.singletonList(workOrder.getRequestedBy().getId()));
					if (users != null && !users.isEmpty()) {
						workOrder.setRequestedBy(users.get(0));
					}
				}
				Map<Long, List<TaskContext>> taskMap = workOrder.getTasks();
				if (taskMap != null) {
					List<TaskContext> tasks = taskMap.values().stream().flatMap(c -> c.stream()).collect(Collectors.toList());
					context.put(FacilioConstants.ContextNames.TASK_LIST, tasks);
				}

				if ((AccountUtil.getCurrentOrg().getId() == 146 || AccountUtil.getCurrentOrg().getId() == 155) && workOrder != null) {
					LOGGER.info("Workorder subject : "+ workOrder.getSubject()+"\n Description : "+workOrder.getDescription());
				}
			}
		}
		else {
			throw new IllegalArgumentException("Invalid Work Order ID : "+workOrderId);
		}
		
		return false;
	}

}

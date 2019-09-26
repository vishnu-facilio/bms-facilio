package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderContext.AllowNegativePreRequisite;
import com.facilio.bmsconsole.context.WorkOrderContext.PreRequisiteStatus;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;

public class ValidatePrerequisiteStatusForTaskUpdateCommnad extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		if(task != null) {
			List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
			if(recordIds != null && !recordIds.isEmpty()) {
				List<TaskContext> oldTasks = getTasks(recordIds);
				long parentId = oldTasks.get(0).getParentTicketId();
			    WorkOrderContext wo = WorkOrderAPI.getWorkOrder(parentId);
			    context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
				if(wo.isPrerequisiteEnabled() && ((AllowNegativePreRequisite.YES_WITH_APPROVAL.equals(wo.getAllowNegativePreRequisiteEnum()) && !PreRequisiteStatus.COMPLETED.equals(wo.getPreRequestStatusEnum()))
						||(AllowNegativePreRequisite.YES_WITH_WARNING.equals(wo.getAllowNegativePreRequisiteEnum()) && !(PreRequisiteStatus.COMPLETED_WITH_NEGATIVE.equals(wo.getPreRequestStatusEnum()) || PreRequisiteStatus.COMPLETED.equals(wo.getPreRequestStatusEnum())) ) )){
					throw new IllegalArgumentException("Prerequisite has to be completed before task updation");
				}
			}
		}
		
		return false;
	}
	private List<TaskContext> getTasks(List<Long> ids) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TASK);
		SelectRecordsBuilder<TaskContext> builder = new SelectRecordsBuilder<TaskContext>()
														.module(module)
														.beanClass(TaskContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.TASK))
														.andCondition(CriteriaAPI.getIdCondition(ids, module));
		
		List<TaskContext> tasks = builder.get();
		if(tasks != null && !tasks.isEmpty()) {
			return tasks;
		}
		return null;
	}
}

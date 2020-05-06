package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderContext.AllowNegativePreRequisite;
import com.facilio.bmsconsole.context.WorkOrderContext.PreRequisiteStatus;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;

public class ValidatePrerequisiteStatusForTaskUpdateCommnad extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		if(task != null) {
			List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
			if(recordIds != null && !recordIds.isEmpty()) {
				Map<Long, TaskContext> oldTasks = TicketAPI.getTaskMap(recordIds);
				context.put(FacilioConstants.ContextNames.TASK_MAP, oldTasks);
				long parentId = oldTasks.get(recordIds.get(0)).getParentTicketId();
				
			    WorkOrderContext wo = WorkOrderAPI.getWorkOrder(parentId, Collections.singletonList("moduleState"));
			    context.put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(wo));
			    
				if(wo.isPrerequisiteEnabled() && ((AllowNegativePreRequisite.YES_WITH_APPROVAL.equals(wo.getAllowNegativePreRequisiteEnum()) && !PreRequisiteStatus.COMPLETED.equals(wo.getPreRequestStatusEnum()))
						||(AllowNegativePreRequisite.YES_WITH_WARNING.equals(wo.getAllowNegativePreRequisiteEnum()) && !(PreRequisiteStatus.COMPLETED_WITH_NEGATIVE.equals(wo.getPreRequestStatusEnum()) || PreRequisiteStatus.COMPLETED.equals(wo.getPreRequestStatusEnum())) ) )){
					throw new IllegalArgumentException("Prerequisite has to be completed before task updation");
				}
			}
		}
		
		return false;
	}
}

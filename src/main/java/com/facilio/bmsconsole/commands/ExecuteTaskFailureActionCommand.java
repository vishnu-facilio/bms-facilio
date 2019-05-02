package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.serializable.SerializableCommand;

public class ExecuteTaskFailureActionCommand implements SerializableCommand {
	
	private static final long serialVersionUID = 1L;

	@Override
	public boolean execute(Context context) throws Exception {
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		EventType eventType = (EventType)context.get(FacilioConstants.ContextNames.EVENT_TYPE);
		if (eventType == EventType.CLOSE_WORK_ORDER && workOrder != null && recordIds != null && !recordIds.isEmpty()) {
			List<TaskContext> tasks = TicketAPI.getRelatedTasks(recordIds, false);
			if (tasks != null && !tasks.isEmpty()) {
				for(TaskContext task: tasks) {
					if (task.isFailed() && task.getActionId() != -1) {
						ActionContext action = ActionAPI.getAction(task.getActionId());
						Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
						action.executeAction(placeHolders, null, null, null);
					}
				}
			}
		}
		return false;
	}

}

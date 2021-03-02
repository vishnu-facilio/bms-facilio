package com.facilio.bmsconsole.commands;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.modules.FieldUtil;

public class ExecuteTaskFailureActionCommand extends FacilioCommand implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		List<WorkOrderContext> workorders = (List<WorkOrderContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		List<EventType> eventTypes = CommonCommandUtil.getEventTypes(context);
		if (eventTypes.contains(EventType.CLOSE_WORK_ORDER) && recordIds != null && !recordIds.isEmpty() && workorders != null) {
			Map<Long, WorkOrderContext> woMap = workorders.stream().collect(Collectors.toMap(WorkOrderContext::getId, Function.identity()));
			List<TaskContext> tasks = TicketAPI.getRelatedTasks(recordIds, false, true);
			if (tasks != null && !tasks.isEmpty()) {
				Map<Long, Map<String, Object>> woPropertiesMap = new HashMap();
				for(TaskContext task: tasks) {
					if (task.isFailed() && task.getActionId() != -1) {
						WorkOrderContext wo = woMap.get(task.getParentTicketId());
						task.setParentWo(wo);
						if (!woPropertiesMap.containsKey(wo.getId())) {
							woPropertiesMap.put(wo.getId(), FieldUtil.getAsProperties(wo));
						}
						ActionContext action = ActionAPI.getAction(task.getActionId());
						Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
						CommonCommandUtil.appendModuleNameInKey(ContextNames.TASK, ContextNames.TASK, FieldUtil.getAsProperties(task), placeHolders);
						CommonCommandUtil.appendModuleNameInKey(ContextNames.WORK_ORDER, ContextNames.WORK_ORDER, woPropertiesMap.get(wo.getId()), placeHolders);
						action.executeAction(placeHolders, null, null, task);
					}
				}
			}
		}
		return false;
	}

}

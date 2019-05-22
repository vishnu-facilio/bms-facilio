package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.constants.FacilioConstants;

public class AddActionForTaskCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		Map<String, List<TaskContext>> taskMap = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
		if (taskMap != null && !taskMap.isEmpty()) {
			List<ActionContext> actions = new ArrayList<>();
			Map<Long, WorkorderTemplate> templateMap = new HashMap<>(); 
			for( Entry<String, List<TaskContext>> entry : taskMap.entrySet()) {
				for(TaskContext task : entry.getValue()) {
					long templateId = task.getWoCreateTemplateId();
					if (templateId > 0) {
						WorkorderTemplate template = templateMap.get(templateId);
						if (template == null) {
							template = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
							templateMap.put(templateId, template);
						}
						if (template.getSubject() == null || StringUtils.isEmpty(template.getResourceId())) {
							WorkorderTemplate newTemplate = new WorkorderTemplate();
							if(template.getSubject() == null) {
								newTemplate.setSubject("${task.subject}");
							}
							newTemplate.setResourceId("${task.resource.id:-}");
							newTemplate.setName(template.getName());
							newTemplate.setWorkflow(TemplateAPI.getWorkflow(newTemplate));
							TemplateAPI.updateWorkorderTemplate(newTemplate, template);
						}
						
						ActionContext action = new ActionContext();
						action.setTemplateId(template.getId());
						action.setActionType(ActionType.CREATE_WORK_ORDER);
						
						task.setAction(action);
						actions.add(action);
					}
				}
			}
			if (!actions.isEmpty()) {
				ActionAPI.addActions(actions);
				for( Entry<String, List<TaskContext>> entry : taskMap.entrySet()) {
					for(TaskContext task : entry.getValue()) {
						if (task.getAction() != null) {
							task.setActionId(task.getAction().getId());
						}
					}
				}
			}
		}
		return false;
	}
	
}

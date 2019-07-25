package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BulkAddActionForTaskCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(BulkAddActionForTaskCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		PreventiveMaintenanceAPI.logIf(92L,"Entering BulkAddActionForTaskCommand");
		BulkWorkOrderContext bulkWorkOrderContext = (BulkWorkOrderContext) context.get(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT);

		if (bulkWorkOrderContext.getTaskMaps() == null || bulkWorkOrderContext.getTaskMaps().isEmpty()) {
			return false;
		}

		List<Long> templateIds = bulkWorkOrderContext.getTaskMaps()
				.stream()
				.filter(Objects::nonNull)
				.flatMap(i -> i.values().stream())
				.flatMap(Collection::stream)
				.map(TaskContext::getWoCreateTemplateId)
				.filter(i -> i > 0)
				.collect(Collectors.toList());

		Map<Long, Template> templateMap = new HashMap<>();

		if (templateIds != null && !templateIds.isEmpty()) {
			List<Template> templates = TemplateAPI.getTemplates(templateIds);
			templates.forEach(i -> templateMap.put(i.getId(), i));
		}

		List<Map<String, List<TaskContext>>> taskMaps = bulkWorkOrderContext.getTaskMaps();
		List<ActionContext> actions = new ArrayList<>();

		for (Map<String, List<TaskContext>> taskMap: taskMaps) {
			if (taskMap == null) {
				continue;
			}
			for (Entry<String, List<TaskContext>> entry: taskMap.entrySet()) {
				for(TaskContext task : entry.getValue()) {
					long templateId = task.getWoCreateTemplateId();
					if (templateId > 0) {
						WorkorderTemplate template = (WorkorderTemplate) templateMap.get(templateId);
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
		}

		if (!actions.isEmpty()) {
			ActionAPI.addActions(actions);
			for (Map<String, List<TaskContext>> taskMap: taskMaps) {
				for( Entry<String, List<TaskContext>> entry : taskMap.entrySet()) {
					for(TaskContext task : entry.getValue()) {
						if (task.getAction() != null) {
							task.setActionId(task.getAction().getId());
						}
					}
				}
			}
		}
		PreventiveMaintenanceAPI.logIf(92L, "Done BulkAddActionForTaskCommand");
		return false;
	}
	
}

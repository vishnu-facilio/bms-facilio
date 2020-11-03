package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.templates.FormTemplate;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.constants.FacilioConstants;

public class BulkAddActionForTaskCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(BulkAddActionForTaskCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		PreventiveMaintenanceAPI.logIf(92L,"Entering BulkAddActionForTaskCommand");
		BulkWorkOrderContext bulkWorkOrderContext = (BulkWorkOrderContext) context.get(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT);

		if (bulkWorkOrderContext.getTaskMaps() == null || bulkWorkOrderContext.getTaskMaps().isEmpty()) {
			return false;
		}
		
		Map<Long, FormTemplate> formMap;
		List<Long> formIds = new ArrayList<>();
		List<TaskContext> formTasks = new ArrayList<>();
		
		bulkWorkOrderContext.getTaskMaps()
		.stream()
		.filter(Objects::nonNull)
		.flatMap(i -> i.values().stream()).flatMap(List::stream).forEach(task -> {
			if (task.getWoCreateFormId() > 0) {
				formIds.add(task.getWoCreateFormId());
				formTasks.add(task);
			}
		});
		
		if (formTasks.isEmpty()) {
			return false;
		}
		
		formMap = TemplateAPI.getFormTemplateMap(formIds, SourceType.TASK_DEVIATION);
		if (formMap == null) {
			formMap = new HashMap<>();
		}
		
		List<Long> templateIds = formMap.values().stream().map(template -> template.getId()).collect(Collectors.toList());
		Map<Long, ActionContext> actionMap = ActionAPI.getActionsFromTemplate(templateIds, false);

		List<ActionContext> actions = new ArrayList<>();

		List<FormField> formFields = new ArrayList<>();
		for(TaskContext task : formTasks) {
			long formId = task.getWoCreateFormId();
			FormTemplate formTemplate = formMap.get(formId);
			if (formTemplate == null) {
				formTemplate = addFormTemplate(formId, formFields);
				formMap.put(formId, formTemplate);
				
				ActionContext action = new ActionContext();
				action.setTemplateId(formTemplate.getId());
				action.setActionType(ActionType.CREATE_DEVIATION_WORK_ORDER);
				if (actionMap == null) {
					actionMap = new HashMap<>();
				}
				actionMap.put(formTemplate.getId(), action);
				
				task.setAction(action);
				actions.add(action);
			}
			else {
				ActionContext formAction = actionMap.get(formTemplate.getId());
				if (formAction.getId() != -1) {
					task.setActionId(formAction.getId());
				}
				else { // Action not in db, but will be added for previous task
					task.setAction(formAction);
				}
			}
		}
		
		if (!formFields.isEmpty()) {
			FormsAPI.updateFormFields(formFields, Collections.singletonList("value"));
		}
		
		if (!actions.isEmpty()) {
			ActionAPI.addActions(actions);
			List<Map<String, List<TaskContext>>> taskMaps = bulkWorkOrderContext.getTaskMaps();
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
	
	private FormTemplate addFormTemplate (long formId, List<FormField> formFields) throws Exception {
		FormTemplate formTemplate = new FormTemplate();
		formTemplate.setName("formTemplate_"+formId);
		formTemplate.setFormId(formId);
		
		TemplateAPI.setFormInTemplate(formTemplate);
		FacilioForm form = formTemplate.getForm();
		Map<String, FormField> fieldMap = form.getFieldsMap();
		
		FormField subjectField = fieldMap.get("subject");
//		Map<Long, String> values = new HashMap<>();
		if (subjectField.getValue() == null) {
			FormField field = new FormField();
			field.setId(subjectField.getId());
			String value = "Deviation Work Order - ${task.resource.name:-}";
			field.setValue(value);
			subjectField.setValue(value);
			if (formTemplate.getOriginalTemplate() != null) {
				formTemplate.getOriginalTemplate().put("subject", value);
			}
			formFields.add(field);
		}
		
		FormField siteField = fieldMap.get("siteId");
		if (siteField.getValue() == null) {
			FormField field = new FormField();
			field.setId(siteField.getId());
			String value = "${workorder.siteId}";
			field.setValue(value);
			siteField.setValue(value);
			if (formTemplate.getOriginalTemplate() != null) {
				formTemplate.getOriginalTemplate().put("siteId", value);
			}
			formFields.add(field);
		}
		
		FormField descriptionField = fieldMap.get("description");
		if (descriptionField != null && descriptionField.getValue() == null) {
			FormField field = new FormField();
			field.setId(descriptionField.getId());
			String value = "Deviation work order created from the task ${task.subject} of ${workorder.subject} work order. \n\n Task Value - ${task.inputValue}";
			field.setValue(value);
			descriptionField.setValue(value);
			if (formTemplate.getOriginalTemplate() != null) {
				formTemplate.getOriginalTemplate().put("description", value);
			}
			formFields.add(field);
		}
		
		FormField resourceField = fieldMap.get("resource");
		if (resourceField != null && resourceField.getValue() == null) {
			FormField field = new FormField();
			field.setId(resourceField.getId());
			field.setValue("${task.resource.id:-}");
			resourceField.setValue("${task.resource.id:-}");
			if (formTemplate.getOriginalTemplate() != null) {
				formTemplate.getOriginalTemplate().put("resource", Collections.singletonMap("id", "${task.resource.id:-}"));
			}
			formFields.add(field);
		}
		formTemplate.setSourceType(SourceType.TASK_DEVIATION);
		formTemplate.setWorkflow(TemplateAPI.getWorkflow(formTemplate));
		TemplateAPI.addTemplate(formTemplate);
		return formTemplate;
	}
	
}

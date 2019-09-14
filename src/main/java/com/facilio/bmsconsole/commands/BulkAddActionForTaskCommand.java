package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.bmsconsole.context.TaskContext;
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
		
		formMap = TemplateAPI.getFormTemplateMap(formIds);
		if (formMap == null) {
			formMap = new HashMap<>();
		}

		List<ActionContext> actions = new ArrayList<>();

		List<FormField> formFields = new ArrayList<>();
		for(TaskContext task : formTasks) {
			long formId = task.getWoCreateFormId();
			FormTemplate formTemplate = formMap.get(formId);
			if (formTemplate == null) {
				formTemplate = new FormTemplate();
				formTemplate.setName("formTemplate_"+formId);
				formTemplate.setFormId(formId);
				
				TemplateAPI.setFormInTemplate(formTemplate);
				FacilioForm form = formTemplate.getForm();
				Map<String, FormField> fieldMap = form.getFieldsMap();
				
				FormField subjectField = fieldMap.get("subject");
//				Map<Long, String> values = new HashMap<>();
				if (subjectField.getValue() == null) {
					FormField field = new FormField();
					field.setId(subjectField.getId());
					field.setValue("${task.subject}");
					subjectField.setValue("${task.subject}");
					if (formTemplate.getOriginalTemplate() != null) {
						formTemplate.getOriginalTemplate().put("subject", "${task.subject}");
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
				
				formTemplate.setWorkflow(TemplateAPI.getWorkflow(formTemplate));
				TemplateAPI.addTemplate(formTemplate);
				
				formMap.put(formId, formTemplate);
			}
			
			ActionContext action = new ActionContext();
			action.setTemplateId(formTemplate.getId());
			action.setActionType(ActionType.CREATE_WORK_ORDER);
			
			task.setAction(action);
			actions.add(action);
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
	
}

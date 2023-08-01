package com.facilio.bmsconsoleV3.commands.workorder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.templates.FormTemplate;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

/**
 * Removed all the usage of V3AddActionForTaskCommand
 * - sets the action IDs to the tasks that has formID.
 * - Constructs form template value as placeholders
 */
@Log4j
public class V3AddActionForTaskCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
		if(MapUtils.isEmpty(recordMap)){
			LOGGER.info("recordMap is empty");
			return false;
		}

		List<V3WorkOrderContext> workOrderContextList = recordMap.get(FacilioConstants.ContextNames.WORK_ORDER);

		if(CollectionUtils.isEmpty(workOrderContextList)){
			LOGGER.info("workOrderContextList is empty");
			return false;
		}

		for (V3WorkOrderContext workOrderContext: workOrderContextList) {
			Map<String, List<V3TaskContext>> taskMap = workOrderContext.getTasksString();
			addActionForTasks(taskMap);
		}
		return false;
	}

	/**
	 * Helper function to set the action ID for the tasks that has formID. It also constructs the form template with
	 * value as placeholders
	 * @param taskMap
	 * @throws Exception
	 */
	private void addActionForTasks(Map<String, List<V3TaskContext>> taskMap) throws Exception {
		if (taskMap != null && !taskMap.isEmpty()) {
			List<ActionContext> actions = new ArrayList<>();
			Map<Long, FormTemplate> formMap;

			List<FormField> formFields = new ArrayList<>();
			List<Long> formIds = new ArrayList<>();
			List<V3TaskContext> formTasks = new ArrayList<>();
			taskMap.values().stream().flatMap(List::stream).forEach(task -> {
				if (task.getWoCreateFormId() != null && task.getWoCreateFormId() > 0) {
					formIds.add(task.getWoCreateFormId());
					formTasks.add(task);
				}
			});

			if (formTasks.isEmpty()) {
				LOGGER.info("formTasks is empty");
				 return;
			}else {
				LOGGER.info("formTasks size: " + formTasks.size());
			}

			formMap = TemplateAPI.getFormTemplateMap(formIds,SourceType.TASK_DEVIATION);
			if (formMap == null) {
				formMap = new HashMap<>();
			}
			List<Long> templateIds = formMap.values().stream().map(template -> template.getId()).collect(Collectors.toList());
			Map<Long, ActionContext> actionMap = ActionAPI.getActionsFromTemplate(templateIds, false);

			for(V3TaskContext task : formTasks) {
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

					formTemplate.setWorkflow(TemplateAPI.getWorkflow(formTemplate));
					formTemplate.setSourceType(SourceType.TASK_DEVIATION);
					TemplateAPI.addTemplate(formTemplate);

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
					else {	// Action not in db, but will be added for previous task
						task.setAction(formAction);
					}
				}

			}

			if (!formFields.isEmpty()) {
				FormsAPI.updateFormFields(formFields, Collections.singletonList("value"));
			}
			if (!actions.isEmpty()) {
				ActionAPI.addActions(actions);
				for( Map.Entry<String, List<V3TaskContext>> entry : taskMap.entrySet()) {
					for(V3TaskContext task : entry.getValue()) {
						if (task.getAction() != null) {
							task.setActionId(task.getAction().getId());
						}
					}
				}
			}
		}
	}

}

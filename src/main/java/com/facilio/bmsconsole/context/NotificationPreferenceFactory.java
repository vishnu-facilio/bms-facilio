package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.text.StringSubstitutor;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.ScheduledRuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;

public class NotificationPreferenceFactory {
	private final static Map<String, List<NotificationPreference>> map = new HashMap<String, List<NotificationPreference>>();
	
	static {
		initializeMap();
	}
	
	private static void initializeMap() {
		String moduleName = FacilioConstants.ContextNames.CONTRACTS;
		
		List<NotificationPreference> contractPreference = new ArrayList<NotificationPreference>();
		
		FacilioForm form = new FacilioForm();
		List<FormSection> sections = new ArrayList<FormSection>();
		FormSection formSection = new FormSection();
		formSection.setName("Sample");
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("days", FieldDisplayType.NUMBER, "How many days before?", Required.REQUIRED, 1, 1));
		formSection.setFields(fields);
		sections.add(formSection);
		form.setSections(sections);
		
		contractPreference.add(new NotificationPreference("expireDateNotification", form) {
			@Override
			public WorkflowRuleContext substitute(Map<String, Object> map, ModuleBaseWithCustomFields record) throws Exception {
				ContractsContext contract = (ContractsContext) record;
				
				WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
				workflowRuleContext.setName("Expiry Date Notification");
				workflowRuleContext.setRuleType(RuleType.RECORD_SPECIFIC_RULE);
				
				WorkflowEventContext event = new WorkflowEventContext();
				event.setModuleName(moduleName);
				event.setActivityType(EventType.SCHEDULED);
				workflowRuleContext.setEvent(event);
				workflowRuleContext.setScheduleType(ScheduledRuleType.ON);
				workflowRuleContext.setTime("10:00");
				
				ActionContext emailAction = new ActionContext();
				emailAction.setActionType(ActionType.EMAIL_NOTIFICATION);
				JSONObject json = new JSONObject();
				json.put("to", map.get("to"));
				json.put("subject", "Expiry notification");
				String message = "Your contract expires at ${expiryDate}";
				Map<String, Object> substitutorMap = new HashMap<String, Object>();
				substitutorMap.put("expiryDate", contract.getEndDate());
				StringSubstitutor.replace(message, substitutorMap);
				json.put("message", message);
				emailAction.setTemplateJson(json);
				workflowRuleContext.addAction(emailAction);
				
				workflowRuleContext.setDateFieldId(1422);
				int days = ((Number) map.get("days")).intValue();
				workflowRuleContext.setInterval(-1 * days * 24 * 60 * 60);
				
				return workflowRuleContext;
			}
		});
		map.put(moduleName, contractPreference);
	}
	
	public static List<NotificationPreference> getModuleNotificationPreferences(String moduleName) {
		return map.get(moduleName);
	}
	
	public static NotificationPreference getNotificationPreference(String moduleName, String name) {
		List<NotificationPreference> list = map.get(moduleName);
		if (CollectionUtils.isNotEmpty(list)) {
			for (NotificationPreference p : list) {
				if (p.getName().equals(name)) {
					return p;
				}
			}
		}
		return null;
	}
	
//	public static 
}

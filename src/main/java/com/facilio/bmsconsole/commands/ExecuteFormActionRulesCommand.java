package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.forms.FormRuleTriggerFieldContext;
import com.facilio.bmsconsole.forms.FormRuleContext.TriggerType;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;

public class ExecuteFormActionRulesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<FormRuleContext> formRuleContexts = (List<FormRuleContext>)context.get(FormRuleAPI.FORM_RULE_CONTEXTS);
		
		TriggerType triggerType = (TriggerType) context.get(FormRuleAPI.FORM_RULE_TRIGGER_TYPE);
		
		Map<String,Object> formData = (Map<String,Object>)context.get(FormRuleAPI.FORM_DATA);
		
		FacilioForm form = (FacilioForm)context.get(ContextNames.FORM);
		
		JSONArray resultJson = new JSONArray();
		context.put(FormRuleAPI.FORM_RULE_RESULT_JSON, resultJson);
		
		
		Map<String,Object> cloneFormData = new HashMap<String, Object>();
		if (triggerType.equals(TriggerType.FORM_ON_LOAD) && formData != null && !formData.isEmpty()) {
		 cloneFormData = new HashMap<String, Object>(formData);
			if (formData.containsKey("data")) {
				Map<String,Object> customData = (Map<String, Object>) formData.get("data");
				cloneFormData.putAll(customData);
				cloneFormData.remove("data");
			}
		}
		
		for(FormRuleContext formRuleContext :formRuleContexts) {	
			if (formRuleContext.getFormContext() == null && form != null) {
				formRuleContext.setFormContext(form);
			}
			boolean flag = false;	
			
			if (triggerType.equals(TriggerType.FORM_ON_LOAD) && formData != null && !formData.isEmpty()) {
				List<FormRuleTriggerFieldContext> triggerFields = FormRuleAPI.getFormRuleTriggerFields(formRuleContext);
				boolean hasValue = false;
				for (FormRuleTriggerFieldContext triggerField : triggerFields) {					
					FormField fieldObj = FormsAPI.getFormFieldFromId(triggerField.getFieldId());	
					
					if (fieldObj.getField() != null && fieldObj.getField().getName() != null && cloneFormData.containsKey(fieldObj.getField().getName())) {
						if (fieldObj.getDisplayTypeEnum() == FieldDisplayType.LOOKUP_SIMPLE) {
							HashMap lookupData = (HashMap) cloneFormData.get(fieldObj.getField().getName());
							long id = (long) lookupData.get("id");
							if (id > 0) {
								hasValue = true;
							}
						}
						
						else if (fieldObj.getDisplayTypeEnum() == FieldDisplayType.NUMBER || fieldObj.getDisplayTypeEnum() == FieldDisplayType.DECIMAL) {	
							if (!cloneFormData.get(fieldObj.getField().getName()).equals((long) -99)) {
								hasValue = true;
							}
						}
						
						else if (cloneFormData.containsKey(fieldObj.getField().getName())) {
							hasValue = true;
						}
						
					}				
				}	
				if (hasValue) {
					flag = formRuleContext.evaluateCriteria(formData, (FacilioContext)context);
				}	
			}			
			else {
				 flag = formRuleContext.evaluateCriteria(formData, (FacilioContext)context);
			}
						
			if(flag) {
				formRuleContext.executeAction((FacilioContext)context);
			}
		}
		
		return false;
	}

}

package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.forms.FormRuleTriggerFieldContext;
import com.facilio.bmsconsole.forms.FormRuleContext.TriggerType;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;

public class ValidateAndFillFromRuleParamsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		TriggerType triggerType = (TriggerType) context.get(FormRuleAPI.FORM_RULE_TRIGGER_TYPE);
		Long formId = (Long) context.get(FacilioConstants.ContextNames.FORM_ID);
		Long formFieldId = (Long) context.get(FacilioConstants.ContextNames.FORM_FIELD_ID);
		Map<String, Object> formData = (Map<String,Object>) context.get(FormRuleAPI.FORM_DATA);

		if(triggerType == null) {
			throw new IllegalArgumentException("Trigger Type Cannot be null during Form Action Evaluation");
		}
		if(formId == null) {
			throw new IllegalArgumentException("fromId Cannot be null during Form Action Evaluation");
		}
		
		FacilioForm form = FormsAPI.getFormFromDB(formId);
		List<FormRuleContext> formRuleContexts = new ArrayList<>();
		switch (triggerType) {
		case FORM_ON_LOAD:
			formRuleContexts = FormRuleAPI.getFormRuleContext(formId, triggerType, formData);
			context.put(FormRuleAPI.FORM_RULE_CONTEXTS, formRuleContexts);
			break;
		case FORM_SUBMIT:
			if(formData == null) {
				throw new IllegalArgumentException("Form Data Cannot be null during Form On Submit Action Evaluation");
			}
			formRuleContexts = FormRuleAPI.getFormRuleContext(formId, triggerType, formData);
			context.put(FormRuleAPI.FORM_RULE_CONTEXTS, formRuleContexts);
			break;
			
		case FIELD_UPDATE:
			if(formData == null) {
				throw new IllegalArgumentException("Form Data Cannot be null during Form Action Evaluation");
			}
			if(formFieldId == null) {
				throw new IllegalArgumentException("Form Field Cannot be null during Form Field Action Evaluation");
			}
			formRuleContexts = FormRuleAPI.getFormRuleContext(formId, Collections.singletonList(formFieldId), triggerType);
			context.put(FormRuleAPI.FORM_RULE_CONTEXTS, formRuleContexts);
			break;

		default:
			break;
		}
		
		Map<String,Object> cloneFormData = new HashMap<String, Object>();
		if (triggerType.equals(TriggerType.FORM_ON_LOAD) && formData != null && !formData.isEmpty()) {
		 cloneFormData = new HashMap<String, Object>(formData);
			if (formData.containsKey("data")) {
				Map<String,Object> customData = (Map<String, Object>) formData.get("data");
				cloneFormData.putAll(customData);
				cloneFormData.remove("data");
			}
			
			List<Long> triggerFieldIds = new ArrayList<>();
			for (FormField fieldObj : form.getFields()) {				
				boolean hasValue = false;
				if (fieldObj.getField() != null && fieldObj.getField().getName() != null && cloneFormData.containsKey(fieldObj.getField().getName())) {
					if (fieldObj.getDisplayTypeEnum() == FieldDisplayType.LOOKUP_SIMPLE) {
						long id = -1;
						if (cloneFormData.get(fieldObj.getField().getName()) instanceof Long) {
							id = (long) cloneFormData.get(fieldObj.getField().getName());	
						}
						else if (cloneFormData.get(fieldObj.getField().getName()) instanceof HashMap) {
							 HashMap lookupData = (HashMap) cloneFormData.get(fieldObj.getField().getName());
							 id = (long) lookupData.get("id");
						}
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
				if (hasValue) {
					triggerFieldIds.add(fieldObj.getId());
				}	
				
			}
			if (triggerFieldIds != null && !triggerFieldIds.isEmpty()) {
				List<FormRuleContext> updateFormRuleContexts = FormRuleAPI.getFormRuleContext(formId, triggerFieldIds, TriggerType.FIELD_UPDATE);
				if (updateFormRuleContexts != null && !updateFormRuleContexts.isEmpty()) {
					formRuleContexts.addAll(updateFormRuleContexts);
				}
			}
		}
		
		context.put(ContextNames.FORM, form);
		
		return false;
	}

}

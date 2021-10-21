package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.forms.FormRuleContext.TriggerType;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;

public class ValidateAndFillFromRuleParamsCommand extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(ValidateAndFillFromRuleParamsCommand.class.getName());
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
			if (formData != null) {
				// An initial unnecessary call without formdata is done from client and this causes unknown problems.
				formRuleContexts = FormRuleAPI.getFormRuleContext(formId, triggerType, formData);
			}
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
		
		List<Long> triggerFieldIds = new ArrayList<>();
		List<FormField> fields = form.getFields();
		Map<String, FormField> fieldMap = fields.stream().collect
				  (Collectors.toMap
				  (field -> ((field.getField() != null && field.getField().getName() != null) ? field.getField().getName():field.getName()),
				   Function.identity()));
		
		if (formData != null && !formData.isEmpty()) {	
					
					if (formData.containsKey("data")) {
						Map<String,Object> customData = (Map<String, Object>) formData.get("data");
						formData.putAll(customData);
						//formData.remove("data");
					}
				
				Map<String, Object> lookupFormData = new HashMap<>();			
				for (String eachKey : formData.keySet()) {
					FormField respectiveFormField = fieldMap.get(eachKey);
					boolean hasValue = false;
					if (respectiveFormField != null && respectiveFormField.getField() != null && respectiveFormField.getField().getName() != null) {
						
						if (respectiveFormField.getDisplayTypeEnum() == FieldDisplayType.LOOKUP_SIMPLE) {
							long id = -1;
							if (formData.get(respectiveFormField.getField().getName()) instanceof Long) {
								id = (long) formData.get(respectiveFormField.getField().getName());	
								HashMap lookupData = new HashMap();
								lookupData.put("id", id);
								lookupFormData.put(eachKey, lookupData);
							}
							else if (formData.get(respectiveFormField.getField().getName()) instanceof HashMap) {
								 HashMap lookupData = (HashMap) formData.get(respectiveFormField.getField().getName());
								 id = (long) lookupData.get("id");
							}
							if (id > 0) {
								hasValue = true;
							}
						}
						
						else if (respectiveFormField.getDisplayTypeEnum() == FieldDisplayType.NUMBER || respectiveFormField.getDisplayTypeEnum() == FieldDisplayType.DECIMAL) {	
							if (!formData.get(respectiveFormField.getField().getName()).equals((long) -99)) {
								hasValue = true;
							}
						}
						
						else if (formData.containsKey(respectiveFormField.getField().getName())) {
							hasValue = true;
						}
						
					}
					if (hasValue) {
						triggerFieldIds.add(respectiveFormField.getId());
					}	
				}
				if (lookupFormData != null && !lookupFormData.isEmpty()) {
					formData.putAll(lookupFormData);
				}
			 
		}
			
		if (triggerType.equals(TriggerType.FORM_ON_LOAD)) {		
			if (triggerFieldIds != null && !triggerFieldIds.isEmpty()) {
				List<FormRuleContext> updateFormRuleContexts = FormRuleAPI.getFormRuleContext(formId, triggerFieldIds, TriggerType.FIELD_UPDATE);
				if (updateFormRuleContexts != null && !updateFormRuleContexts.isEmpty()) {
					formRuleContexts.addAll(updateFormRuleContexts);
				}
			}
		}
		
		LOGGER.debug("ruleInfoObject - formData - "+ formData + triggerType);
		context.put(FormRuleAPI.FORM_DATA, formData);
		context.put(ContextNames.FORM, form);
		
		return false;
	}

}

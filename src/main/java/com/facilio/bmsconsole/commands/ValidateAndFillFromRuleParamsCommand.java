package com.facilio.bmsconsole.commands;

import java.util.*;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.CurrencyUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.forms.FormRuleContext.TriggerType;
import com.facilio.bmsconsole.forms.FormRuleContext.ExecuteType;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;

public class ValidateAndFillFromRuleParamsCommand extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(ValidateAndFillFromRuleParamsCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		TriggerType triggerType = (TriggerType) context.get(FormRuleAPI.FORM_RULE_TRIGGER_TYPE);
		Long formId = (Long) context.get(FacilioConstants.ContextNames.FORM_ID);
		Long subFormId = (Long) context.get(FacilioConstants.ContextNames.SUB_FORM_ID);
		Long formFieldId = (Long) context.get(FacilioConstants.ContextNames.FORM_FIELD_ID);
		Map<String, Object> formData = (Map<String,Object>) context.get(FormRuleAPI.FORM_DATA);
		ExecuteType executeType = (ExecuteType) context.get(FormRuleAPI.FORM_RULE_EXECUTE_TYPE);

		if(executeType == null){
			executeType = ExecuteType.CREATE_AND_EDIT;
		}
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
				formRuleContexts = FormRuleAPI.getFormRuleContext(formId, triggerType, formData,executeType);
			}
			context.put(FormRuleAPI.FORM_RULE_CONTEXTS, formRuleContexts);
			break;
		case FORM_SUBMIT:
			if(formData == null) {
				throw new IllegalArgumentException("Form Data Cannot be null during Form On Submit Action Evaluation");
			}
			formRuleContexts = FormRuleAPI.getFormRuleContext(formId, triggerType, formData, executeType);
			context.put(FormRuleAPI.FORM_RULE_CONTEXTS, formRuleContexts);
			break;
			
		case FIELD_UPDATE:
			if(formData == null) {
				throw new IllegalArgumentException("Form Data Cannot be null during Form Action Evaluation");
			}
			if(formFieldId == null) {
				throw new IllegalArgumentException("Form Field Cannot be null during Form Field Action Evaluation");
			}
			formRuleContexts = FormRuleAPI.getFormRuleContext(formId, Collections.singletonList(formFieldId), triggerType,executeType);
			
			FormField formField = FormsAPI.getFormFieldFromId(formFieldId);
			
			if(subFormId != null && subFormId > 0 && subFormId.equals(formField.getFormId())) {
				
				context.put(FormRuleAPI.IS_SUB_FORM_TRIGGER_FIELD, Boolean.TRUE);
			}
			
			context.put(FormRuleAPI.FORM_RULE_CONTEXTS, formRuleContexts);
			break;

		case SUB_FORM_ADD_OR_DELETE:
			if(formData == null) {
				throw new IllegalArgumentException("Form Data Cannot be null during Sub form Action Evaluation");
			}
			if(subFormId == null || subFormId < 0) {
				throw new IllegalArgumentException("sub form Cannot be null during Sub form Action Evaluation");
			}
			formRuleContexts = FormRuleAPI.getSubFormRuleContext(formId, subFormId, triggerType);
			context.put(FormRuleAPI.FORM_RULE_CONTEXTS, formRuleContexts);
			break;
		default:
			break;
		}
		
		List<Long> triggerFieldIds = new ArrayList<>();
		List<FormField> fields = form.getFields();
//		Map<String, FormField> fieldMap = fields.stream().collect
//				  (Collectors.toMap
//				  (field -> ((field.getField() != null && field.getField().getName() != null) ? field.getField().getName():field.getName()),
//				   Function.identity()));

		Map<String, FormField> fieldMap = new HashMap<>();
		for(FormField formField : fields){
			if ((formField.getField() != null && formField.getField().getName() != null) && !fieldMap.containsKey(formField.getName())){
				fieldMap.put(formField.getName(),formField);
			}
			else{
				LOGGER.debug("formField "+ formField.getName()+ " duplicate for form id : "+ formId);
				if((formField.getField() != null && formField.getField().getName() != null) && formField.getFieldId() != -1){
					fieldMap.put(formField.getName(),formField);
				}
			}
		}

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
				if (respectiveFormField != null && respectiveFormField.getField() != null && respectiveFormField.getField().getName() != null && !Objects.equals(respectiveFormField.getName(), "siteId") && respectiveFormField.getField().getDataTypeEnum()!= FieldType.NUMBER) {
					
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
						else if (formData.containsKey(respectiveFormField.getField().getName())) {
							hasValue = true;
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
					
					if(respectiveFormField.getField() != null) {
						if(respectiveFormField.getField().getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
							List<HashMap<String, Long>> multilookupValues = (List<HashMap<String, Long>>) formData.get(respectiveFormField.getField().getName());
							if(CollectionUtils.isNotEmpty(multilookupValues)) {
								List<Long> multiLookupIds = new ArrayList<Long>(); 
								for(HashMap<String, Long> multilookupValue : multilookupValues) {
									Long id = (Long)multilookupValue.get("id");
									multiLookupIds.add(id);
								}
								lookupFormData.put(eachKey+".id", StringUtils.join(multiLookupIds, ","));
							}
						}
					}
				}
				if (hasValue) {
					triggerFieldIds.add(respectiveFormField.getId());
				}	
			}
			if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY)) {
				FacilioModule formModule = form.getModule();
				String moduleName = formModule.getName();
				List<FacilioField> multiCurrencyFields = CurrencyUtil.getMultiCurrencyFieldsForModule(moduleName);
				CurrencyContext baseCurrency = CurrencyUtil.getBaseCurrency();
				Map<String, CurrencyContext> currencyCodeVsCurrency = CurrencyUtil.getCurrencyMap();
				CurrencyUtil.replaceCurrencyValueWithBaseCurrencyValue(formData, multiCurrencyFields, baseCurrency, currencyCodeVsCurrency);
			}
			if (lookupFormData != null && !lookupFormData.isEmpty()) {
				formData.putAll(lookupFormData);
			}
			 
		}
			
		if (triggerType.equals(TriggerType.FORM_ON_LOAD)) {
			if (triggerFieldIds != null && !triggerFieldIds.isEmpty()) {
				List<FormRuleContext> updateFormRuleContexts = FormRuleAPI.getFormRuleContext(formId, triggerFieldIds, TriggerType.FIELD_UPDATE,executeType);
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

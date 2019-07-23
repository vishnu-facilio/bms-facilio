package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.forms.FormRuleContext.TriggerType;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.constants.FacilioConstants;

public class ValidateAndFillFromRuleParamsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		TriggerType triggerType = (TriggerType) context.get(FormRuleAPI.FORM_RULE_TRIGGER_TYPE);
		Long fromId = (Long) context.get(FacilioConstants.ContextNames.FORM_ID);
		Long formFieldId = (Long) context.get(FacilioConstants.ContextNames.FORM_FIELD_ID);
		Map<String, Object> formData = (Map<String,Object>) context.get(FormRuleAPI.FORM_DATA);
		
		if(triggerType == null) {
			throw new IllegalArgumentException("Trigger Type Cannot be null during Form Action Evaluation");
		}
		if(fromId == null) {
			throw new IllegalArgumentException("fromId Cannot be null during Form Action Evaluation");
		}
		
		switch (triggerType) {
		case FORM_ON_LOAD:
			FormRuleContext formRuleContext = FormRuleAPI.getFormRuleContext(fromId, null, triggerType);
			context.put(FormRuleAPI.FORM_RULE_CONTEXT, formRuleContext);
			break;
		case FORM_SUBMIT:
			if(formData == null) {
				throw new IllegalArgumentException("Form Data Cannot be null during Form On Submit Action Evaluation");
			}
			formRuleContext = FormRuleAPI.getFormRuleContext(fromId, null, triggerType);
			context.put(FormRuleAPI.FORM_RULE_CONTEXT, formRuleContext);
			break;
			
		case FIELD_UPDATE:
			if(formData == null) {
				throw new IllegalArgumentException("Form Data Cannot be null during Form Action Evaluation");
			}
			if(formFieldId == null) {
				throw new IllegalArgumentException("Form Field Cannot be null during Form Field Action Evaluation");
			}
			formRuleContext = FormRuleAPI.getFormRuleContext(fromId, formFieldId, triggerType);
			context.put(FormRuleAPI.FORM_RULE_CONTEXT, formRuleContext);
			break;

		default:
			break;
		}
		
		
		
		return false;
	}

}

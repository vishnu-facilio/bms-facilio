package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
	private static final Logger LOGGER = LogManager.getLogger(ExecuteFormActionRulesCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<FormRuleContext> formRuleContexts = (List<FormRuleContext>)context.get(FormRuleAPI.FORM_RULE_CONTEXTS);
		
		TriggerType triggerType = (TriggerType) context.get(FormRuleAPI.FORM_RULE_TRIGGER_TYPE);
		
		Map<String,Object> formData = (Map<String,Object>)context.get(FormRuleAPI.FORM_DATA);
		
		FacilioForm form = (FacilioForm)context.get(ContextNames.FORM);
		
		JSONArray resultJson = new JSONArray();
		context.put(FormRuleAPI.FORM_RULE_RESULT_JSON, resultJson);
				
		for(FormRuleContext formRuleContext :formRuleContexts) {	
			if (formRuleContext.getFormContext() == null && form != null) {
				formRuleContext.setFormContext(form);
			}
			boolean flag = formRuleContext.evaluateCriteria(formData, (FacilioContext)context);
			LOGGER.info("ruleInfoObject - flag"+ flag);
			if(flag) {
				formRuleContext.executeAction((FacilioContext)context);
			}
		}
		
		return false;
	}

}

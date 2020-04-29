package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;

import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.chain.FacilioContext;

public class ExecuteFormActionRulesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<FormRuleContext> formRuleContexts = (List<FormRuleContext>)context.get(FormRuleAPI.FORM_RULE_CONTEXTS);
		
		Map<String,Object> formData = (Map<String,Object>)context.get(FormRuleAPI.FORM_DATA);
		
		JSONArray resultJson = new JSONArray();
		context.put(FormRuleAPI.FORM_RULE_RESULT_JSON, resultJson);
		
		for(FormRuleContext formRuleContext :formRuleContexts) {
			
			boolean flag = formRuleContext.evaluateCriteria(formData, (FacilioContext)context);
			
			if(flag) {
				formRuleContext.executeAction((FacilioContext)context);
			}
		}
		
		return false;
	}

}

package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.chain.FacilioContext;

public class ExecuteFormActionRulesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		FormRuleContext formRuleContext = (FormRuleContext)context.get(FormRuleAPI.FORM_RULE_CONTEXT);
		
		Map<String,Object> formData = (Map<String,Object>)context.get(FormRuleAPI.FORM_DATA);
		
		JSONArray resultJson = new JSONArray();
		context.put(FormRuleAPI.FORM_RULE_RESULT_JSON, resultJson);
		
		boolean flag = formRuleContext.evaluateCriteria(formData, (FacilioContext)context);
		
		if(flag) {
			formRuleContext.executeAction((FacilioContext)context);
		}
		
		return false;
	}

}

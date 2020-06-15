package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.constants.FacilioConstants;

public class GetFormRulesMapListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		FormRuleContext formRule = (FormRuleContext)context.get(FormRuleAPI.FORM_RULE_CONTEXT);
		 Map<Long,FormRuleContext> formRulesMap = FormRuleAPI.getFormTypeRulesMap(formRule.getFormId());
		context.put(FormRuleAPI.FORM_RULE_RESULT_JSON, formRulesMap);
		return false;
	}

	

}

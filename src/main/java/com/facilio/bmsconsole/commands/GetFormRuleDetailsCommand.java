package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;

public class GetFormRuleDetailsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FormRuleContext formRule = (FormRuleContext)context.get(FormRuleAPI.FORM_RULE_CONTEXT);
		FormRuleContext formRuleContext = FormRuleAPI.getFormRuleContext(formRule.getId());
		context.put(FormRuleAPI.FORM_RULE_RESULT_JSON, formRuleContext);
		return false;
	}	

}

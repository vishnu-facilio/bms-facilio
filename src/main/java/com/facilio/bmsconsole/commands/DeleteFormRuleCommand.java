package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;

public class DeleteFormRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		FormRuleContext formRule = (FormRuleContext)context.get(FormRuleAPI.FORM_RULE_CONTEXT);
		
		FormRuleAPI.deleteFormRuleContext(formRule);
		return false;
	}

}

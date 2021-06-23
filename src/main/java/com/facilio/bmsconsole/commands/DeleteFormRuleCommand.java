package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;

public class DeleteFormRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		FormRuleContext formRule = (FormRuleContext)context.get(FormRuleAPI.FORM_RULE_CONTEXT);
		
		FormRuleAPI.deleteFormRuleContext(formRule);
		return false;
	}

}

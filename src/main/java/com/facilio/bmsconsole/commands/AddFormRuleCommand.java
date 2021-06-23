package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.db.criteria.CriteriaAPI;

public class AddFormRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		FormRuleContext formRule = (FormRuleContext)context.get(FormRuleAPI.FORM_RULE_CONTEXT);
		
		long id = CriteriaAPI.addCriteria(formRule.getCriteria(), AccountUtil.getCurrentOrg().getId());
		
		formRule.setCriteriaId(id);
		FormRuleAPI.addFormRuleContext(formRule);
		return false;
	}

}

package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.db.criteria.CriteriaAPI;

public class UpdateFormRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		FormRuleContext formRule = (FormRuleContext)context.get(FormRuleAPI.FORM_RULE_CONTEXT);
		
		CriteriaAPI.deleteCriteria(formRule.getCriteriaId());
		long id = CriteriaAPI.addCriteria(formRule.getCriteria(), AccountUtil.getCurrentOrg().getId());
		formRule.setCriteriaId(id);
		FormRuleAPI.updateFormRuleContext(formRule);
		return false;
	}

}

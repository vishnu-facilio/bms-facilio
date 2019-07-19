package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.forms.FormRuleActionContext;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.db.criteria.CriteriaAPI;

public class AddFormRuleActionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		FormRuleContext formRule = (FormRuleContext)context.get(FormRuleAPI.FORM_RULE_CONTEXT);
		
		for(FormRuleActionContext action :formRule.getActions()) {
			if(action.getCriteria() != null) {
				long id = CriteriaAPI.addCriteria(action.getCriteria(), AccountUtil.getCurrentOrg().getId());
				action.setCriteriaId(id);
			}
			FormRuleAPI.addFormRuleActionContext(action);
		}
		return false;
	}

}

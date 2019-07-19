package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FormRuleActionContext;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.db.criteria.CriteriaAPI;

public class DeleteFormRuleActionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		FormRuleContext formRule = (FormRuleContext)context.get(FormRuleAPI.FORM_RULE_CONTEXT);
		
		for(FormRuleActionContext action :formRule.getActions()) {
			if(action.getCriteriaId() > 0) {
				CriteriaAPI.deleteCriteria(action.getCriteriaId());
			}
			FormRuleAPI.deleteFormRuleActionContext(action);
		}
		return false;
	}

}

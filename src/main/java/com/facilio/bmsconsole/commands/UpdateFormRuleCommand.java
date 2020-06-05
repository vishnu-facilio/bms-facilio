package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.db.criteria.CriteriaAPI;

public class UpdateFormRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		FormRuleContext formRule = (FormRuleContext)context.get(FormRuleAPI.FORM_RULE_CONTEXT);
		
		long oldCriteriaId = formRule.getCriteriaId();
		long id = CriteriaAPI.addCriteria(formRule.getCriteria(), AccountUtil.getCurrentOrg().getId());
		formRule.setCriteriaId(id);
		FormRuleAPI.updateFormRuleContext(formRule);
		CriteriaAPI.deleteCriteria(oldCriteriaId);
		FormRuleAPI.deleteFormRuleTriggerFieldsContext(formRule.getId());
		FormRuleAPI.addFormRuleTriggerFieldsContext(formRule,formRule.getTriggerFields());
		return false;
	}

}

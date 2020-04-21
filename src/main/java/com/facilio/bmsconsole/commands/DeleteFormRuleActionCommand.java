package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FormRuleActionContext;
import com.facilio.bmsconsole.forms.FormRuleActionFieldsContext;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.db.criteria.CriteriaAPI;

public class DeleteFormRuleActionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		FormRuleContext formRule = (FormRuleContext)context.get(FormRuleAPI.FORM_RULE_CONTEXT);
		
		for(FormRuleActionContext action :formRule.getActions()) {
			
			for(FormRuleActionFieldsContext actionField : action.getFormRuleActionFieldsContext()) {
				
				if(actionField.getCriteriaId() > 0) {
					CriteriaAPI.deleteCriteria(actionField.getCriteriaId());
				}
			}
			
			FormRuleAPI.deleteFormRuleActionContext(action);
		}
		return false;
	}

}

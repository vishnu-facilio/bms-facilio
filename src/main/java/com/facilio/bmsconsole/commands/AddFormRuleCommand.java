package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.db.criteria.CriteriaAPI;

import java.util.List;
import java.util.Objects;

public class AddFormRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		FormRuleContext formRule = (FormRuleContext)context.get(FormRuleAPI.FORM_RULE_CONTEXT);

		if(formRule ==null){
			return false;
		}
		FacilioForm form = null;
		if(formRule.getFormId()>0){
			form = FormsAPI.getFormFromDB(formRule.getFormId());
		}

		if(form!=null && form.getModule().getName()!=null){
			Criteria formRuleCriteria = formRule.getCriteria();
			if (formRuleCriteria != null) {
				CriteriaAPI.updateConditionField(form.getModule().getName(), formRuleCriteria);
			}
		}

		long id = CriteriaAPI.addCriteria(formRule.getCriteria(), AccountUtil.getCurrentOrg().getId());
		formRule.setCriteriaId(id);
		
		if(formRule.getSubFormCriteria() != null) {
			long subFormCriteriaId = CriteriaAPI.addCriteria(formRule.getSubFormCriteria(), AccountUtil.getCurrentOrg().getId());
			formRule.setSubFormCriteriaId(subFormCriteriaId);
		}
		
		FormRuleAPI.addFormRuleContext(formRule);
		return false;
	}

}

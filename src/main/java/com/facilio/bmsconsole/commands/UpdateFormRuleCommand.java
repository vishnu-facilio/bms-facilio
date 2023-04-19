package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.forms.FormRuleContext.TriggerType;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.db.criteria.CriteriaAPI;

public class UpdateFormRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		FormRuleContext formRule = (FormRuleContext)context.get(FormRuleAPI.FORM_RULE_CONTEXT);

		FacilioForm form = null;
		if(formRule.getFormId()>0){
			form = FormsAPI.getFormFromDB(formRule.getFormId());
		}
		if(form!=null && form.getModule().getName()!=null){
			Criteria formRuleCriteria = formRule.getCriteria();
			CriteriaAPI.updateConditionField(form.getModule().getName(), formRuleCriteria);
		}

		long oldCriteriaId = formRule.getCriteriaId();
		long id = CriteriaAPI.addCriteria(formRule.getCriteria(), AccountUtil.getCurrentOrg().getId());
		formRule.setCriteriaId(id);
		
		if(formRule.getSubFormCriteria() == null) {
			if(formRule.getSubFormCriteriaId() > 0) {
				formRule.setSubFormCriteriaId(-1l);
			}
		}
		else {
			long subFormCriteriaId = CriteriaAPI.addCriteria(formRule.getSubFormCriteria(), AccountUtil.getCurrentOrg().getId());
			formRule.setSubFormCriteriaId(subFormCriteriaId);
		}
		
		FormRuleAPI.updateFormRuleContext(formRule);
		
		CriteriaAPI.deleteCriteria(oldCriteriaId);
		
		if(formRule.getTriggerTypeEnum() == TriggerType.FIELD_UPDATE) {
			
			FormRuleAPI.deleteFormRuleTriggerFieldsContext(formRule.getId());
			FormRuleAPI.addFormRuleTriggerFieldsContext(formRule,formRule.getTriggerFields());
		}
		return false;
	}

}

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

import java.util.ArrayList;
import java.util.List;

public class UpdateFormRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		FormRuleContext formRule = (FormRuleContext)context.get(FormRuleAPI.FORM_RULE_CONTEXT);

		FacilioForm form = null;
		List<Long> oldCriteriaIds = new ArrayList<>();
		if(formRule.getFormId()>0){
			form = FormsAPI.getFormFromDB(formRule.getFormId());
		}
		if(form!=null && form.getModule().getName()!=null){
			Criteria formRuleCriteria = formRule.getCriteria();
			CriteriaAPI.updateConditionField(form.getModule().getName(), formRuleCriteria);
		}

		long oldCriteriaId = FormRuleAPI.getFormRuleCriteriaId(formRule.getId(),false);
		if(oldCriteriaId>0){
			oldCriteriaIds.add(oldCriteriaId);
		}

		long id = CriteriaAPI.addCriteria(formRule.getCriteria(), AccountUtil.getCurrentOrg().getId());
		if(id==-1){
			formRule.setCriteriaId(-99);
		}else{
			formRule.setCriteriaId(id);
		}

		
		if(formRule.getSubFormCriteria() == null ) {
			if(formRule.getSubFormCriteriaId() > 0) {
				formRule.setSubFormCriteriaId(-99);
			}
		}
		else {
			Criteria subFormCriteria = formRule.getSubFormCriteria();
			FacilioForm subForm = FormsAPI.getFormFromDB(formRule.getSubFormId());
			CriteriaAPI.updateConditionField(subForm.getModule().getName(), subFormCriteria);
			long oldSubFormCriteriaId = FormRuleAPI.getFormRuleCriteriaId(formRule.getId(),true);
			if(oldSubFormCriteriaId>0){
				oldCriteriaIds.add(oldSubFormCriteriaId);
			}
			long subFormCriteriaId = CriteriaAPI.addCriteria(subFormCriteria, AccountUtil.getCurrentOrg().getId());
			if(subFormCriteriaId<0){
				formRule.setSubFormCriteriaId(-99);
			}else{
				formRule.setSubFormCriteriaId(subFormCriteriaId);
			}
		}
		
		FormRuleAPI.updateFormRuleContext(formRule);
		
		CriteriaAPI.batchDeleteCriteria(oldCriteriaIds);

		
		if(formRule.getTriggerTypeEnum() == TriggerType.FIELD_UPDATE) {
			
			FormRuleAPI.deleteFormRuleTriggerFieldsContext(formRule.getId());
			FormRuleAPI.addFormRuleTriggerFieldsContext(formRule,formRule.getTriggerFields());
		}
		return false;
	}

}

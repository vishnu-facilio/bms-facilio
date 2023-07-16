package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.db.criteria.CriteriaAPI;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class AddFormRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        FormRuleContext formRule = (FormRuleContext) context.get(FormRuleAPI.FORM_RULE_CONTEXT);

        if (formRule == null) {
            return false;
        }

        FacilioForm form = null;
        if (formRule.getFormId() > 0) {
            form = FormsAPI.getFormFromDB(formRule.getFormId());
        }

        Criteria formRuleCriteria = formRule.getCriteria();

        List<FormRuleActionContext> formRuleActionContexts = formRule.getActions();
        FacilioUtil.throwIllegalArgumentException(form == null, "Invalid form id.");
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(formRuleActionContexts), "Form rule actions cannot be empty.");

        String moduleName = form.getModule().getName();

        if (StringUtils.isNotEmpty(moduleName) && formRuleCriteria != null) {
            CriteriaAPI.updateConditionField(moduleName, formRuleCriteria);
        }

        long id = CriteriaAPI.addCriteria(formRuleCriteria, AccountUtil.getCurrentOrg().getId());
        formRule.setCriteriaId(id);

        Criteria subFormCriteria = formRule.getSubFormCriteria();
        FacilioForm subForm = null;
        if(subFormCriteria!= null && formRule.getSubFormId()>0){
            long subFormId = formRule.getSubFormId();
            subForm = FormsAPI.getFormFromDB(subFormId);
        }

        if(subForm!=null && StringUtils.isNotEmpty(subForm.getModule().getName())){
            CriteriaAPI.updateConditionField(subForm.getModule().getName(),subFormCriteria);
            long subFormCriteriaId = CriteriaAPI.addCriteria(subFormCriteria, AccountUtil.getCurrentOrg().getId());
            formRule.setSubFormCriteriaId(subFormCriteriaId);
        }

        FormRuleAPI.addFormRuleContext(formRule);

        return false;
    }

}

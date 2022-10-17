package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;

public class AddDefaultFormRuleSignupCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        FacilioForm facilioForm = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
        long formId = facilioForm.getId();

        if(facilioForm.getDefaultFormRules()!=null){
            String appLinkName = facilioForm.getAppLinkName();
            List<FormRuleContext> defaultFormRules = facilioForm.getDefaultFormRules();
            for(FormRuleContext formRuleContext:defaultFormRules ){
                List<String> getAppLinkNamesForRule = formRuleContext.getAppLinkNamesForRule();
                if(getAppLinkNamesForRule.contains(appLinkName)){
                    formRuleContext.setIsDefault(true);
                    formRuleContext.setFormId(formId);

                    FacilioChain chain = TransactionChainFactory.getAddFormRuleChain();
                    Context ruleContext = chain.getContext();

                    ruleContext.put(FormRuleAPI.FORM_RULE_CONTEXT,formRuleContext);

                    chain.execute();
                }
            }
        }
        return false;
    }
}

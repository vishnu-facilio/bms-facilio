package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.chain.FacilioChain;

public class CustomButtonAction extends FacilioAction {

    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    private CustomButtonRuleContext rule;
    public CustomButtonRuleContext getRule() {
        return rule;
    }
    public void setRule(CustomButtonRuleContext rule) {
        this.rule = rule;
    }

    public String addOrUpdateCustomButton() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateCustomButtonChain();
        return SUCCESS;
    }
}

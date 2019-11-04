package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

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

    private long ruleId;
    public long getRuleId() {
        return ruleId;
    }
    public void setRuleId(long ruleId) {
        this.ruleId = ruleId;
    }

    public String addOrUpdateCustomButton() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateCustomButtonChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);

        chain.execute();
        return SUCCESS;
    }

    public String getAllCustomButtons() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAllCustomButtonChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

        chain.execute();
        setResult(FacilioConstants.ContextNames.CUSTOM_BUTTONS, context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST));

        return SUCCESS;
    }

    public String deleteCustomButton() throws Exception {
        FacilioChain chain = TransactionChainFactory.getDeleteCustomButtonChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.RULE_ID, ruleId);
        chain.execute();

        return SUCCESS;
    }
}

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

    private long ruleId = -1;
    public long getRuleId() {
        return ruleId;
    }
    public void setRuleId(long ruleId) {
        this.ruleId = ruleId;
    }

    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String addOrUpdateCustomButton() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateCustomButtonChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);

        chain.execute();

        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
        return SUCCESS;
    }

    public String getAllCustomButtons() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAllCustomButtonChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

        chain.execute();
        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST));

        return SUCCESS;
    }

    public String deleteCustomButton() throws Exception {
        FacilioChain chain = TransactionChainFactory.getDeleteCustomButtonChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.RULE_ID, ruleId);
        chain.execute();

        return SUCCESS;
    }

    private int positionType;
    public int getPositionType() {
        return positionType;
    }
    public void setPositionType(int positionType) {
        this.positionType = positionType;
    }

    public String getAvailableButtons() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAvailableButtons();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.ID, id);
        context.put(FacilioConstants.ContextNames.POSITION_TYPE, positionType);
        chain.execute();

        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST));

        return SUCCESS;
    }
}

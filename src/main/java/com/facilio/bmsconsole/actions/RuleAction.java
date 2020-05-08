package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.RuleRollupCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.AlarmWorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class RuleAction extends  FacilioAction {

    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public AlarmWorkflowRuleContext getWorkflowRule() {
        return workflowRule;
    }

    public void setWorkflowRule(AlarmWorkflowRuleContext workflowRule) {
        this.workflowRule = workflowRule;
    }

    private AlarmWorkflowRuleContext workflowRule;
    private long ruleId;
    public long getRuleId() {
        return ruleId;
    }
    public void setRuleId(long ruleId) {
        this.ruleId = ruleId;
    }

    public String addRuleAction() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddModuleWorkflowRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
        chain.execute();

        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
        return SUCCESS;
    }

    public String updateRuleAction() throws Exception {
        FacilioChain chain = TransactionChainFactory.getUpdateModuleWorkflowRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
        chain.execute();

        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
        return SUCCESS;
    }

    public String fetchRuleActions() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getFetchRuleActionChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RULE_ID, ruleId);
        chain.execute();

        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST));
        return SUCCESS;
    }

    public String deleteRuleAction() throws Exception {
        FacilioChain chain = TransactionChainFactory.getDeleteCustomButtonChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.RULE_ID, ruleId);
        chain.execute();

        return SUCCESS;
    }

    private Long id;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    private RuleRollupCommand.RollupType rollupType;
    public int getRollupType() {
        if (rollupType != null) {
            return rollupType.getIndex();
        }
        return -1;
    }
    public void setRollupType(int rollupType) {
        this.rollupType = RuleRollupCommand.RollupType.valueOf(rollupType);
    }

    private Long startTime;
    public Long getStartTime() {
        return startTime;
    }
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    private Long endTime;
    public Long getEndTime() {
        return endTime;
    }
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getRollup() throws Exception {
        FacilioChain chain = TransactionChainFactory.getRuleRollupChain();
        Context context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        context.put(FacilioConstants.ContextNames.START_TIME, startTime);
        context.put(FacilioConstants.ContextNames.END_TIME, endTime);
        context.put(FacilioConstants.ContextNames.ROLL_UP_TYPE, rollupType);

        chain.execute();

        return SUCCESS;
    }
}

package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ModuleWorkflowRuleAction extends FacilioAction {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    private int ruleType;
    public int getRuleType() {
        return ruleType;
    }
    public void setRuleType(int ruleType) {
        this.ruleType = ruleType;
    }

    public String fetchWorkflowRules() throws Exception {
        FacilioChain c = ReadOnlyChainFactory.getCustomModuleWorkflowRulesChain();
        FacilioContext context = c.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        context.put(FacilioConstants.ContextNames.RULE_TYPE, ruleType);
        constructListContext(context);
        c.execute();

        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST));

        return SUCCESS;
    }

    private WorkflowRuleContext workflowRule;
    public WorkflowRuleContext getWorkflowRule() {
        return workflowRule;
    }
    public void setWorkflowRule(WorkflowRuleContext workflowRule) {
        this.workflowRule = workflowRule;
    }

    public String addRule() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddModuleWorkflowRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
        chain.execute();

        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
        return SUCCESS;
    }

    public String updateRule() throws Exception {
        FacilioChain chain = TransactionChainFactory.getUpdateModuleWorkflowRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
        chain.execute();

        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
        return SUCCESS;
    }

    private List<Long> ids;
    public List<Long> getIds() {
        return ids;
    }
    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String deleteRule() throws Exception {
        FacilioChain chain = TransactionChainFactory.getDeleteWorkflowRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, ids);
        chain.execute();

        return SUCCESS;
    }
}

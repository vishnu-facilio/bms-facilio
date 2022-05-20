package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.util.LicensingInfoUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Getter
    @Setter
    private int ruleId;

    public String fetchWorkflowRules() throws Exception {
        FacilioChain c = ReadOnlyChainFactory.getCustomModuleWorkflowRulesChain();
        FacilioContext context = c.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        context.put(FacilioConstants.ContextNames.RULE_TYPE, ruleType);
        constructListContext(context);
        c.execute();

        List<WorkflowRuleContext>workFlowRuleList= (List<WorkflowRuleContext>) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST);
        List<Map<String,Object>> approvalWorkFlowRuleList = new ArrayList<>();
        for(WorkflowRuleContext approvalWorkFlowRule:workFlowRuleList){
            Map<String,Object>approvalWorkflowRuleMap=new HashMap<>();
            approvalWorkflowRuleMap.put("name",approvalWorkFlowRule.getName());
            approvalWorkflowRuleMap.put("description",approvalWorkFlowRule.getDescription());
            approvalWorkflowRuleMap.put("active",approvalWorkFlowRule.getStatus()!=null ? approvalWorkFlowRule.getStatus():false);
            approvalWorkflowRuleMap.put("id",approvalWorkFlowRule.getId());
            approvalWorkFlowRuleList.add(approvalWorkflowRuleMap);
        }
        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, approvalWorkFlowRuleList);
        return SUCCESS;
    }

    public String fetchWorkflowRule() throws Exception {
        FacilioChain c = ReadOnlyChainFactory.getModuleWorkflowRuleChain();
        FacilioContext context = c.getContext();
        context.put(FacilioConstants.ContextNames.ID, getRuleId());
        c.execute();

        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE, context.get(FacilioConstants.ContextNames.WORKFLOW_RULE));

        return SUCCESS;
    }
    public String getCountForWorkFlowRules() throws Exception{
        FacilioChain chain = ReadOnlyChainFactory.getCountForWorkflowRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME,getModuleName());
        context.put(FacilioConstants.ContextNames.RULE_TYPE,ruleType);
        chain.execute();

        setResult(FacilioConstants.ContextNames.COUNT,context.get(FacilioConstants.ContextNames.COUNT));
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

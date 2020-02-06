package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.SLAEntityContext;
import com.facilio.bmsconsole.workflow.rule.SLAPolicyContext;
import com.facilio.bmsconsole.workflow.rule.SLAWorkflowCommitmentRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class SLAAction extends FacilioAction {

    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    private SLAWorkflowCommitmentRuleContext slaRule;
    public SLAWorkflowCommitmentRuleContext getSlaRule() {
        return slaRule;
    }
    public void setSlaRule(SLAWorkflowCommitmentRuleContext slaRule) {
        this.slaRule = slaRule;
    }

    private Long id;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    private Long slaPolicyId;
    public Long getSlaPolicyId() {
        return slaPolicyId;
    }
    public void setSlaPolicyId(Long slaPolicyId) {
        this.slaPolicyId = slaPolicyId;
    }

    public String getAllSLA() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAllSLAChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.SLA_POLICY_ID, slaPolicyId);

        chain.execute();

        setResult(FacilioConstants.ContextNames.SLA_RULE_MODULE_LIST, context.get(FacilioConstants.ContextNames.SLA_RULE_MODULE_LIST));
        return SUCCESS;
    }

    public String addOrUpdateSLA() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddSLAChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.SLA_RULE_MODULE, slaRule);
        context.put(FacilioConstants.ContextNames.SLA_POLICY_ID, slaPolicyId);

        chain.execute();

        setResult(FacilioConstants.ContextNames.SLA_RULE_MODULE, context.get(FacilioConstants.ContextNames.SLA_RULE_MODULE));
        return SUCCESS;
    }

    public String getSLA() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getSLAChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, getId());

        chain.execute();

        setResult(FacilioConstants.ContextNames.SLA_RULE_MODULE, context.get(FacilioConstants.ContextNames.SLA_RULE_MODULE));
        return SUCCESS;
    }

    public String deleteSLA() throws Exception {
        FacilioChain chain = TransactionChainFactory.getDelWorkflowRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RULE_ID, id);
        chain.execute();

        return SUCCESS;
    }

    private SLAPolicyContext slaPolicy;
    public SLAPolicyContext getSlaPolicy() {
        return slaPolicy;
    }
    public void setSlaPolicy(SLAPolicyContext slaPolicy) {
        this.slaPolicy = slaPolicy;
    }

    public String addOrUpdateSLAPolicy() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateSLAPolicyChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.SLA_POLICY, slaPolicy);
        chain.execute();

        setResult(FacilioConstants.ContextNames.SLA_POLICY, context.get(FacilioConstants.ContextNames.SLA_POLICY));

        return SUCCESS;
    }

    public String viewSLAPolicy() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.fetchWorkflowRuleWithActionsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        chain.execute();

        setResult(FacilioConstants.ContextNames.SLA_POLICY, context.get(FacilioConstants.ContextNames.WORKFLOW_RULE));

        return SUCCESS;
    }

    public String listSLAPolicy() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.fetchWorkflowRules();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_TYPE, WorkflowRuleContext.RuleType.SLA_POLICY_RULE);
        context.put(FacilioConstants.ContextNames.WORKFLOW_FETCH_CHILDREN, true);
        chain.execute();

        setResult(FacilioConstants.ContextNames.SLA_POLICY_LIST, context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST));

        return SUCCESS;
    }

    private SLAEntityContext slaEntity;
    public SLAEntityContext getSlaEntity() {
        return slaEntity;
    }
    public void setSlaEntity(SLAEntityContext slaEntity) {
        this.slaEntity = slaEntity;
    }

    public String addOrUpdateSLAEntity() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateSLAEntityChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.SLA_ENTITY, slaEntity);
        chain.execute();

        setResult(FacilioConstants.ContextNames.SLA_ENTITY, context.get(FacilioConstants.ContextNames.SLA_ENTITY));

        return SUCCESS;
    }

    public String listSLAEntity() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAllSLAEntityChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        chain.execute();

        setResult(FacilioConstants.ContextNames.SLA_ENTITY_LIST, context.get(FacilioConstants.ContextNames.SLA_ENTITY_LIST));

        return SUCCESS;
    }

    public String deleteSLAEntity() throws Exception {
        FacilioChain chain = TransactionChainFactory.deleteSLAEntityChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        chain.execute();

        return SUCCESS;
    }
}

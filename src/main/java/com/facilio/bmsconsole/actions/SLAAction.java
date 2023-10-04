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
import com.facilio.ims.handler.AuditLogHandler;
import com.facilio.modules.FieldUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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

    private List<SLAWorkflowCommitmentRuleContext> slaRuleList;
    public List<SLAWorkflowCommitmentRuleContext> getSlaRuleList() {
        return slaRuleList;
    }
    public void setSlaRuleList(List<SLAWorkflowCommitmentRuleContext> slaRuleList) {
        this.slaRuleList = slaRuleList;
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

    public String bulkAddOrUpdateSLA() throws Exception {
        FacilioChain chain = TransactionChainFactory.getBulkAddOrUpdateSLAChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SLA_RULE_MODULE_LIST, slaRuleList);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.SLA_POLICY_ID, slaPolicyId);
        chain.execute();

        setResult(FacilioConstants.ContextNames.SLA_RULE_MODULE_LIST, context.get(FacilioConstants.ContextNames.SLA_RULE_MODULE_LIST));
        return SUCCESS;
    }

    public String addOrUpdateSLA() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateSLAChain();
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
        context.put(FacilioConstants.ContextNames.SORTING_QUERY, "EXECUTION_ORDER ASC");
        chain.execute();

        List<WorkflowRuleContext> workFlowRuleList= (List<WorkflowRuleContext>)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST);
        List<Map<String,Object>> slaWorkFlowRuleList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(workFlowRuleList)) {
            for (WorkflowRuleContext slaWorkFlowRule : workFlowRuleList) {
                Map<String, Object> slaWorkFlowRuleMap = new HashMap<>();
                slaWorkFlowRuleMap.put("name", slaWorkFlowRule.getName());
                slaWorkFlowRuleMap.put("description", slaWorkFlowRule.getDescription());
                slaWorkFlowRuleMap.put("status", slaWorkFlowRule.getStatus());
                slaWorkFlowRuleMap.put("active", slaWorkFlowRule.isActive());
                slaWorkFlowRuleMap.put("id", slaWorkFlowRule.getId());
                slaWorkFlowRuleList.add(slaWorkFlowRuleMap);
            }
        }
        setResult(FacilioConstants.ContextNames.SLA_POLICY_LIST, slaWorkFlowRuleList);
        return SUCCESS;
    }

    private List<SLAPolicyContext.SLAPolicyEntityEscalationContext> escalationList;
    public List<SLAPolicyContext.SLAPolicyEntityEscalationContext> getEscalationList() {
        return escalationList;
    }
    public void setEscalationList(List<SLAPolicyContext.SLAPolicyEntityEscalationContext> escalationList) {
        this.escalationList = escalationList;
    }

    public String addSLAPolicyEscalations() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddSLAPolicyEscalationsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SLA_POLICY_ESCALATION_LIST, escalationList);
        context.put(FacilioConstants.ContextNames.SLA_POLICY_ID, slaPolicyId);
        chain.execute();

        setResult(FacilioConstants.ContextNames.SLA_POLICY_ESCALATION_LIST, context.get(FacilioConstants.ContextNames.SLA_POLICY_ESCALATION_LIST));

        return SUCCESS;
    }

    public String getSLAPolicyEscalations() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getSLAPolicyEscalationsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SLA_POLICY_ID, slaPolicyId);
        chain.execute();

        setResult(FacilioConstants.ContextNames.SLA_POLICY_ESCALATION_LIST, context.get(FacilioConstants.ContextNames.SLA_POLICY_ESCALATION_LIST));
        return SUCCESS;
    }

    private List<Long> ids;
    public List<Long> getIds() {
        return ids;
    }
    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String reorderSLAPolicy() throws Exception {
        FacilioChain chain = TransactionChainFactory.getReorderWorkflowRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
        context.put(FacilioConstants.ContextNames.RULE_TYPE, WorkflowRuleContext.RuleType.SLA_POLICY_RULE.getIntVal());
        chain.execute();

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

        SLAEntityContext slaEntityLog = FieldUtil.cloneBean(slaEntity, SLAEntityContext.class);

        chain.execute();

        long slaEntityId = (long) context.get(FacilioConstants.ContextNames.ID);
        setResult(FacilioConstants.ContextNames.SLA_ENTITY, context.get(FacilioConstants.ContextNames.SLA_ENTITY));

        String type= slaEntityLog.getId() > 0 ? "modified" : "created";
        sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("SLA Entity {%s} has been %s for %s module", slaEntityLog.getName(),type, moduleName),
                slaEntityLog.getDescription(),
                AuditLogHandler.RecordType.SETTING,
                "AutomationRule",slaEntityId)
                .setActionType(slaEntityLog.getId() > 0 ? AuditLogHandler.ActionType.UPDATE : AuditLogHandler.ActionType.ADD)
                .setLinkConfig(((Function<Void, String>) o -> {
                    JSONArray array = new JSONArray();
                    JSONObject json = new JSONObject();
                    json.put("id", slaEntityId);
                    json.put("moduleName", moduleName);
                    json.put("navigateTo", "AutomationRule");
                    array.add(json);
                    return array.toJSONString();
                }).apply(null))
        );
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

    public String addSLAPolicyWithChildren() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddSLAPolicyWithChildrenChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SLA_POLICY, slaPolicy);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        chain.execute();

        setResult(FacilioConstants.ContextNames.SLA_POLICY, context.get(FacilioConstants.ContextNames.SLA_POLICY));
        sendAuditLog(slaPolicy);
        return SUCCESS;
    }

    public void sendAuditLog(SLAPolicyContext slaPolicy) throws Exception {

        String type = slaPolicy.getCreatedTime() == slaPolicy.getModifiedTime() ? "created":"updated";
        sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("SLA Rule {%s} has been %s for %s module", slaPolicy.getName(),type, slaPolicy.getModuleName()),
                slaPolicy.getDescription(),
                AuditLogHandler.RecordType.SETTING,
                "AutomationRule",slaPolicy.getId())
                .setActionType(slaPolicy.getCreatedTime()==slaPolicy.getModifiedTime() ? AuditLogHandler.ActionType.ADD : AuditLogHandler.ActionType.UPDATE)
                .setLinkConfig(((Function<Void, String>) o -> {
                    JSONArray array = new JSONArray();
                    JSONObject json = new JSONObject();
                    json.put("id", slaPolicy.getId());
                    json.put("moduleName", moduleName);
                    json.put("ruleType",slaPolicy.getRuleType());
                    json.put("navigateTo", "AutomationRule");
                    array.add(json);
                    return array.toJSONString();
                }).apply(null))
        );

    }

    public String getSLAPolicyWithChildren() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getSLAPolicyWithChildrenChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SLA_POLICY_ID, slaPolicyId);
        chain.execute();

        setResult(FacilioConstants.ContextNames.SLA_POLICY, context.get(FacilioConstants.ContextNames.SLA_POLICY));

        return SUCCESS;
    }
}

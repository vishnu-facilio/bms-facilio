package com.facilio.bmsconsole.actions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.handler.AuditLogHandler;
import com.facilio.modules.FacilioModule;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.function.Function;

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
    public String searchText;
    public String getSearchText() {
        return searchText;
    }
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
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
        context.put(FacilioConstants.ContextNames.SEARCH,getSearchText());
        constructListContext(context);
        c.execute();

        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST));

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
        sendAuditLog(workflowRule);
        return SUCCESS;
    }

    public String updateRule() throws Exception {
        FacilioChain chain = TransactionChainFactory.getUpdateModuleWorkflowRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
        chain.execute();

        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
        sendAuditLog(workflowRule);
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
        List<WorkflowRuleContext> workflowRules = (List<WorkflowRuleContext>) context.get(FacilioConstants.ContextNames.WORKFLOW_RULES);
        if(workflowRules != null) {
            long moduleID = workflowRules.get(0).getModuleId();
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleID);
            String subject = ids.size() > 1 ? String.format("%d AutomationRules has been deleted for %s module", ids.size(), module.getDisplayName()) : String.format("AutomationRule %s has been deleted for %s module", workflowRules.get(0).getName(), module.getDisplayName());
            sendAuditLogs(new AuditLogHandler.AuditLogContext(subject,
                    workflowRules.get(0).getDescription(),
                    AuditLogHandler.RecordType.SETTING,
                    "AutomationRule", 0)
                    .setActionType(AuditLogHandler.ActionType.DELETE));
        }


        return SUCCESS;
    }

    public void sendAuditLog(WorkflowRuleContext workflowRule) throws Exception {

        String type=workflowRule.getCreatedTime()==workflowRule.getModifiedTime() ? "created":"updated";
        sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("AutomationRule {%s} has been %s for %s module", workflowRule.getName(),type, workflowRule.getModule().getDisplayName()),
                workflowRule.getDescription(),
                AuditLogHandler.RecordType.SETTING,
                "AutomationRule",workflowRule.getId())
                .setActionType(workflowRule.getCreatedTime()==workflowRule.getModifiedTime() ? AuditLogHandler.ActionType.ADD : AuditLogHandler.ActionType.UPDATE)
                .setLinkConfig(((Function<Void, String>) o -> {
                    JSONArray array = new JSONArray();
                    JSONObject json = new JSONObject();
                    json.put("id", workflowRule.getId());
                    json.put("moduleName", moduleName);
                    json.put("ruleType",workflowRule.getRuleType());
                    json.put("navigateTo", "AutomationRule");
                    array.add(json);
                    return array.toJSONString();
                }).apply(null))
        );

    }
}

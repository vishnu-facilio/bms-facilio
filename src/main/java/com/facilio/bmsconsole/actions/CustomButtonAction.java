package com.facilio.bmsconsole.actions;

import com.amazonaws.regions.Regions;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.interceptors.AuthInterceptor;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import com.facilio.wmsv2.handler.AuditLogHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;
import java.util.function.Function;
import org.json.simple.JSONArray;

import java.util.List;

@Log4j
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

        String type=rule.getCreatedTime()==rule.getModifiedTime() ? "created":"updated";
        sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("CustomButton {%s} has been %s for %s module", rule.getName(),type, rule.getModule().getDisplayName()),
                rule.getDescription(),
                AuditLogHandler.RecordType.SETTING,
                "CustomButton",rule.getId())
                .setActionType(rule.getCreatedTime()==rule.getModifiedTime() ? AuditLogHandler.ActionType.ADD:AuditLogHandler.ActionType.UPDATE)
                .setLinkConfig(((Function<Void, String>) o -> {
                    JSONArray array = new JSONArray();
                    JSONObject json = new JSONObject();
                                json.put("id", rule.getId());
                                json.put("moduleName", moduleName);
                                json.put("ruleType", rule.getRuleType());
                                json.put("navigateTo", "CustomButton");
                                array.add(json);
                                return array.toJSONString();
                            }).apply(null))
                        );


        return SUCCESS;
    }

    public String getAllCustomButtons() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAllCustomButtonChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

        JSONObject pagination = new JSONObject();
        pagination.put("page", getPage());
        pagination.put("perPage", getPerPage());
        if (getPerPage() < 0) {
            pagination.put("perPage", 5000);
        }
        context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());

        chain.execute();
        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST));

        return SUCCESS;
    }

    public String deleteCustomButton() throws Exception {
        FacilioChain chain = TransactionChainFactory.getDeleteCustomButtonChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.RULE_ID, ruleId);
        chain.execute();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        WorkflowRuleContext workflowRule= (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
        if(workflowRule != null) {
            FacilioModule module = modBean.getModule(workflowRule.getModuleId());
            sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("CustomButton %s has been deleted for %s module", workflowRule.getName(), module.getDisplayName()), workflowRule.getDescription(),
                    AuditLogHandler.RecordType.SETTING,
                    "CustomButton", workflowRule.getId())
                    .setActionType(AuditLogHandler.ActionType.DELETE));
        }

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

        if (Regions.US_WEST_2.getName().equals(FacilioProperties.getRegion())) {
            LOGGER.info("ACtion class of getAvailableButtons called");
        }

        FacilioChain chain = ReadOnlyChainFactory.getAvailableButtons();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.ID, id);
        context.put(FacilioConstants.ContextNames.POSITION_TYPE, positionType);
        chain.execute();

        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST));

        if (Regions.US_WEST_2.getName().equals(FacilioProperties.getRegion())) {
            LOGGER.info("Action class done for getAvailableButton" + AuthInterceptor.getResponseCode());
        }

        return SUCCESS;
    }

    private List<Long> ids;
    public List<Long> getIds() {
        return ids;
    }
    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String reorderCustomButtons() throws Exception {
        FacilioChain chain = TransactionChainFactory.getReorderWorkflowRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
        context.put(FacilioConstants.ContextNames.RULE_TYPE, WorkflowRuleContext.RuleType.CUSTOM_BUTTON.getIntVal());
        chain.execute();

        return SUCCESS;
    }

    @Getter @Setter
    private List<Long> recordIds;
    @Getter @Setter
    private List<Integer> positionTypes;
    public String getAvailableButtonsForRecords() throws Exception{
        FacilioChain chain = ReadOnlyChainFactory.getCustomButtonsListForRecords();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,recordIds);
        context.put(FacilioConstants.ContextNames.POSITION_TYPE,positionTypes);
        chain.execute();
        setResult(FacilioConstants.ContextNames.CUSTOM_BUTTONS,context.get(FacilioConstants.ContextNames.CUSTOM_BUTTONS));
        setResult(Constants.CUSTOM_BUTTONS_RECORDS,context.get(Constants.CUSTOM_BUTTONS_RECORDS));
        return SUCCESS;
    }

    public String getCustomButton() throws Exception{
        FacilioChain chain = ReadOnlyChainFactory.getCustomButtonChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, ruleId);
        chain.execute();

        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE, context.get(FacilioConstants.ContextNames.WORKFLOW_RULE));
        return SUCCESS;
    }
}

package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.moduleBuilder.util.ResponseFormatUtil;
import com.facilio.v3.context.Constants;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

public class SystemButtonAction extends FacilioAction{

    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    private SystemButtonRuleContext rule;
    public SystemButtonRuleContext getRule() {
        return rule;
    }
    public void setRule(SystemButtonRuleContext rule) {
        this.rule = rule;
    }

    private long id;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private int positionType;
    public void setPositionType(int positionType) {
        this.positionType = positionType;
    }
    public int getPositionType() {
        return positionType;
    }

    public String updateSystemButton() throws Exception{
        FacilioChain chain = TransactionChainFactory.getUpdateSystemButtonChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);

        chain.execute();

        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE,rule);
        return SUCCESS;
    }

    public String getAllSystemButtons() throws Exception{
        FacilioChain chain = ReadOnlyChainFactory.getAllSystemButtonChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        chain.execute();

        List<SystemButtonRuleContext> systemButtonList = (List<SystemButtonRuleContext>) ResponseFormatUtil.formatWorkflowRulesBasedOnResponseFields(
                (List<SystemButtonRuleContext>) context.get(FacilioConstants.ContextNames.SYSTEM_BUTTONS),
                Arrays.asList("id","name","description","positionType","positionTypeEnum","status"),
                false,
                new SystemButtonRuleContext());

        setResult(FacilioConstants.ContextNames.SYSTEM_BUTTONS,systemButtonList);
        return SUCCESS;
    }

    public String getSystemButton() throws Exception{
        FacilioChain chain = ReadOnlyChainFactory.getSystemButtonChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.ID,getId());
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        chain.execute();

        setResult(FacilioConstants.ContextNames.SYSTEM_BUTTON,context.get(FacilioConstants.ContextNames.SYSTEM_BUTTON));

        return SUCCESS;
    }

    public String getAvailableSystemButton() throws Exception{
        FacilioChain chain = ReadOnlyChainFactory.getAvailableSystemButtonChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.ID,id);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        context.put(FacilioConstants.ContextNames.POSITION_TYPE,positionType);
        chain.execute();

        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST));

        return SUCCESS;
    }

    @Getter
    @Setter
    private List<Long> recordIds;

    public String getAvailableButtonsForList() throws Exception{
        FacilioChain chain = ReadOnlyChainFactory.getAvailableButtonsForListChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,recordIds);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        context.put(FacilioConstants.ContextNames.POSITION_TYPE,positionType);
        chain.execute();

        setResult(FacilioConstants.ContextNames.SYSTEM_BUTTONS,context.get(FacilioConstants.ContextNames.SYSTEM_BUTTONS));
        setResult(Constants.SYSTEM_BUTTONS_RECORDS,context.get(Constants.SYSTEM_BUTTONS_RECORDS));
        return SUCCESS;
    }

}

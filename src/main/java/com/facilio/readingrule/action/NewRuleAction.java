package com.facilio.readingrule.action;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.AlarmWorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
@Setter
public class NewRuleAction extends V3Action {

    private String moduleName;
    private AlarmWorkflowRuleContext workflowRule;

    private Map<String,Object> faultToWorkorder;
    private long ruleId;
    private long actionId;
//    private Long startTime;
//    private Long endTime;

    public String addRuleAction() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddModuleWorkflowRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
        chain.execute();

        setData(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
        return SUCCESS;
    }

    public String updateRuleAction() throws Exception {
        FacilioChain chain = TransactionChainFactory.getUpdateModuleWorkflowRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
        chain.execute();

        setData(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
        return SUCCESS;
    }

    public String fetchRuleActions() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getFetchRuleActionChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RULE_ID, ruleId);
        chain.execute();

        setData(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST));
        return SUCCESS;
    }

    public String deleteRuleAction() throws Exception {
        FacilioChain chain = TransactionChainFactory.getDeleteCustomButtonChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.RULE_ID, ruleId);
        chain.execute();

        setData((JSONObject) new JSONObject().put("result", true));
        return SUCCESS;
    }

    public String addRuleWorkOrderAction() throws Exception{
        FacilioChain chain =TransactionChainFactory.addRuleToWOWorkFlowChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_MODULE, faultToWorkorder);
        context.put(FacilioConstants.ContextNames.RULE_ID,ruleId);
        chain.execute();
        setData((JSONObject) new JSONObject().put("result", true));
        return SUCCESS;
    }

    public String updateRuleWorkOrderAction() throws Exception{
        FacilioChain chain =TransactionChainFactory.updateRuleToWorkFlowChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ReadingRules.FAULT_TO_WORKORDER, faultToWorkorder);
        context.put(FacilioConstants.ContextNames.RULE_ID,ruleId);
        chain.execute();
        setData((JSONObject) new JSONObject().put("result", true));
        return SUCCESS;
    }
    public String fetchRuleWorkOrderAction() throws Exception{
        FacilioChain chain =TransactionChainFactory.fetchRuleToWorkflowChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RULE_ID,ruleId);
        chain.execute();
        setData(FacilioConstants.ReadingRules.FAULT_TO_WORKORDER, context);
        return SUCCESS;
    }

    public String updateRuleWfStatus() throws Exception{
        FacilioChain chain =TransactionChainFactory.wfRuleStatusChangeFromRule();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RULE_ID,ruleId);
        chain.execute();
        setData((JSONObject) new JSONObject().put("result", true));
        return SUCCESS;
    }


//    private RuleRollupCommand.RollupType rollupType;
//    public int getRollupType() {
//        if (rollupType != null) {
//            return rollupType.getIndex();
//        }
//        return -1;
//    }
//    public void setRollupType(int rollupType) {
//        this.rollupType = RuleRollupCommand.RollupType.valueOf(rollupType);
//    }

//    public String getRollup() throws Exception {
//        FacilioChain chain = TransactionChainFactory.getRuleRollupChain();
//        Context context = chain.getContext();
//        context.put(FacilioConstants.ContextNames.ID, id);
//        context.put(FacilioConstants.ContextNames.START_TIME, startTime);
//        context.put(FacilioConstants.ContextNames.END_TIME, endTime);
//        context.put(FacilioConstants.ContextNames.ROLL_UP_TYPE, rollupType);
//
//        chain.execute();
//
//        return SUCCESS;
//    }

}

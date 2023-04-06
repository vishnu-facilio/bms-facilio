package com.facilio.readingrule.faulttowo.command;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.readingrule.faulttowo.RuleWoAPI;
import com.facilio.readingrule.faulttowo.ReadingRuleWorkOrderRelContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;

public class UpdateRuleWoDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Object> ruleWorkOrder = (Map<String, Object>) context.get(FacilioConstants.ReadingRules.FAULT_TO_WORKORDER);

        Long ruleId = (Long) context.get(FacilioConstants.ContextNames.RULE_ID);
        if (MapUtils.isNotEmpty(ruleWorkOrder)) {
            for (Map.Entry prop : ruleWorkOrder.entrySet()) {
                Map<String, Object> ruleWoDetail = (Map<String, Object>) prop.getValue();

                if (ruleWoDetail != null) {
                    ReadingRuleWorkOrderRelContext ruleWoCtx = FieldUtil.getAsBeanFromMap((Map<String, Object>) ruleWoDetail.get("ruleToWo"), ReadingRuleWorkOrderRelContext.class);
                    ReadingRuleWorkOrderRelContext oldRecord=RuleWoAPI.getRuleWoDetailsFromId(ruleWoCtx.getId()).get(0);
                    if (ruleWoDetail.get("ruleToWo") != null) {
                        WorkflowRuleContext wfRule = FieldUtil.getAsBeanFromMap((Map<String, Object>) ruleWoDetail.get(FacilioConstants.ContextNames.WORKFLOW_RULE), WorkflowRuleContext.class);
                        wfRule.setId(ruleWoCtx.getWorkFlowRuleId());
                        updateWorkFlowDetails(wfRule);
                    }
                    RuleWoAPI.checkAndUpdateRuleWoDetails(ruleWoCtx,oldRecord);
                }
            }
        }
        return false;
    }

    private void updateWorkFlowDetails(WorkflowRuleContext wfRule) throws Exception {
        FacilioChain chain = TransactionChainFactory.updateWorkflowRuleChain();
        FacilioContext addWorkflowContext = chain.getContext();
        addWorkflowContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, wfRule);
        addWorkflowContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.NEW_READING_ALARM);
        chain.execute();
    }
}

package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class PublishStateFlowCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long stateFlowId = (Long) context.get(FacilioConstants.ContextNames.ID);
        if (stateFlowId > 0) {
            StateFlowRuleContext stateFlowContext = StateFlowRulesAPI.getStateFlowContext(stateFlowId);
            if (stateFlowContext.isDraft() && stateFlowContext.getDraftParentId() == -1) {
                // newly created stateflow, that is not live yet
                stateFlowContext.setDraft(false);
                stateFlowContext.setPublishedDate(DateTimeUtil.getCurrenTime());
                FacilioChain chain = TransactionChainFactory.updateWorkflowRuleChain();
                FacilioContext updateWorkflowContext = chain.getContext();
                updateWorkflowContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, stateFlowContext);
                chain.execute();
                return false;
            }

            StateFlowRuleContext draftStateFlowForParent = StateFlowRulesAPI.getDraftStateFlowForParent(stateFlowId);
            if (draftStateFlowForParent == null) {
                throw new IllegalArgumentException("There is no draft");
            }

            List<WorkflowRuleContext> allStateTransitionList = StateFlowRulesAPI.getAllStateTransitionList(stateFlowContext);
            if (CollectionUtils.isNotEmpty(allStateTransitionList)) {
                for (WorkflowRuleContext stateTransition : allStateTransitionList) {
                    StateFlowRulesAPI.deleteStateTransition(stateFlowContext.getId(), stateTransition.getId());
                }
            }

            List<WorkflowRuleContext> draftStateFlows = StateFlowRulesAPI.getAllStateTransitionList(draftStateFlowForParent);
            if (CollectionUtils.isNotEmpty(draftStateFlows)) {
                for (WorkflowRuleContext workflowRuleContext : draftStateFlows) {
                    StateFlowRulesAPI.deleteStateTransition(draftStateFlowForParent.getId(), workflowRuleContext.getId());

                    StateflowTransitionContext stateflowTransitionContext = (StateflowTransitionContext) workflowRuleContext;
                    stateflowTransitionContext.setStateFlowId(stateFlowContext.getId());
                    stateflowTransitionContext.setId(-1);

                    FacilioChain ruleChain = TransactionChainFactory.addWorkflowRuleChain();
                    FacilioContext ruleContext = ruleChain.getContext();
                    ruleContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, stateflowTransitionContext);
                    ruleChain.execute();
                }
            }

            StateFlowRulesAPI.deleteWorkflowRule(draftStateFlowForParent.getId());


            stateFlowContext.setPublishedDate(DateTimeUtil.getCurrenTime());
            FacilioChain chain = TransactionChainFactory.updateWorkflowRuleChain();
            FacilioContext updateWorkflowContext = chain.getContext();
            updateWorkflowContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, stateFlowContext);
            chain.execute();

        }
        return false;
    }
}

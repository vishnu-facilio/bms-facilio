package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.SLAWorkflowAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class GetSLAPolicyWithChildrenCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long slaPolicyId = (Long) context.get(FacilioConstants.ContextNames.SLA_POLICY_ID);
        if (slaPolicyId > 0) {
            SLAPolicyContext slaPolicyContext = (SLAPolicyContext) WorkflowRuleAPI.getWorkflowRule(slaPolicyId);
            if (slaPolicyContext == null) {
                throw new IllegalArgumentException("SLA Policy not found");
            }

            List<SLAPolicyContext.SLAPolicyEntityEscalationContext> slaPolicyEntityEscalations = SLAWorkflowAPI.getSLAPolicyEntityEscalations(slaPolicyId);
            if (CollectionUtils.isNotEmpty(slaPolicyEntityEscalations)) {
                List<SLAWorkflowEscalationContext> allEscalations = new ArrayList<>();
                for (SLAPolicyContext.SLAPolicyEntityEscalationContext escalation : slaPolicyEntityEscalations) {
                    List<SLAWorkflowEscalationContext> levels = escalation.getLevels();
                    for (SLAWorkflowEscalationContext level : levels) {
                        List<ActionContext> actions = level.getActions();
                        level.setActions(ActionAPI.addActions(actions, slaPolicyContext, false));
                    }
                    allEscalations.addAll(levels);
                }
            }
            slaPolicyContext.setEscalations(slaPolicyEntityEscalations);

            FacilioChain allSLAChain = ReadOnlyChainFactory.getAllSLAChain();
            FacilioContext slaContext = allSLAChain.getContext();
            slaContext.put(FacilioConstants.ContextNames.SLA_POLICY_ID, slaPolicyId);
            allSLAChain.execute();

            List<SLAWorkflowCommitmentRuleContext> slaCommitments = (List<SLAWorkflowCommitmentRuleContext>) slaContext.get(FacilioConstants.ContextNames.SLA_RULE_MODULE_LIST);
            slaPolicyContext.setCommitments(slaCommitments);

            context.put(FacilioConstants.ContextNames.SLA_POLICY, slaPolicyContext);
        }
        return false;
    }
}

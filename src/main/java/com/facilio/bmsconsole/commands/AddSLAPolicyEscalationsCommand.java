package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.SLAWorkflowAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.SLAPolicyContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AddSLAPolicyEscalationsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<SLAPolicyContext.SLAPolicyEntityEscalationContext> slaEscalations = (List<SLAPolicyContext.SLAPolicyEntityEscalationContext>) context.get(FacilioConstants.ContextNames.SLA_POLICY_ESCALATION_LIST);
        Long slaPolicyId = (Long) context.get(FacilioConstants.ContextNames.SLA_POLICY_ID);
        if (CollectionUtils.isNotEmpty(slaEscalations) && (slaPolicyId != null && slaPolicyId > 0)) {
            SLAPolicyContext slaPolicy = (SLAPolicyContext) WorkflowRuleAPI.getWorkflowRule(slaPolicyId);
            SLAWorkflowAPI.deleteSLAPolicyEscalation(slaPolicy);
            slaPolicy.setEscalations(slaEscalations);
            SLAWorkflowAPI.addEscalations(slaPolicy);
        }
        return false;
    }
}

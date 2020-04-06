package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.SLAWorkflowAPI;
import com.facilio.bmsconsole.workflow.rule.SLAPolicyContext;
import com.facilio.bmsconsole.workflow.rule.SLAWorkflowEscalationContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class GetSLAPolicyEscalationsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long slaPolicyId = (Long) context.get(FacilioConstants.ContextNames.SLA_POLICY_ID);
        if (slaPolicyId != null && slaPolicyId > 0) {
            List<SLAPolicyContext.SLAPolicyEntityEscalationContext> escalations = SLAWorkflowAPI.getSLAPolicyEntityEscalations(slaPolicyId);
            if (CollectionUtils.isNotEmpty(escalations)) {
                List<SLAWorkflowEscalationContext> allEscalations = new ArrayList<>();
                for (SLAPolicyContext.SLAPolicyEntityEscalationContext escalation : escalations) {
                    allEscalations.addAll(escalation.getLevels());
                }
            }
            context.put(FacilioConstants.ContextNames.SLA_POLICY_ESCALATION_LIST, escalations);
        }
        return false;
    }
}

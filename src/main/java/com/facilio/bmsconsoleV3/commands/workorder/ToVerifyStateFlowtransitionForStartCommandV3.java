package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ToVerifyStateFlowtransitionForStartCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3WorkOrderContext> wos = (List<V3WorkOrderContext>) context.get(FacilioConstants.TicketActivity.OLD_TICKETS);


        if(CollectionUtils.isNotEmpty(wos)){

            Long currentTransitionId = (Long) context.get(FacilioConstants.ContextNames.TRANSITION_ID);

            if (currentTransitionId != null && wos != null && wos.size() == 1) {
                if (wos.get(0).getQrEnabled() != null && wos.get(0).getQrEnabled() && wos.get(0).getResource() != null) {
                    StateflowTransitionContext stateflowTransition = (StateflowTransitionContext) WorkflowRuleAPI.getWorkflowRule(currentTransitionId);
                    if (stateflowTransition != null && stateflowTransition.getName().equals("Start Work")) {
                        context.put(FacilioConstants.ContextNames.RESOURCE_ID, wos.get(0).getResource().getId());
                        context.put(FacilioConstants.ContextNames.SHOULD_VERIFY_QR, true);
                    }
                }
            }
        }
        return false;
    }
}

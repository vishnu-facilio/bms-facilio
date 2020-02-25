package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.SLAWorkflowAPI;
import com.facilio.bmsconsole.workflow.rule.SLAPolicyContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class AddOrUpdateSLAPolicyWithChildrenCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        SLAPolicyContext slaPolicy = (SLAPolicyContext) context.get(FacilioConstants.ContextNames.SLA_POLICY);
        if (StringUtils.isNotEmpty(moduleName) && slaPolicy != null) {
            FacilioChain slaPolicyChain = TransactionChainFactory.getAddOrUpdateSLAPolicyChain();
            FacilioContext slaPolicyContext = slaPolicyChain.getContext();
            slaPolicyContext.put(FacilioConstants.ContextNames.SLA_POLICY, slaPolicy);
            slaPolicyContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
            slaPolicyChain.execute();

            SLAWorkflowAPI.deleteAllSLACommitments(slaPolicy);

            FacilioChain slaChain = TransactionChainFactory.getBulkAddOrUpdateSLAChain();
            FacilioContext slaContext = slaChain.getContext();
            slaContext.put(FacilioConstants.ContextNames.SLA_RULE_MODULE_LIST, slaPolicy.getCommitments());
            slaContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
            slaContext.put(FacilioConstants.ContextNames.SLA_POLICY_ID, slaPolicy.getId());
            slaChain.execute();

            FacilioChain slaEscalationChain = TransactionChainFactory.getAddSLAPolicyEscalationsChain();
            FacilioContext slaEscalationContext = slaEscalationChain.getContext();
            slaEscalationContext.put(FacilioConstants.ContextNames.SLA_POLICY_ESCALATION_LIST, slaPolicy.getEscalations());
            slaEscalationContext.put(FacilioConstants.ContextNames.SLA_POLICY_ID, slaPolicy.getId());
            slaEscalationChain.execute();
        }
        return false;
    }
}

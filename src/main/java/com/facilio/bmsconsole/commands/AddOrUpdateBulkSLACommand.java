package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.workflow.rule.SLAWorkflowCommitmentRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AddOrUpdateBulkSLACommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<SLAWorkflowCommitmentRuleContext> slaRuleList = (List<SLAWorkflowCommitmentRuleContext>) context.get(FacilioConstants.ContextNames.SLA_RULE_MODULE_LIST);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Long parentSLAPolicyId = (Long) context.get(FacilioConstants.ContextNames.SLA_POLICY_ID);

        if (CollectionUtils.isNotEmpty(slaRuleList)) {
            int counter = 1;
            for (SLAWorkflowCommitmentRuleContext slaCommitment : slaRuleList) {

                // temp fix for executionOrder
                slaCommitment.setExecutionOrder(counter++);

                FacilioChain addOrUpdateSLAChain = TransactionChainFactory.getAddOrUpdateSLAChain();
                FacilioContext addOrUpdateSLAChainContext = addOrUpdateSLAChain.getContext();
                addOrUpdateSLAChainContext.put(FacilioConstants.ContextNames.SLA_RULE_MODULE, slaCommitment);
                addOrUpdateSLAChainContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
                addOrUpdateSLAChainContext.put(FacilioConstants.ContextNames.SLA_POLICY_ID, parentSLAPolicyId);
                addOrUpdateSLAChain.execute();
            }
        }
        return false;
    }
}

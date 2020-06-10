package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class FetchStateflowAndSlaCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(Constants.MODULE_NAME);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);

        if (CollectionUtils.isNotEmpty(wos)) {
            V3WorkOrderContext workOrder = wos.get(0);

            if (workOrder.getStateFlowId() > 0) {
                StateFlowRuleContext stateFlowRuleContext = (StateFlowRuleContext) WorkflowRuleAPI.getWorkflowRule(workOrder.getStateFlowId());
                if (stateFlowRuleContext != null) {
                    workOrder.setStateFlowRule(stateFlowRuleContext);
                }
            }
            if (workOrder.getSlaPolicyId() > 0) {
                WorkflowRuleContext slaRule = WorkflowRuleAPI.getWorkflowRule(workOrder.getSlaPolicyId());
                if (slaRule != null) {
                    workOrder.setSlaRule(slaRule);
                }
            }
        }
        return false;
    }
}

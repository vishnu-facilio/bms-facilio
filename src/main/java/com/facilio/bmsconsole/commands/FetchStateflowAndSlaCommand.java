package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class FetchStateflowAndSlaCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		
		if (workOrder != null) {
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

package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class AddorUpdateRecordRuleCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkflowRuleContext recordRule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (recordRule != null) {
			
			if (recordRule.getExecutionOrder() == -1) {
				recordRule.setExecutionOrder(0);
			}
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, recordRule);
			Chain ruleChain;
			if (recordRule.getId() > 0) {
				ruleChain = TransactionChainFactory.updateWorkflowRuleChain();
			} else {
				ruleChain = TransactionChainFactory.addWorkflowRuleChain();
			}
			ruleChain.execute(context);
		}
		return false;
	}

}

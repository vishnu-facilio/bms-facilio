package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.StateFlowContext;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransistionContext;
import com.facilio.constants.FacilioConstants;

public class AddOrUpdateStateFlowCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		StateFlowContext stateFlow = (StateFlowContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (stateFlow != null) {
//			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if (stateFlow.getId() > 0) {
//				updateStateFlow();
			} else {
				StateFlowRuleContext ruleContext = stateFlow.constructRule();
				
				context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, ruleContext);
				Chain ruleChain = TransactionChainFactory.addWorkflowRuleChain();
				ruleChain.execute(context);
				
//				long ruleId = WorkflowRuleAPI.addWorkflowRule(ruleContext);
				stateFlow.setId(ruleContext.getId());
				
				StateFlowRulesAPI.addOrUpdateStateFlow(stateFlow);
			}
		}
		return false;
	}

}

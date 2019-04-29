package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.StateFlowContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransistionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdateStateFlowCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		StateFlowContext stateFlow = (StateFlowContext) context.get(FacilioConstants.ContextNames.RECORD);
		List<ActionContext> actionList = (List<ActionContext>) context.get(FacilioConstants.ContextNames.ACTIONS_LIST);
		if (stateFlow != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule facilioModule = modBean.getModule(stateFlow.getModuleId());
			if (facilioModule == null) {
				facilioModule = modBean.getModule(stateFlow.getModuleName());
			}
			if (facilioModule == null) {
				throw new Exception("Invalid Module");
			}
			stateFlow.setModuleId(facilioModule.getModuleId());
			
			StateFlowRuleContext ruleContext = stateFlow.constructRule();
			ruleContext.setActions(actionList);
			
			boolean add = false;
			if (stateFlow.getId() < 0) {
				add = true;
			}
			
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, ruleContext);
			Chain ruleChain;
			if (add) {
				ruleChain = TransactionChainFactory.addWorkflowRuleChain();
			} else {
				ruleChain = TransactionChainFactory.updateWorkflowRuleChain();
			}
			ruleChain.execute(context);
			
			if (add) {
				stateFlow.setId(ruleContext.getId());
			}
			StateFlowRulesAPI.addOrUpdateStateFlow(stateFlow, add);
		}
		return false;
	}

}

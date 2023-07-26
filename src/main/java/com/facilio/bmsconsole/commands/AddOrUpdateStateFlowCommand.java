package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class AddOrUpdateStateFlowCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		StateFlowRuleContext stateFlow = (StateFlowRuleContext) context.get(FacilioConstants.ContextNames.RECORD);
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

			StateFlowRuleContext defaultStateFlow = StateFlowRulesAPI.getDefaultStateFlow(facilioModule);
			if (defaultStateFlow == null) {
				stateFlow.setDefaltStateFlow(true);
			}
			else if (defaultStateFlow.getId() == stateFlow.getId()) {
				stateFlow.setDefaltStateFlow(true);
			}
			else {
				stateFlow.setDefaltStateFlow(false);
			}
			
			if (stateFlow.getExecutionOrder() == -1) {
				stateFlow.setExecutionOrder(0);
			}

			stateFlow.setActivityType(EventType.CREATE);
			stateFlow.setModuleId(facilioModule.getModuleId());
			stateFlow.setRuleType(RuleType.STATE_FLOW);

			boolean add = false;
			boolean publish = (boolean) context.getOrDefault(FacilioConstants.ContextNames.STATE_FLOW_PUBLISH,false);


			if (stateFlow.getId() < 0) {
				add = true;
				if (publish){
					stateFlow.setDraft(stateFlow.isDraft());
				}
				else if (!stateFlow.isDefaltStateFlow()) {
					stateFlow.setDraft(true);
				}
			}
			
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, stateFlow);
			FacilioChain ruleChain;
			if (add) {
				ruleChain = TransactionChainFactory.addWorkflowRuleChain();
			} else {
				ruleChain = TransactionChainFactory.updateWorkflowRuleChain();
			}
			ruleChain.execute(context);

			if (add) {
				StateFlowRulesAPI.updateStateTransitionExecutionOrder(facilioModule, RuleType.STATE_FLOW);
			}
		}
		return false;
	}
}

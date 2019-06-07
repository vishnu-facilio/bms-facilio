package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class ExecuteStateTransitionsCommand extends ExecuteAllWorkflowsCommand {
	private static final long serialVersionUID = 1L;

	public ExecuteStateTransitionsCommand(RuleType ruleTypes) {
		super(ruleTypes);
	}
	
	@Override
	public boolean execute(Context context) throws Exception {
		context.put(FacilioConstants.ContextNames.STATE_TRANSITION_ONLY_CONDITIONED_CHECK, true);
		return super.execute(context);
	}
	
//	@Override
//	protected List<WorkflowRuleContext> getWorkFlowRules(FacilioModule module, List<EventType> activities,
//			Criteria parentCriteria) throws Exception {
//		return super.getWorkFlowRules(module, activities, parentCriteria);
//	}
}

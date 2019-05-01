package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;

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

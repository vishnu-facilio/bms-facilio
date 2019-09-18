package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class ExecuteStateTransitionsCommand extends ExecuteAllWorkflowsCommand {
	private static final long serialVersionUID = 1L;

	public ExecuteStateTransitionsCommand(RuleType ruleTypes) {
		super(ruleTypes);
	}
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		context.put(FacilioConstants.ContextNames.STATE_TRANSITION_ONLY_CONDITIONED_CHECK, true);
		return super.executeCommand(context);
	}

	@Override
	protected Criteria getCriteria(List<? extends ModuleBaseWithCustomFields> records) {
		if (CollectionUtils.isEmpty(records)) {
			return null;
		}

		Criteria criteria = super.getCriteria(records);

		return criteria;
	}
}

package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.chain.FacilioContext;
import com.facilio.modules.FacilioModule;
import io.opentelemetry.extension.annotations.WithSpan;
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
	@WithSpan
	public boolean executeCommand(Context context) throws Exception {
		context.put(FacilioConstants.ContextNames.STATE_TRANSITION_ONLY_CONDITIONED_CHECK, true);
		UpdateEventListForStateFlowCommand.updateEventListForStateTransition(context);
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

	@Override
	protected List<WorkflowRuleContext> getWorkflowRules(FacilioModule module, List<EventType> activities, List<? extends ModuleBaseWithCustomFields> records, FacilioContext context) throws Exception {
		List<WorkflowRuleContext> workflowRules = super.getWorkflowRules(module, activities, records, context);

		Set<Long> stateFlowIds = new HashSet<>();
		Set<Long> fromStateIds = new HashSet<>();
		if (CollectionUtils.isNotEmpty(records) && CollectionUtils.isNotEmpty(workflowRules)) {
			for (ModuleBaseWithCustomFields record : records) {
				stateFlowIds.add(record.getStateFlowId());
				if (record.getModuleState() != null) {
					fromStateIds.add(record.getModuleState().getId());
				}
			}
			workflowRules = workflowRules.stream().filter(rule -> {
				if (rule instanceof StateflowTransitionContext) {
					StateflowTransitionContext stateflowTransitionContext = (StateflowTransitionContext) rule;
					if (stateflowTransitionContext.getTypeEnum() != AbstractStateTransitionRuleContext.TransitionType.CONDITIONED) {
						return false;
					}
					if (stateFlowIds.contains(stateflowTransitionContext.getStateFlowId()) && fromStateIds.contains(stateflowTransitionContext.getFromStateId())) {
						return true;
					}
				}
				return false;
			}).collect(Collectors.toList());
		}

		return workflowRules;
	}
}

package com.facilio.bmsconsole.commands;

import java.util.*;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import io.opentelemetry.extension.annotations.WithSpan;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;

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

		List<WorkflowRuleContext> workflowRules = new ArrayList<>();
		List<Map<String, Long>> stateIds = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(records)) {
			for (ModuleBaseWithCustomFields record : records) {
				long fromStateId = record.getModuleState() != null ? record.getModuleState().getId() : -1;
				long stateFlowId = record.getStateFlowId() > 0 ? record.getStateFlowId() : -1;
				Map<String, Long> idMap = new HashMap<>();
				idMap.put("fromStateId",fromStateId);
				idMap.put("stateFlowId",stateFlowId);
				stateIds.add(idMap);
			}
		}
		workflowRules = StateFlowRulesAPI.getStateTransitions(stateIds, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED);
		return workflowRules;
	}
}

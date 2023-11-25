package com.facilio.bmsconsole.commands;

import java.util.*;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class GetAvailableStateCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(GetAvailableStateCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ModuleBaseWithCustomFields moduleData = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleData != null) {
			WorkflowRuleContext.RuleType ruleType = (WorkflowRuleContext.RuleType) context.get(FacilioConstants.ContextNames.RULE_TYPE);
			if (ruleType == null) {
				ruleType = WorkflowRuleContext.RuleType.STATE_FLOW;
			}
			FacilioStatus currentState = getStatus(moduleData, ruleType);
			if (currentState != null) {	// For preopen
				currentState = TicketAPI.getStatus(currentState.getId());				
			}
			long stateFlowId = getStateFlowId(moduleData, ruleType);
			context.put("currentState", currentState);
			if (currentState != null) {
				long currentTime = System.currentTimeMillis();
				List<WorkflowRuleContext> availableState = StateFlowRulesAPI.getAvailableState(stateFlowId, currentState.getId(), moduleName,
						moduleData, (FacilioContext) context);
				StateFlowRulesAPI.removeUnwantedTranstions(availableState);
				setStateTransitionSequence(availableState);
				if (ruleType == WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW) {
					// if the status is in current state, list count should be 2
					if (currentState.isRequestedState() && CollectionUtils.isNotEmpty(availableState) && availableState.size() != 2) {
						availableState = new ArrayList<>();
					}
				}
				LOGGER.debug("### time taken inside getAvailableState: " + this.getClass().getSimpleName() + ": " + (System.currentTimeMillis() - currentTime));
				context.put("availableStates", availableState);
			}
		}
		return false;
	}

	private void setStateTransitionSequence(List<WorkflowRuleContext> availableStates) throws Exception{
		if (CollectionUtils.isEmpty(availableStates)){
			return;
		}

		List<Long> stateTransitionIds = availableStates.stream().map(WorkflowRuleContext::getId).collect(Collectors.toList());
		Criteria criteria = new Criteria();
		for (Long id : stateTransitionIds){
			criteria.addOrCondition(CriteriaAPI.getCondition("STATE_TRANSITION_ID","stateTransitionId",String.valueOf(id), NumberOperators.EQUALS));
		}

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getStateflowTransitionSequenceFields())
				.table(ModuleFactory.getStateFlowTransitionSequenceModule().getTableName())
				.andCriteria(criteria);

		List<StateFlowTransitionSequenceContext> transitionSequence = FieldUtil.getAsBeanListFromMapList(builder.get(),StateFlowTransitionSequenceContext.class);

		Map<Long, List<StateFlowTransitionSequenceContext>> transitionSequenceMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(transitionSequence)) {
			transitionSequenceMap = transitionSequence.stream().collect(
					Collectors.groupingBy(StateFlowTransitionSequenceContext::getStateTransitionId, HashMap::new, Collectors.toCollection(ArrayList::new)
					));
		}

		if (MapUtils.isNotEmpty(transitionSequenceMap)) {
			for (WorkflowRuleContext state : availableStates) {
				((StateflowTransitionContext) state).setStateFlowTransitionSequence(transitionSequenceMap.get(state.getId()));
			}
		}

	}

	private long getStateFlowId(ModuleBaseWithCustomFields moduleData, WorkflowRuleContext.RuleType ruleType) {
		switch (ruleType) {
			case STATE_FLOW:
				return moduleData.getStateFlowId();
			case APPROVAL_STATE_FLOW:
				return moduleData.getApprovalFlowId();
			default:
				throw new IllegalArgumentException("Invalid rule type");
		}
	}

	private FacilioStatus getStatus(ModuleBaseWithCustomFields moduleData, WorkflowRuleContext.RuleType ruleType) {
		switch (ruleType) {
			case STATE_FLOW:
				return moduleData.getModuleState();
			case APPROVAL_STATE_FLOW:
				return moduleData.getApprovalStatus();
			default:
				throw new IllegalArgumentException("Invalid rule type");
		}
	}
}

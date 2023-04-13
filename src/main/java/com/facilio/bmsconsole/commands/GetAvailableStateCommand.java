package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.AbstractStateTransitionRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;

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
				removeUnwantedTranstions(availableState);

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

	public static void removeUnwantedTranstions(List<WorkflowRuleContext> states) {
		if (CollectionUtils.isEmpty(states)) {
			return;
		}
		
		Iterator<WorkflowRuleContext> iterator = states.iterator();
		while (iterator.hasNext()) {
			AbstractStateTransitionRuleContext transition = (AbstractStateTransitionRuleContext) iterator.next();
			if (transition instanceof StateflowTransitionContext) {
				if (transition.getTypeEnum() != AbstractStateTransitionRuleContext.TransitionType.NORMAL) {
					iterator.remove();
					continue;
				}

				if (AccountUtil.getCurrentUser().getAppDomain().getAppDomainType() == AppDomainType.TENANT_PORTAL.getIndex()) {
					if (!transition.isShowInTenantPortal()) {
						iterator.remove();
						continue;
					}
				} else if (AccountUtil.getCurrentUser().getAppDomain().getAppDomainType() == AppDomainType.VENDOR_PORTAL.getIndex()) {
					if (!transition.isShowInVendorPortal()) {
						iterator.remove();
						continue;
					}
				} else if (AccountUtil.getCurrentUser().getAppDomain().getAppDomainType() == AppDomainType.SERVICE_PORTAL.getIndex()) {
					if (!transition.isShowInOccupantPortal()) {
						iterator.remove();
						continue;
					}
					else if (AccountUtil.getCurrentUser().getAppDomain().getAppDomainType() == AppDomainType.CLIENT_PORTAL.getIndex()) {
						if (!transition.isShowInClientPortal()) {
							iterator.remove();
							continue;
						}
					}
				}
			}
		}
	}

}

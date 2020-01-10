package com.facilio.bmsconsole.commands;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.dto.IAMUser.AppType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext.TransitionType;
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
			FacilioStatus currentState = moduleData.getModuleState();
			context.put("currentState", currentState);
			if (currentState != null) {
				long currentTime = System.currentTimeMillis();
				List<WorkflowRuleContext> availableState = StateFlowRulesAPI.getAvailableState(moduleData.getStateFlowId(), currentState.getId(), moduleName, moduleData, (FacilioContext) context);
				removeUnwantedTranstions(availableState);
//				System.out.println("################### time taken: " + (System.currentTimeMillis() - currentTime));
				LOGGER.debug("### time taken inside getAvailableState: " + this.getClass().getSimpleName() + ": " + (System.currentTimeMillis() - currentTime));
				context.put("availableStates", availableState);
			}
		}
		return false;
	}
	
	public static void removeUnwantedTranstions(List<WorkflowRuleContext> states) {
		if (CollectionUtils.isEmpty(states)) {
			return;
		}
		
		Iterator<WorkflowRuleContext> iterator = states.iterator();
		while (iterator.hasNext()) {
			StateflowTransitionContext transition = (StateflowTransitionContext) iterator.next();
			if (transition.getTypeEnum() != TransitionType.NORMAL) {
				iterator.remove();
				continue;
			}
			
			if(AccountUtil.getCurrentUser().getAppTypeEnum() == AppType.TENANT_PORTAL) {
				if (!transition.isShowInTenantPortal()) {
					iterator.remove();
					continue;
				}
			}
			else if (AccountUtil.getCurrentUser().getAppTypeEnum() == AppType.VENDOR_PORTAL) {
				if (!transition.isShowInVendorPortal()) {
					iterator.remove();
					continue;
				}
			}
		}
	}

}

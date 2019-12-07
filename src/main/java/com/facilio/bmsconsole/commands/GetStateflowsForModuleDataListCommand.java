package com.facilio.bmsconsole.commands;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.facilio.accounts.dto.IAMUser.AppType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext.TransitionType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class GetStateflowsForModuleDataListCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ModuleBaseWithCustomFields> records = (List<ModuleBaseWithCustomFields>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(records)) {
			Map<String, List<WorkflowRuleContext>> stateFlows = StateFlowRulesAPI.getAvailableStates(records);
			if (MapUtils.isNotEmpty(stateFlows)) {
				for (String key : stateFlows.keySet()) {
					List<WorkflowRuleContext> list = stateFlows.get(key);
					Iterator<WorkflowRuleContext> iterator = list.iterator();
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
		context.put("stateFlows", stateFlows);
		}
		return false;
	}

}

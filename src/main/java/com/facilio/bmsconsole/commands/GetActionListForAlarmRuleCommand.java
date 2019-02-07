package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class GetActionListForAlarmRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		
		List<ReadingRuleContext> rules = alarmRule.getAlarmTriggerRules();
		Map<Long,ActionContext> finalAction = new HashMap<>();
		if(rules != null && !rules.isEmpty()) {
			for (WorkflowRuleContext rule : rules) {
				List<ActionContext> actions = ActionAPI.getAllActionsFromWorkflowRule(AccountUtil.getCurrentOrg().getId(), rule.getId());
				for(ActionContext action :actions) {
					if(!finalAction.containsKey(action.getId())) {
						finalAction.put(action.getId(), action);
					}
				}
			}
		}
		alarmRule.setAddAlarmActions(new ArrayList<>(finalAction.values()));
		return false;
	}

}

package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;

public class AddActionForAlarmRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		
		for( ReadingRuleContext rule :alarmRule.getAlarmTriggerRules()) {
			
			if (rule.getActions() != null && !rule.getActions().isEmpty()) {
				List<ActionContext> actions = ActionAPI.addActions(rule.getActions(), rule);
				ActionAPI.addWorkflowRuleActionRel(rule.getId(), actions);
			}
		}
		
		ActionContext actionContext = new ActionContext();
		actionContext.setActionType(ActionType.CLEAR_ALARM);
		actionContext.setStatus(true);
		List<ActionContext>  clearActions = ActionAPI.addActions(Collections.singletonList(actionContext), null);
		
		if(alarmRule.getAlarmClearRule() != null) {
			
			ActionAPI.addWorkflowRuleActionRel(alarmRule.getAlarmClearRule().getId(), clearActions);
			
			alarmRule.getAlarmClearRule().setActions(clearActions);
		}
		
		if(alarmRule.getAlarmClearRuleDuplicate() != null) {
			
			ActionAPI.addWorkflowRuleActionRel(alarmRule.getAlarmClearRuleDuplicate().getId(), clearActions);
			
			alarmRule.getAlarmClearRuleDuplicate().setActions(clearActions);
		}
		
		
		return false;
	}

}

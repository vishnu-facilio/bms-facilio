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
			else if (rule.getRuleTypeEnum() == RuleType.ALARM_CLEAR_RULE) {
				ActionContext actionContext = new ActionContext();
				actionContext.setActionType(ActionType.CLEAR_ALARM);
				actionContext.setStatus(true);
				
				List<ActionContext> actions = ActionAPI.addActions(Collections.singletonList(actionContext), rule);
				
				ActionAPI.addWorkflowRuleActionRel(rule.getId(), actions);
				
				rule.setActions(actions);
			}
		}
		
		if(alarmRule.getAlarmClearRule() != null) {
			ActionContext actionContext = new ActionContext();
			actionContext.setActionType(ActionType.CLEAR_ALARM);
			actionContext.setStatus(true);
			
			List<ActionContext> actions = ActionAPI.addActions(Collections.singletonList(actionContext), alarmRule.getAlarmClearRule());
			
			ActionAPI.addWorkflowRuleActionRel(alarmRule.getAlarmClearRule().getId(), actions);
			
			alarmRule.getAlarmClearRule().setActions(actions);
		}
		
		
		return false;
	}

}

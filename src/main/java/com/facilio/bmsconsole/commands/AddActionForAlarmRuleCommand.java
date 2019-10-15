package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;

public class AddActionForAlarmRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		
		ReadingRuleContext alarmTriggerRule =  alarmRule.getAlarmTriggerRule();
		
		List<ActionContext> actions = new ArrayList<ActionContext>();
		if(alarmTriggerRule != null) {
			actions = ActionAPI.addActions(alarmTriggerRule.getActions(), alarmTriggerRule);
			ActionAPI.addWorkflowRuleActionRel(alarmTriggerRule.getId(), actions);
		}
		
		if(alarmRule.getAlarmRCARules() != null) {
			for( ReadingRuleContext rule :alarmRule.getAlarmRCARules()) {
				
				actions = ActionAPI.addActions(rule.getActions(), rule);
				ActionAPI.addWorkflowRuleActionRel(rule.getId(), actions);
			}
		}
		
		if(!alarmRule.isAutoClear()) {
			ActionContext actionContext = new ActionContext();
			actionContext.setActionType(ActionType.CLEAR_ALARM);
			actionContext.setStatus(true);
			List<ActionContext> clearActions = ActionAPI.addActions(Collections.singletonList(actionContext), null);
			
			ActionAPI.addWorkflowRuleActionRel(alarmRule.getAlarmClearRule().getId(), clearActions);
			
			alarmRule.getAlarmClearRule().setActions(clearActions);
			
			ActionAPI.addWorkflowRuleActionRel(alarmRule.getAlarmClearRuleDuplicate().getId(), clearActions);
			
			alarmRule.getAlarmClearRuleDuplicate().setActions(clearActions);
		}
		return false;
	}

}

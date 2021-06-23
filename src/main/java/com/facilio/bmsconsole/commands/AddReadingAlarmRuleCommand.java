package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingAlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;

public class AddReadingAlarmRuleCommand extends FacilioCommand {

	
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Map<String,Long> readingAlarmRuleNameVsIdMap = new HashMap<>();
		
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		
		RuleType ruleType = (RuleType) context.get(FacilioConstants.ContextNames.RULE_TYPE);
		
		List<ReadingAlarmRuleContext> readingAlarmRuleContexts = null;
		if(alarmRule != null) {
			readingAlarmRuleContexts = alarmRule.getReadingAlarmRuleContexts();
		}
		else {
			readingAlarmRuleContexts = (List<ReadingAlarmRuleContext>) context.get(FacilioConstants.ContextNames.READING_ALARM_RULES);
		}
		
		if(readingAlarmRuleContexts != null) {
			
			for(ReadingAlarmRuleContext readingAlarmRuleContext : readingAlarmRuleContexts) {
				
				if(readingAlarmRuleContext.getReadingRuleGroupId() < 0) {
					readingAlarmRuleContext.setReadingRuleGroupId(alarmRule.getPreRequsite().getRuleGroupId());
				}
				
				if(ruleType != null) {
					readingAlarmRuleContext.setRuleType(ruleType);
				}
				else {
					if(readingAlarmRuleContext.getActions() != null && readingAlarmRuleContext.getActions().stream().allMatch(act->act.getActionType()==22)){
						readingAlarmRuleContext.setRuleType(RuleType.REPORT_DOWNTIME_RULE);
					}
					else{
						readingAlarmRuleContext.setRuleType(RuleType.READING_ALARM_RULE);
					}
				}

				if(readingAlarmRuleContext.getParentRuleName() != null) {
					readingAlarmRuleContext.setParentRuleId(readingAlarmRuleNameVsIdMap.get(readingAlarmRuleContext.getParentRuleName()));
				}
				
				WorkflowRuleAPI.addWorkflowRule(readingAlarmRuleContext);
				
				readingAlarmRuleNameVsIdMap.put(readingAlarmRuleContext.getName(),readingAlarmRuleContext.getId());
				
				List<ActionContext> actions = ActionAPI.addActions(readingAlarmRuleContext.getActions(), readingAlarmRuleContext);
				ActionAPI.addWorkflowRuleActionRel(readingAlarmRuleContext.getId(), actions);
			}
		}
		
		return false;
	}

}

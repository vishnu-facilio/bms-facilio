package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.ReadingAlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;

public class UpdateAlarmRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		
		AlarmRuleContext oldRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.OLD_ALARM_RULE);
		
		ReadingRuleContext preRequsiteRule = alarmRule.getPreRequsite();
		
		preRequsiteRule.setRuleType(WorkflowRuleContext.RuleType.READING_RULE); //Setting to default rule type
		
		preRequsiteRule = ReadingRuleAPI.updateReadingRuleWithChildren(preRequsiteRule, null);
		
		ReadingRuleAPI.updateTriggerAndClearRule(alarmRule,oldRule,context);
		
		if(preRequsiteRule.getActivityTypeEnum() != null && preRequsiteRule.getActivityTypeEnum().equals(EventType.SCHEDULED_READING_RULE)) {
			FacilioTimer.deleteJob(preRequsiteRule.getId(), FacilioConstants.Job.SCHEDULED_READING_RULE_JOB_NAME);
		}
		
		List<ReadingAlarmRuleContext> readingAlarmRules = WorkflowRuleAPI.getReadingAlarmRulesFromReadingRuleGroupId(alarmRule.getPreRequsite().getRuleGroupId());
		
		if(readingAlarmRules != null) {
			deleteReadingAlarmRuleAndActions(readingAlarmRules);
		}
		
		return false;
	}

	
	private void deleteReadingAlarmRuleAndActions(List<ReadingAlarmRuleContext> readingAlarmRules) throws Exception {
		
		List<Long> rulesToDelete = new ArrayList<>();
		
		for(ReadingAlarmRuleContext readingAlarmRule : readingAlarmRules) {
			rulesToDelete.add(readingAlarmRule.getId());
		}
		WorkflowRuleAPI.deleteWorkFlowRules(rulesToDelete);
		ActionAPI.deleteAllActionsFromWorkflowRules(rulesToDelete);
	}
}

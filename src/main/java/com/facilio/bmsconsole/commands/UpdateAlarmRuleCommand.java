package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.ReadingAlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class UpdateAlarmRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		
		List<ReadingRuleContext> oldRules = ReadingRuleAPI.getReadingRulesList(alarmRule.getPreRequsite().getRuleGroupId());
		
		ReadingRuleContext preRequsiteRule = alarmRule.getPreRequsite();
		
		preRequsiteRule.setRuleType(null); //Type is not allowed to be changed
		
		preRequsiteRule = ReadingRuleAPI.updateReadingRuleWithChildren(preRequsiteRule);
		
		deleteActions(oldRules);
		
		deleteTriggerAndClearRuleOfGroup(oldRules);
		
		ReadingRuleAPI.addTriggerAndClearRule(alarmRule);
		
		if(preRequsiteRule.getEvent() != null && preRequsiteRule.getEvent().getActivityTypeEnum().equals(EventType.SCHEDULED_READING_RULE)) {
			FacilioTimer.deleteJob(preRequsiteRule.getId(), FacilioConstants.Job.SCHEDULED_READING_RULE_JOB_NAME);
		}
		
		List<ReadingAlarmRuleContext> readingAlarmRules = WorkflowRuleAPI.getReadingAlarmRulesFromReadingRuleGroupId(alarmRule.getPreRequsite().getRuleGroupId());
		
		if(readingAlarmRules != null) {
			deleteReadingAlarmRuleAndActions(readingAlarmRules);
		}
		
		return false;
	}

	private void deleteTriggerAndClearRuleOfGroup(List<ReadingRuleContext> oldRules) throws Exception {
		
		List<Long> rulesToDelete = new ArrayList<>();
		
		for(ReadingRuleContext triggerRule : oldRules) {
			if(triggerRule.getRuleGroupId() != triggerRule.getId()) {
				rulesToDelete.add(triggerRule.getId());
			}
		}
		WorkflowRuleAPI.deleteWorkFlowRules(rulesToDelete);
	}
	
	private void deleteReadingAlarmRuleAndActions(List<ReadingAlarmRuleContext> readingAlarmRules) throws Exception {
		
		List<Long> rulesToDelete = new ArrayList<>();
		
		for(ReadingAlarmRuleContext readingAlarmRule : readingAlarmRules) {
			rulesToDelete.add(readingAlarmRule.getId());
		}
		WorkflowRuleAPI.deleteWorkFlowRules(rulesToDelete);
		ActionAPI.deleteAllActionsFromWorkflowRules(rulesToDelete);
	}
	
	private void deleteActions(List<ReadingRuleContext> oldRules) throws Exception {
		
		List<Long> ruleIds = new ArrayList<>();
		if(oldRules != null && !oldRules.isEmpty()) {
			for (ReadingRuleContext rule : oldRules) {
				if(rule.getRuleGroupId() != rule.getId()) {
					ruleIds.add(rule.getId());
				}
			}
			ActionAPI.deleteAllActionsFromWorkflowRules(ruleIds);
		}
	}
}

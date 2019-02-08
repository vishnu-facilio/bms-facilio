package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;

public class UpdateAlarmRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		
		AlarmRuleContext oldAlarmRule = new AlarmRuleContext(ReadingRuleAPI.getReadingRules(alarmRule.getPreRequsite().getRuleGroupId()));

		
		ReadingRuleContext preRequsiteRule = alarmRule.getPreRequsite();
		WorkflowRuleAPI.updateWorkflowRule(preRequsiteRule);
		
		deleteTriggerAndClearRuleOfGroup(oldAlarmRule);
		
		ReadingRuleAPI.addTriggerAndClearRule(alarmRule);
		
		if(preRequsiteRule.getEvent().getActivityTypeEnum().equals(ActivityType.SCHEDULED_READING_RULE)) {
			FacilioTimer.deleteJob(preRequsiteRule.getId(), FacilioConstants.Job.SCHEDULED_READING_RULE_JOB_NAME);
		}
		
		return false;
	}

	public void deleteTriggerAndClearRuleOfGroup(AlarmRuleContext alarmRule) throws Exception {
		
		List<Long> rulesToDelete = new ArrayList<>();
		for(ReadingRuleContext triggerRule : alarmRule.getAlarmTriggerRules()) {
			rulesToDelete.add(triggerRule.getId());
		}
		if(alarmRule.getAlarmClearRule() != null) {
			rulesToDelete.add(alarmRule.getAlarmClearRule().getId());
		}
		WorkflowRuleAPI.deleteWorkFlowRules(rulesToDelete);
	}
}

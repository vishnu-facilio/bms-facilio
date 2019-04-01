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
		
		AlarmRuleContext oldRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.OLD_ALARM_RULE);
		
		ReadingRuleContext preRequsiteRule = alarmRule.getPreRequsite();
		
		preRequsiteRule.setRuleType(null); //Type is not allowed to be changed
		
		preRequsiteRule = ReadingRuleAPI.updateReadingRuleWithChildren(preRequsiteRule);
		
		updateTriggerAndClearRule(alarmRule,oldRule,context);
		
		if(preRequsiteRule.getEvent() != null && preRequsiteRule.getEvent().getActivityTypeEnum().equals(EventType.SCHEDULED_READING_RULE)) {
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
	
	
	private void updateTriggerAndClearRule(AlarmRuleContext alarmRule,AlarmRuleContext oldRule, Context context) throws Exception {
		
		ReadingRuleContext preRequsiteRule = alarmRule.getPreRequsite();
		
		Map<String,Long> ruleNameVsIdMap = oldRule.getNameVsIdMap();
		
		ReadingRuleContext alarmTriggerRule = alarmRule.getAlarmTriggerRule();
		if(alarmTriggerRule != null) {
			ReadingRuleAPI.fillDefaultPropsForAlarmRule(alarmTriggerRule,preRequsiteRule,WorkflowRuleContext.RuleType.ALARM_TRIGGER_RULE,preRequsiteRule.getId());
			alarmTriggerRule.setOnSuccess(true);
			alarmTriggerRule.setClearAlarm(alarmRule.isAutoClear());
			
			
			Chain chain = TransactionChainFactory.updateVersionedWorkflowRuleChain();
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, alarmTriggerRule);
			chain.execute(context);
			
			ruleNameVsIdMap.put(alarmTriggerRule.getName(), alarmTriggerRule.getId());
		}
		
		List<ReadingRuleContext> alarmRCARules = alarmRule.getAlarmRCARules();
		
		if(alarmRCARules != null) {
			
//			int executionOrder = 1;
			for(ReadingRuleContext alarmRCARule :alarmRCARules) {
				
				Long parentId = alarmRCARule.getParentRuleName() != null ? ruleNameVsIdMap.get(alarmRCARule.getParentRuleName()) : alarmTriggerRule.getId();
				if(alarmRCARule.getParentRuleName() == null) {
					alarmRCARule.setOnSuccess(true);
				}
				alarmRCARule.setClearAlarm(false);
				ReadingRuleAPI.fillDefaultPropsForAlarmRule(alarmRCARule,preRequsiteRule,WorkflowRuleContext.RuleType.ALARM_RCA_RULES,parentId);
				if(alarmRCARule.getId() > 0) {
					Chain chain = TransactionChainFactory.updateVersionedWorkflowRuleChain();
					context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, alarmRCARule);
					chain.execute(context);
				}
				else {
					WorkflowRuleAPI.addWorkflowRule(alarmRCARule);
				}
				
				ruleNameVsIdMap.put(alarmRCARule.getName(), alarmRCARule.getId());
			}
		}
		
		if(alarmRule.getDeletedAlarmRCARules() != null) {
			for(ReadingRuleContext deletedRcaRule : alarmRule.getDeletedAlarmRCARules()) {
				WorkflowRuleAPI.deleteWorkflowRule(deletedRcaRule.getId());
			}
		}
		
		if(!alarmRule.isAutoClear()) {
			ReadingRuleContext alarmClearRule = alarmRule.getAlarmClearRule();
			alarmClearRule.setThresholdType(ThresholdType.SIMPLE);
			ReadingRuleAPI.fillDefaultPropsForAlarmRule(alarmClearRule,preRequsiteRule,WorkflowRuleContext.RuleType.ALARM_CLEAR_RULE,alarmTriggerRule.getId());
			alarmClearRule.setOnSuccess(false);
			alarmClearRule.setClearAlarm(false);
			WorkflowRuleAPI.addWorkflowRule(alarmClearRule);
			ruleNameVsIdMap.put(alarmClearRule.getName(), alarmClearRule.getId());
			
			
			ReadingRuleContext alarmClearRuleDuplicate = alarmRule.getAlarmClearRuleDuplicate();
			alarmClearRuleDuplicate.setThresholdType(ThresholdType.SIMPLE);
			ReadingRuleAPI.fillDefaultPropsForAlarmRule(alarmClearRuleDuplicate,preRequsiteRule,WorkflowRuleContext.RuleType.ALARM_CLEAR_RULE,preRequsiteRule.getId());
			alarmClearRuleDuplicate.setOnSuccess(false);
			alarmClearRuleDuplicate.setClearAlarm(false);
			WorkflowRuleAPI.addWorkflowRule(alarmClearRuleDuplicate);
			ruleNameVsIdMap.put(alarmClearRuleDuplicate.getName(), alarmClearRuleDuplicate.getId());
		}
	}
}

package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext.ThresholdType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateAlarmRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		
		AlarmRuleContext oldRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.OLD_ALARM_RULE);
		
		ReadingRuleContext preRequsiteRule = alarmRule.getPreRequsite();
		
		preRequsiteRule.setRuleType(null); //Type is not allowed to be changed
		
		preRequsiteRule.setReportBreakdown(alarmRule.isReportBreakdown());
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
	
	private void updateParentRuleId(long oldRuleId,long newRuleId) throws SQLException {
		
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
			.table(ModuleFactory.getWorkflowRuleModule().getTableName())
			.fields(FieldFactory.getWorkflowRuleFields())
			.andCustomWhere("PARENT_RULE_ID = ?", oldRuleId);
		
		Map<String,Object> value = new HashMap<>();
		value.put("parentRuleId", newRuleId);
		update.update(value);
	}
	
	public void updateReportBreakDown(long ruleGroupId, boolean reportBreakdownValue) throws Exception {
		FacilioModule readingRuleModule = ModuleFactory.getReadingRuleModule();
		FacilioField ruleGroupIdField = FieldFactory.getField("ruleGroupId", "RULE_GROUP_ID", readingRuleModule,FieldType.LOOKUP);
		FacilioField reportBreakdownField = FieldFactory.getField("reportBreakdown", "REPORT_BREAKDOWN",readingRuleModule, FieldType.BOOLEAN);
		Condition ruleGroupIdCondition = CriteriaAPI.getCondition(ruleGroupIdField,Collections.singletonList(ruleGroupId), NumberOperators.EQUALS);
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder().table(readingRuleModule.getTableName())
				.fields(Collections.singletonList(reportBreakdownField)).andCondition(ruleGroupIdCondition);
		Map<String, Object> value = new HashMap<>();
		value.put("reportBreakdown", reportBreakdownValue);
		update.update(value);
	}
	
	private void updateTriggerAndClearRule(AlarmRuleContext alarmRule,AlarmRuleContext oldRule, Context context) throws Exception {
		
		ReadingRuleContext preRequsiteRule = alarmRule.getPreRequsite();
		
		Map<String,Long> ruleNameVsIdMap = oldRule.getNameVsIdMap();
		
		ReadingRuleContext alarmTriggerRule = alarmRule.getAlarmTriggerRule();
		if(alarmTriggerRule != null) {
			ReadingRuleAPI.fillDefaultPropsForAlarmRule(alarmTriggerRule,preRequsiteRule,WorkflowRuleContext.RuleType.ALARM_TRIGGER_RULE,preRequsiteRule.getId());
			alarmTriggerRule.setOnSuccess(true);
			alarmTriggerRule.setClearAlarm(alarmRule.isAutoClear());
			
			alarmTriggerRule.setReportBreakdown(alarmRule.isReportBreakdown());
			
			Chain chain = TransactionChainFactory.updateVersionedWorkflowRuleChain();
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, alarmTriggerRule);
			chain.execute(context);
			
			ruleNameVsIdMap.put(alarmTriggerRule.getName(), alarmTriggerRule.getId());
			
			updateParentRuleId(oldRule.getAlarmTriggerRule().getId(),alarmTriggerRule.getId());
		}
		
		if(alarmTriggerRule == null) {
			alarmTriggerRule = oldRule.getAlarmTriggerRule();			// setting it here since its used bellow
			updateReportBreakDown(alarmTriggerRule.getRuleGroupId(),alarmRule.isReportBreakdown());
		}
		
		List<ReadingRuleContext> alarmRCARules = alarmRule.getAlarmRCARules();
		
		if(alarmRCARules != null) {
			
			int executionOrder = getMaxExecutionOrder (oldRule.getAlarmRCARules());
			
			for(ReadingRuleContext alarmRCARule :alarmRCARules) {
				
				Long parentId = alarmRCARule.getParentRuleName() != null ? ruleNameVsIdMap.get(alarmRCARule.getParentRuleName()) : alarmTriggerRule.getId();
				if(alarmRCARule.getParentRuleName() == null) {
					alarmRCARule.setOnSuccess(true);
				}
				alarmRCARule.setClearAlarm(false);
				alarmRCARule.setReportBreakdown(alarmRule.isReportBreakdown());
				ReadingRuleAPI.fillDefaultPropsForAlarmRule(alarmRCARule,preRequsiteRule,WorkflowRuleContext.RuleType.ALARM_RCA_RULES,parentId);
				if(alarmRCARule.getId() > 0) {
					long oldId = alarmRCARule.getId();
					
					Chain chain = TransactionChainFactory.updateVersionedWorkflowRuleChain();
					context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, alarmRCARule);
					chain.execute(context);
					
					updateParentRuleId(oldId,alarmRCARule.getId());
				}
				else {
					alarmRCARule.setExecutionOrder(++executionOrder);
					WorkflowRuleAPI.addWorkflowRule(alarmRCARule);
				}
				
				ruleNameVsIdMap.put(alarmRCARule.getName(), alarmRCARule.getId());
			}
		}
		
		if(alarmRule.getDeletedAlarmRCARules() != null) {
			for(ReadingRuleContext deletedRcaRule : alarmRule.getDeletedAlarmRCARules()) {
				WorkflowRuleAPI.deleteWorkflowRule(deletedRcaRule.getId());
				updateParentRuleId(deletedRcaRule.getId(), alarmTriggerRule.getId());
			}
		}
		
		if(!alarmRule.isAutoClear()) {
			ReadingRuleContext alarmClearRule = alarmRule.getAlarmClearRule();
			alarmClearRule.setThresholdType(ThresholdType.SIMPLE);
			ReadingRuleAPI.fillDefaultPropsForAlarmRule(alarmClearRule,preRequsiteRule,WorkflowRuleContext.RuleType.ALARM_CLEAR_RULE,alarmTriggerRule.getId());
			alarmClearRule.setOnSuccess(false);
			alarmClearRule.setClearAlarm(false);
			alarmClearRule.setReportBreakdown(alarmRule.isReportBreakdown());
			WorkflowRuleAPI.addWorkflowRule(alarmClearRule);
			ruleNameVsIdMap.put(alarmClearRule.getName(), alarmClearRule.getId());
			
			
			ReadingRuleContext alarmClearRuleDuplicate = alarmRule.getAlarmClearRuleDuplicate();
			alarmClearRuleDuplicate.setThresholdType(ThresholdType.SIMPLE);
			ReadingRuleAPI.fillDefaultPropsForAlarmRule(alarmClearRuleDuplicate,preRequsiteRule,WorkflowRuleContext.RuleType.ALARM_CLEAR_RULE,preRequsiteRule.getId());
			alarmClearRuleDuplicate.setOnSuccess(false);
			alarmClearRuleDuplicate.setClearAlarm(false);
			alarmClearRuleDuplicate.setReportBreakdown(alarmRule.isReportBreakdown());
			WorkflowRuleAPI.addWorkflowRule(alarmClearRuleDuplicate);
			ruleNameVsIdMap.put(alarmClearRuleDuplicate.getName(), alarmClearRuleDuplicate.getId());
		}
	}

	private int getMaxExecutionOrder(List<ReadingRuleContext> alarmRCARules) {
		if(alarmRCARules != null) {
			return alarmRCARules.get(alarmRCARules.size()-1).getExecutionOrder();
		}
		return 0;
	}
}

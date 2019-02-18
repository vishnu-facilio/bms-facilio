package com.facilio.bmsconsole.workflow.rule;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;

public class AlarmRuleContext {
	
	private ReadingRuleContext preRequsite;
	List<ReadingRuleContext> alarmTriggerRules;
	ReadingRuleContext alarmClearRule;
	
	boolean isAutoClear;			// set when autoclear without any condition.
	
	public boolean getIsAutoClear() {
		return isAutoClear;
	}
	public void setIsAutoClear(boolean isAutoClear) {
		this.isAutoClear = isAutoClear;
	}
	public boolean isAutoClear() {
		return isAutoClear;
	}

	public void setAutoClear(boolean isAutoClear) {
		this.isAutoClear = isAutoClear;
	}
	
	boolean isClearAlarmOnPreRequsiteFail = true;
	
	public boolean isClearAlarmOnPreRequsiteFail() {
		return isClearAlarmOnPreRequsiteFail;
	}
	public void setClearAlarmOnPreRequsiteFail(boolean isClearAlarmOnPreRequsiteFail) {
		this.isClearAlarmOnPreRequsiteFail = isClearAlarmOnPreRequsiteFail;
	}
	
	public AlarmRuleContext() {
		
	}

	public AlarmRuleContext(List<ReadingRuleContext> rules) {
		for(ReadingRuleContext rule :rules) {
			
			if(rule.getRuleTypeEnum().equals(RuleType.READING_RULE)) {
				preRequsite = rule;
			}
			else if(rule.getRuleTypeEnum().equals(RuleType.ALARM_CLEAR_RULE)) {
				alarmClearRule = rule;
			}
			else if(rule.getRuleTypeEnum().equals(RuleType.ALARM_TRIGGER_RULE)) {
				addAlarmTriggerRule(rule);
				if(rule.getClearAlarm()) {
					setIsAutoClear(true);
				}
			}
		}
	}
	
	public List<ReadingRuleContext> getAlarmTriggerRules() {
		return alarmTriggerRules;
	}
	public void setAlarmTriggerRules(List<ReadingRuleContext> alarmTriggerRules) {
		this.alarmTriggerRules = alarmTriggerRules;
	}
	public void addAlarmTriggerRule(ReadingRuleContext alarmTriggerRule) {
		this.alarmTriggerRules = this.alarmTriggerRules == null ? new ArrayList<>() : this.alarmTriggerRules;
		this.alarmTriggerRules.add(alarmTriggerRule);
	}
	public ReadingRuleContext getAlarmClearRule() {
		return alarmClearRule;
	}
	public ReadingRuleContext getPreRequsite() {
		return preRequsite;
	}
	public void setPreRequsite(ReadingRuleContext preRequsite) {
		this.preRequsite = preRequsite;
	}
	public void setAlarmClearRule(ReadingRuleContext alarmClearRule) {
		this.alarmClearRule = alarmClearRule;
	}
	
	public List<ReadingRuleContext> getAllRuleList() {
		List<ReadingRuleContext> rules = new ArrayList<>();
		if(preRequsite != null) {
			rules.add(preRequsite);
			if(alarmTriggerRules != null) {
				rules.addAll(alarmTriggerRules);
				if(alarmClearRule != null) {
					rules.add(alarmClearRule);
				}
			}
		}
		return rules;
	}
	
	List<ActionContext> addAlarmActions;

	public List<ActionContext> getAddAlarmActions() {
		return addAlarmActions;
	}
	public void setAddAlarmActions(List<ActionContext> addAlarmActions) {
		this.addAlarmActions = addAlarmActions;
	}
}

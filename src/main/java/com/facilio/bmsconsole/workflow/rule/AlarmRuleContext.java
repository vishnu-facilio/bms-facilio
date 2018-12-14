package com.facilio.bmsconsole.workflow.rule;

import java.util.ArrayList;
import java.util.List;

public class AlarmRuleContext {
	
	private ReadingRuleContext preRequsite;
	List<ReadingRuleContext> alarmTriggerRules;
	ReadingRuleContext alarmClearRule;
	
	public List<ReadingRuleContext> getAlarmTriggerRules() {
		return alarmTriggerRules;
	}
	public void setAlarmTriggerRules(List<ReadingRuleContext> alarmTriggerRules) {
		this.alarmTriggerRules = alarmTriggerRules;
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
}

package com.facilio.bmsconsole.workflow.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;

public class AlarmRuleContext {
	
	ReadingAlarmRuleContext readingAlarmRuleContext;
	
	public ReadingAlarmRuleContext getReadingAlarmRuleContext() {
		return readingAlarmRuleContext;
	}
	public void setReadingAlarmRuleContext(ReadingAlarmRuleContext readingAlarmRuleContext) {
		this.readingAlarmRuleContext = readingAlarmRuleContext;
	}

	private ReadingRuleContext preRequsite;
	List<ReadingRuleContext> alarmTriggerRules;
	ReadingRuleContext alarmClearRule;
	
	ReadingRuleContext alarmClearRuleDuplicate;
	
	boolean isClearAlarmOnPreRequsiteFail = true;
	
	public boolean isClearAlarmOnPreRequsiteFail() {
		return isClearAlarmOnPreRequsiteFail;
	}
	public void setClearAlarmOnPreRequsiteFail(boolean isClearAlarmOnPreRequsiteFail) {
		this.isClearAlarmOnPreRequsiteFail = isClearAlarmOnPreRequsiteFail;
	}
	
	public Map<String, Long> getRuleNameVsIdMap() {
		return ruleNameVsIdMap;
	}
	public void setRuleNameVsIdMap(Map<String, Long> ruleNameVsIdMap) {
		this.ruleNameVsIdMap = ruleNameVsIdMap;
	}
	
	public void addRuleNameVsIdMap(String name,Long id) {
		this.ruleNameVsIdMap.put(name, id);
	}

	Map<String,Long> ruleNameVsIdMap = new HashMap<>();
	
	public AlarmRuleContext() {
		
	}

	public AlarmRuleContext(List<ReadingRuleContext> rules) {
		for(ReadingRuleContext rule :rules) {
			
			if(rule.getRuleTypeEnum().equals(RuleType.READING_RULE)) {
				preRequsite = rule;
			}
			else if(rule.getRuleTypeEnum().equals(RuleType.ALARM_CLEAR_RULE) && rule.isOnSuccess()) {
				alarmClearRule = rule;
			}
			else if(rule.getRuleTypeEnum().equals(RuleType.ALARM_CLEAR_RULE) && !rule.isOnSuccess()) {
				alarmClearRuleDuplicate = rule;
			}
			else if(rule.getRuleTypeEnum().equals(RuleType.ALARM_TRIGGER_RULE)) {
				addAlarmTriggerRule(rule);
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
	public void setAlarmClearRule(ReadingRuleContext alarmClearRule) throws Exception {		// do not use this method anywhere in server
		this.alarmClearRule = alarmClearRule;
		if(alarmClearRule != null) {
			this.alarmClearRuleDuplicate = (ReadingRuleContext) alarmClearRule.clone();
		}
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
	@JSON(serialize=false)
	public ReadingRuleContext getAlarmClearRuleDuplicate() {
		return alarmClearRuleDuplicate;
	}
	public void setAlarmClearRuleDuplicate(ReadingRuleContext alarmClearRuleDuplicate) {
		this.alarmClearRuleDuplicate = alarmClearRuleDuplicate;
	}
}

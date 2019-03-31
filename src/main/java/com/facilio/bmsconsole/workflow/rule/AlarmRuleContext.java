package com.facilio.bmsconsole.workflow.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;

public class AlarmRuleContext {
	
	private ReadingRuleContext preRequsite;
	List<ReadingRuleContext> alarmTriggerRuleVersionHistory;
	Map<Long,List<ReadingRuleContext>> alarmRCARulesVersionHistory;
	ReadingRuleContext alarmTriggerRule;
	List<ReadingRuleContext> alarmRCARules;
	List<ReadingRuleContext> deletedAlarmRCARules;

	ReadingRuleContext alarmClearRule;
	
	ReadingRuleContext alarmClearRuleDuplicate;
	
	boolean isClearAlarmOnPreRequsiteFail = true;
	
	public boolean isClearAlarmOnPreRequsiteFail() {
		return isClearAlarmOnPreRequsiteFail;
	}
	public void setClearAlarmOnPreRequsiteFail(boolean isClearAlarmOnPreRequsiteFail) {
		this.isClearAlarmOnPreRequsiteFail = isClearAlarmOnPreRequsiteFail;
	}

	public AlarmRuleContext() {
		
	}
	public List<ReadingRuleContext> getAlarmTriggerRuleVersionHistory() {
		return alarmTriggerRuleVersionHistory;
	}
	
	public List<ReadingRuleContext> getDeletedAlarmRCARules() {
		return deletedAlarmRCARules;
	}
	public void setDeletedAlarmRCARules(List<ReadingRuleContext> deletedAlarmRCARules) {
		this.deletedAlarmRCARules = deletedAlarmRCARules;
	}
	public void addAlarmTriggerRuleVersionHistory(ReadingRuleContext alarmTriggerRule) {
		this.alarmTriggerRuleVersionHistory = this.alarmTriggerRuleVersionHistory == null ? new ArrayList<>() : this.alarmTriggerRuleVersionHistory;
		this.alarmTriggerRuleVersionHistory.add(alarmTriggerRule);
	}
	public Map<Long, List<ReadingRuleContext>> getAlarmRCARulesVersionHistory() {
		return alarmRCARulesVersionHistory;
	}
	public void addAlarmRCARulesVersionHistory(Long parentRuleId, ReadingRuleContext alarmRCARule) {
		this.alarmRCARulesVersionHistory = this.alarmRCARulesVersionHistory == null ? new HashMap<>() : this.alarmRCARulesVersionHistory;
		List<ReadingRuleContext> list = alarmRCARulesVersionHistory.containsKey(parentRuleId) ? alarmRCARulesVersionHistory.get(parentRuleId) : new ArrayList<>();
		list.add(alarmRCARule);
		this.alarmRCARulesVersionHistory.put(parentRuleId, list);
	}
	

	public AlarmRuleContext(List<ReadingRuleContext> rules) {
		for(ReadingRuleContext rule :rules) {
			
			if(rule.getRuleTypeEnum().equals(RuleType.READING_RULE)) {
				preRequsite = rule;
			}
			else if(rule.getRuleTypeEnum().equals(RuleType.ALARM_CLEAR_RULE) && !rule.isOnSuccess()) {
				alarmClearRule = rule;
			}
			else if(rule.getRuleTypeEnum().equals(RuleType.ALARM_TRIGGER_RULE)) {
				if(rule.isActive()) {
					alarmTriggerRule = rule;
				}
				else {
					addAlarmTriggerRuleVersionHistory(rule);
				}
			}
			else if(rule.getRuleTypeEnum().equals(RuleType.ALARM_RCA_RULES)) {
				
				if(rule.isActive()) {
					addAlarmRCARules(rule);
				}
				else {
					addAlarmRCARulesVersionHistory(rule.getVersionGroupId(),rule);
				}
			}
		}
	}
	public AlarmRuleContext(List<ReadingRuleContext> rules,List<ReadingAlarmRuleContext> readingAlarmRuleContexts) {
		this(rules);
		this.readingAlarmRuleContexts = readingAlarmRuleContexts;
	}
	
	public ReadingRuleContext getAlarmTriggerRule() {
		return alarmTriggerRule;
	}
	public void setAlarmTriggerRule(ReadingRuleContext alarmTriggerRule) {
		this.alarmTriggerRule = alarmTriggerRule;
	}
	public List<ReadingRuleContext> getAlarmRCARules() {
		return alarmRCARules;
	}
	public void setAlarmRCARules(List<ReadingRuleContext> alarmRCARules) {
		this.alarmRCARules = alarmRCARules;
	}
	public void addAlarmRCARules(ReadingRuleContext alarmRCARules) {
		if(this.alarmRCARules == null) {
			this.alarmRCARules = new ArrayList<>();
		}
		this.alarmRCARules.add(alarmRCARules);
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
	
	@JSON(serialize=false)
	public ReadingRuleContext getAlarmClearRuleDuplicate() {
		return alarmClearRuleDuplicate;
	}
	public void setAlarmClearRuleDuplicate(ReadingRuleContext alarmClearRuleDuplicate) {
		this.alarmClearRuleDuplicate = alarmClearRuleDuplicate;
	}
	
	List<ReadingAlarmRuleContext> readingAlarmRuleContexts;
	
	public List<ReadingAlarmRuleContext> getReadingAlarmRuleContexts() {
		return readingAlarmRuleContexts;
	}
	public void setReadingAlarmRuleContexts(List<ReadingAlarmRuleContext> readingAlarmRuleContexts) {
		this.readingAlarmRuleContexts = readingAlarmRuleContexts;
	}
	
	boolean isAutoClear;

	public boolean isAutoClear() {
		return isAutoClear;
	}
	public void setAutoClear(boolean isAutoClear) {
		this.isAutoClear = isAutoClear;
	}
	public void setIsAutoClear(boolean isAutoClear) {
		this.isAutoClear = isAutoClear;
	}
	
	public Map<String,Long> getNameVsIdMap() {
		Map<String,Long> ruleNameVsIdMap = new HashMap<>();
		
		if(alarmTriggerRule != null) {
			ruleNameVsIdMap.put(alarmTriggerRule.getName(), alarmTriggerRule.getId());
		}
		if(alarmRCARules != null) {
			for(ReadingRuleContext alarmRCARule :alarmRCARules) {
				ruleNameVsIdMap.put(alarmRCARule.getName(), alarmRCARule.getId());
			}
		}
		return ruleNameVsIdMap;
	}
}

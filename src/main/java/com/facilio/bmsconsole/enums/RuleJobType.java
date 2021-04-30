package com.facilio.bmsconsole.enums;

import com.facilio.bmsconsole.context.ExecuteHistoricalRule;
import com.facilio.bmsconsole.context.ExecuteHistoryForReadingRule;
import com.facilio.bmsconsole.context.ExecuteHistoryForRuleRollUp;
import com.facilio.bmsconsole.context.ExecuteHistoryForSensorRule;
import com.facilio.bmsconsole.context.ExecuteHistoryForAssetRollUp;

import com.facilio.modules.FacilioIntEnum;

public enum RuleJobType implements FacilioIntEnum {
	
	READING_ALARM (new ExecuteHistoryForReadingRule(), 3),
	ML_ANOMALY_ALARM,
	RCA_ALARM,
	READING_RCA_ALARM,
	BMS_ALARM,
	VIOLATION_ALARM,
	AGENT_ALARM,
	CONTROLLER_ALARM,
	PRE_ALARM (new ExecuteHistoryForReadingRule(), 3),
	OPERATION_ALARM,
	RULE_ROLLUP_ALARM (new ExecuteHistoryForRuleRollUp(), 2),
	ASSET_ROLLUP_ALARM (new ExecuteHistoryForAssetRollUp(), 2),
	SENSOR_ALARM,
	SENSOR_ROLLUP_ALARM (new ExecuteHistoryForSensorRule(), 3, RuleJobType.SENSOR_ALARM),
	MULTIVARIATE_ANOMALY_ALARM,
	;
	public Integer getIndex() {
		return ordinal() + 1;
	}

	@Override
	public String getValue() {
		return name();
	}
	
	public static RuleJobType valueOf(int value) {
		if (value > 0 && value <= values().length) {
			return values()[value - 1];
		}
		return null;
	}
	
	RuleJobType(){	
	}
	
	ExecuteHistoricalRule historyRuleExecutionType;
	Integer hierarchyLevels = null;
	RuleJobType rollUpAlarmType = null;
	
	RuleJobType(ExecuteHistoricalRule historyRuleExecutionType){
		this.historyRuleExecutionType = historyRuleExecutionType;
	}
	
	RuleJobType(ExecuteHistoricalRule historyRuleExecutionType, int hierarchyLevels){
		this.historyRuleExecutionType = historyRuleExecutionType;
		this.hierarchyLevels = hierarchyLevels;
	}
	
	RuleJobType(ExecuteHistoricalRule historyRuleExecutionType, int hierarchyLevels, RuleJobType rollUpAlarmType){
		this.historyRuleExecutionType = historyRuleExecutionType;
		this.hierarchyLevels = hierarchyLevels;
		this.rollUpAlarmType = rollUpAlarmType;
	}
	
	public ExecuteHistoricalRule getHistoryRuleExecutionType() {
		return historyRuleExecutionType;
	}
	
	public RuleJobType getRollUpAlarmType() {
		return rollUpAlarmType;
	}

	public Integer getHierarchyLevels() {
		return hierarchyLevels;
	}
}

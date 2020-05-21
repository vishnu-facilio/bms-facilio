package com.facilio.bmsconsole.enums;

import com.facilio.bmsconsole.context.ExecuteHistoricalRuleInterface;
import com.facilio.bmsconsole.context.ExecuteHistoryForReadingRule;
import com.facilio.bmsconsole.context.ExecuteHistoryForRuleRollUp;
import com.facilio.bmsconsole.context.ExecuteHistoryForSensorRule;
import com.facilio.bmsconsole.context.ExecuteHistoryForAssetRollUp;

import com.facilio.modules.FacilioEnum;

public enum RuleJobType implements FacilioEnum {
	
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
	SENSOR_ALARM (new ExecuteHistoryForSensorRule(), 3),
	;
	public int getIndex() {
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
	
	ExecuteHistoricalRuleInterface historyRuleExecutionType;
	Integer hierarchyLevels = null;
	
	RuleJobType(ExecuteHistoricalRuleInterface historyRuleExecutionType){
		this.historyRuleExecutionType = historyRuleExecutionType;
	}
	
	RuleJobType(ExecuteHistoricalRuleInterface historyRuleExecutionType, int hierarchyLevels){
		this.historyRuleExecutionType = historyRuleExecutionType;
		this.hierarchyLevels = hierarchyLevels;
	}
	
	public ExecuteHistoricalRuleInterface getHistoryRuleExecutionType() {
		return historyRuleExecutionType;
	}
	
	public Integer getHierarchyLevels() {
		return hierarchyLevels;
	}
}

package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class MLAlarmOccurrenceContext extends ModuleBaseWithCustomFields {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long ttime = -1;
	public long getTtime() {
		return ttime;
	}
	public void setTtime(long ttime) {
		this.ttime = ttime;
	}
	
	private MLAlarmContext parentAlarm;
	public MLAlarmContext getParentAlarm() {
		return parentAlarm;
	}
	public void setParentAlarm(MLAlarmContext parentAlarm) {
		this.parentAlarm = parentAlarm;
	}
	
	private long ruleId = -1;
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}

}

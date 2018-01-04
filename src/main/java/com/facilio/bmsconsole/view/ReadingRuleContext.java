package com.facilio.bmsconsole.view;

import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext.RuleType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ReadingRuleContext extends WorkflowRuleContext {
	private long startValue = -1;
	public long getStartValue() {
		return startValue;
	}
	public void setStartValue(long startValue) {
		this.startValue = startValue;
	}
	
	private long interval = -1;
	public long getInterval() {
		return interval;
	}
	public void setInterval(long interval) {
		this.interval = interval;
	}
	
	private long lastValue = -1;
	public long getLastValue() {
		if(lastValue != -1) {
			return lastValue;
		}
		else if (startValue != -1) {
			return startValue - interval;
		}
		return lastValue;
	}
	public void setLastValue(long lastValue) {
		this.lastValue = lastValue;
	}
	
	@Override
	@JsonInclude(Include.ALWAYS)
	public int getRuleType() {
		return RuleType.READING_RULE.getIntVal();
	}
	
	@Override
	public RuleType getRuleTypeEnum() {
		return RuleType.READING_RULE;
	}
}

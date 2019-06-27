package com.facilio.bmsconsole.context;

public class ReadingAlarm extends BaseAlarmContext {
	private static final long serialVersionUID = 1L;
	
	private long ruleId;
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}

	private long subRuleId = -1;
	public long getSubRuleId() {
		return subRuleId;
	}
	public void setSubRuleId(long subRuleId) {
		this.subRuleId = subRuleId;
	}
	
	private long readingFieldId;
	public long getReadingFieldId() {
		return readingFieldId;
	}
	public void setReadingFieldId(long readingFieldId) {
		this.readingFieldId = readingFieldId;
	}
	
//	@Override
//	public Type getType() {
//		if (super.getType() != Type.READING_ALARM) {
//			setType(Type.READING_ALARM);
//		}
//		return super.getType();
//	}

}

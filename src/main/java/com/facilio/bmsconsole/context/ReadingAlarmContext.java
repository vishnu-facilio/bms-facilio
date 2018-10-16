package com.facilio.bmsconsole.context;

public class ReadingAlarmContext extends AlarmContext {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long readingFieldId = -1;
	public long getReadingFieldId() {
		return readingFieldId;
	}
	public void setReadingFieldId(long readingFieldId) {
		this.readingFieldId = readingFieldId;
	}
	
	private long readingDataId = -1;
	public long getReadingDataId() {
		return readingDataId;
	}
	public void setReadingDataId(long readingDataId) {
		this.readingDataId = readingDataId;
	}

	private long baselineId = -1;
	public long getBaselineId() {
		return baselineId;
	}
	public void setBaselineId(long baselineId) {
		this.baselineId = baselineId;
	}
	
	private long startTime = -1;
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	private long endTime = -1;
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	private String readingMessage;
	public String getReadingMessage() {
		return readingMessage;
	}
	public void setReadingMessage(String readingMessage) {
		this.readingMessage = readingMessage;
	}
	
	private long ruleId = -1;
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}
	String reportMeta;
	public String getReportMeta() {
		return reportMeta;
	}
	public void setReportMeta(String reportMeta) {
		this.reportMeta = reportMeta;
	}
}

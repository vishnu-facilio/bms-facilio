package com.facilio.bmsconsole.workflow.rule;

import java.io.Serializable;

public class ReadingRuleAlarmMeta implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long alarmId = -1;
	public long getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(long alarmId) {
		this.alarmId = alarmId;
	}
	
	private long ruleId = -1;
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}
	
	private long resourceId = -1;
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	
	private long readingFieldId = -1;
	public long getReadingFieldId() {
		return readingFieldId;
	}
	public void setReadingFieldId(long readingFieldId) {
		this.readingFieldId = readingFieldId;
	}

	private Boolean clear;
	public Boolean getClear() {
		return clear;
	}
	public void setClear(Boolean clear) {
		this.clear = clear;
	}
	public boolean isClear() {
		if (clear != null) {
			return clear.booleanValue();
		}
		return false;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return new StringBuilder("Alarm Meta [id:").append(id)
					.append(", alarmId: ").append(alarmId)
					.append(", ruleId: ").append(ruleId)
					.append(", resourceId: ").append(resourceId)
					.append(", readingFieldId: ").append(readingFieldId)
					.append(", clear: ").append(clear)
					.toString();
	}
}

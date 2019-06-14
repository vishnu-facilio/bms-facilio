package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.context.BreakContext.BreakType;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class BaseAlarmContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private String subject;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private String key;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	private ResourceContext resource;
	public ResourceContext getResource() {
		return resource;
	}
	public void setResource(ResourceContext resource) {
		this.resource = resource;
	}
	
	private AlarmSeverityContext severity;
	public AlarmSeverityContext getSeverity() {
		return severity;
	}
	public void setSeverity(AlarmSeverityContext severity) {
		this.severity = severity;
	}
	
	private Type type;
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	
//	private AlarmCategoryContext category;
//	public AlarmCategoryContext getCategory() {
//		return category;
//	}
//	public void setCategory(AlarmCategoryContext category) {
//		this.category = category;
//	}
	
	private Boolean acknowledged;
	public Boolean getAcknowledged() {
		return acknowledged;
	}
	public void setAcknowledged(Boolean acknowledged) {
		this.acknowledged = acknowledged;
	}
	
	private long acknowledgedBy = -1;
	public long getAcknowledgedBy() {
		return acknowledgedBy;
	}
	public void setAcknowledgedBy(long acknowledgedBy) {
		this.acknowledgedBy = acknowledgedBy;
	}
	
	private long acknowledgedTime = -1;
	public long getAcknowledgedTime() {
		return acknowledgedTime;
	}
	public void setAcknowledgedTime(long acknowledgedTime) {
		this.acknowledgedTime = acknowledgedTime;
	}
	
	private long lastOccurredTime = -1;
	public long getLastOccurredTime() {
		return lastOccurredTime;
	}
	public void setLastOccurredTime(long lastOccurredTime) {
		this.lastOccurredTime = lastOccurredTime;
	}
	
	private long lastClearedTime = -1;
	public long getLastClearedTime() {
		return lastClearedTime;
	}
	public void setLastClearedTime(long lastClearedTime) {
		this.lastClearedTime = lastClearedTime;
	}
	
	public static enum Type {
		READING_ALARM,
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static Type valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}

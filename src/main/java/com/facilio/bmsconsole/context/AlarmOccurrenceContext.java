package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class AlarmOccurrenceContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;
	
	private AlarmSeverityContext severity;
	public AlarmSeverityContext getSeverity() {
		return severity;
	}
	public void setSeverity(AlarmSeverityContext severity) {
		this.severity = severity;
	}
	
	private String condition;
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	private String source;
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	private BaseAlarmContext alarm;
	public BaseAlarmContext getAlarm() {
		return alarm;
	}
	public void setAlarm(BaseAlarmContext alarm) {
		this.alarm = alarm;
	}
	
	private AlarmSeverityContext previousSeverity;
	public AlarmSeverityContext getPreviousSeverity() {
		return previousSeverity;
	}
	public void setPreviousSeverity(AlarmSeverityContext previousSeverity) {
		this.previousSeverity = previousSeverity;
	}
	
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
	
	private long createdTime = -1;
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	private long clearedTime = -1;
	public long getClearedTime() {
		return clearedTime;
	}
	public void setClearedTime(long clearedTime) {
		this.clearedTime = clearedTime;
	}
	
	private long woId = -1;
	public long getWoId() {
		return woId;
	}
	public void setWoId(long woId) {
		this.woId = woId;
	}
	
	private String problem;
	public String getProblem() {
		return problem;
	}
	public void setProblem(String problem) {
		this.problem = problem;
	}
	
	private String possibleCauses;
	public String getPossibleCauses() {
		return possibleCauses;
	}
	public void setPossibleCauses(String possibleCauses) {
		this.possibleCauses = possibleCauses;
	}
	
	private String recommendation;
	public String getRecommendation() {
		return recommendation;
	}
	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}
	
	private long noOfEvents = -1;
	public long getNoOfEvents() {
		return noOfEvents;
	}
	public void setNoOfEvents(long noOfEvents) {
		this.noOfEvents = noOfEvents;
	}
	
	private Boolean autoClear;
	public Boolean getAutoClear() {
		if (autoClear == null) {
			return false;
		}
		return autoClear;
	}
	public void setAutoClear(Boolean autoClear) {
		this.autoClear = autoClear;
	}
	
	public void updateAlarm(BaseAlarmContext baseAlarm) {
		baseAlarm.setSeverity(severity);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (obj instanceof AlarmOccurrenceContext) {
			return ((AlarmOccurrenceContext) obj).getId() == getId();
		}
		return false;
	}
}

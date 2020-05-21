package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.enums.RuleJobType;

public class WorkflowRuleResourceLoggerContext {
	
	private long id = -1;
	private long orgId;
	private long parentRuleLoggerId;
	private long resourceId;
	private String messageKey;
	private ResourceContext resourceContext;
	private Status status;
	private long alarmCount;
	private long modifiedStartTime;
	private long modifiedEndTime;
	private long calculationStartTime;
	private long calculationEndTime;
	private RuleJobType ruleJobType;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getParentRuleLoggerId() {
		return parentRuleLoggerId;
	}
	public void setParentRuleLoggerId(long parentRuleLoggerId) {
		this.parentRuleLoggerId = parentRuleLoggerId;
	}
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	public ResourceContext getResourceContext() {
		return resourceContext;
	}
	public void setResourceContext(ResourceContext resourceContext) {
		this.resourceContext = resourceContext;
	}
	public String getMessageKey() {
		return messageKey;
	}
	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}
	public int getStatus() {
		if (status != null) {
			return status.getIntVal();
		}
		return -1;
	}
	public Status getStatusAsEnum() {
		return status;
	}
	public void setStatus(int statusint) {
		this.status = Status.getAllOptions().get(statusint);
	}
	public void setStatusAsEnum(Status status) {
		this.status = status;
	}
	public long getAlarmCount() {
		return alarmCount;
	}
	public void setAlarmCount(long alarmCount) {
		this.alarmCount = alarmCount;
	}
	public long getModifiedStartTime() {
		return modifiedStartTime;
	}
	public void setModifiedStartTime(long modifiedStartTime) {
		this.modifiedStartTime = modifiedStartTime;
	}
	public long getModifiedEndTime() {
		return modifiedEndTime;
	}
	public void setModifiedEndTime(long modifiedEndTime) {
		this.modifiedEndTime = modifiedEndTime;
	}
	public long getCalculationStartTime() {
		return calculationStartTime;
	}
	public void setCalculationStartTime(long calculationStartTime) {
		this.calculationStartTime = calculationStartTime;
	}
	public long getCalculationEndTime() {
		return calculationEndTime;
	}
	public void setCalculationEndTime(long calculationEndTime) {
		this.calculationEndTime = calculationEndTime;
	}
	public RuleJobType getRuleJobTypeEnum() {
		return ruleJobType;
	}
	public int getRuleJobType() {
		if (ruleJobType == null) {
			return -1;
		}
		return ruleJobType.getIndex();
	}
	public void setRuleJobType(RuleJobType ruleJobType) {
		this.ruleJobType = ruleJobType;
	}
	public void setRuleJobType(int ruleJobType) {
		this.ruleJobType = RuleJobType.valueOf(ruleJobType);
	}
	
	public enum Status {
		
		IN_PROGRESS(1),
		EVENT_GENERATING_STATE(2),
		ALARM_PROCESSING_STATE(3),
		RESOLVED(4),
		FAILED(5),
		PARTIALLY_PROCESSED_STATE(6),
		PARTIALLY_COMPLETED_STATE(7),
		RESCHEDULED(8),
		;

		int intVal;
		private Status(int intVal) {
			this.intVal = intVal;
		}
		
		public int getIntVal() {
			return intVal;
		}

		private static final Map<Integer, Status> optionMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, Status> initTypeMap() {
			Map<Integer, Status> typeMap = new HashMap<>();

			for (Status status : values()) {
				typeMap.put(status.getIntVal(), status);
			}
			return typeMap;
		}

		public static Map<Integer, Status> getAllOptions() {
			return optionMap;
		}
	}

}

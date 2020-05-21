package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.enums.RuleJobType;

public class WorkflowRuleLoggerContext {
	
	private long id = -1;
	private long orgId;
	private long ruleId;
	private long noOfResources;
	private long resolvedResourcesCount;
	private Status status;
	private long totalAlarmCount;
	private long startTime;
	private long endTime;
	private long createdTime;
	private long createdBy;
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
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}
	public long getNoOfResources() {
		return noOfResources;
	}
	public void setNoOfResources(long noOfResources) {
		this.noOfResources = noOfResources;
	}
	public long getResolvedResourcesCount() {
		return resolvedResourcesCount;
	}
	public void setResolvedResourcesCount(long resolvedResourcesCount) {
		this.resolvedResourcesCount = resolvedResourcesCount;
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
	public long getTotalAlarmCount() {
		return totalAlarmCount;
	}
	public void setTotalAlarmCount(long totalAlarmCount) {
		this.totalAlarmCount = totalAlarmCount;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	public long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
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
		RESOLVED(2),
		FAILED(3),
		PARTIALLY_COMPLETED(4),
		RESCHEDULED(5),
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

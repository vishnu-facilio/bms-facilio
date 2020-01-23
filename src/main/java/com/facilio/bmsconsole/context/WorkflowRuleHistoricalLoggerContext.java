package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.context.BaseAlarmContext.Type;

public class WorkflowRuleHistoricalLoggerContext {

	private long id = -1;
	private long orgId;
	private Type type;
	private long ruleId;
	private Long resourceId;
	private ResourceContext resourceContext;
	private Status status;
	private long alarmCount;
	private long ruleResourceLoggerId;
	private long loggerGroupId;
	private long resourceLogCount;
	private long resolvedLogCount;
	private int totalChildAlarmCount;
	private long startTime;
	private long endTime;
	private long calculationStartTime;
	private long calculationEndTime;
	private long createdTime;
	private long createdBy;
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

	public Long getResourceId() {
		return resourceId;
	}
	
	public ResourceContext getResourceContext() {
		return resourceContext;
	}

	public void setResourceContext(ResourceContext resourceContext) {
		this.resourceContext = resourceContext;
	}
	
	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
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

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	public long getRuleResourceLoggerId() {
		return ruleResourceLoggerId;
	}

	public void setRuleResourceLoggerId(long ruleResourceLoggerId) {
		this.ruleResourceLoggerId = ruleResourceLoggerId;
	}

	public long getLoggerGroupId() {
		return loggerGroupId;
	}

	public void setLoggerGroupId(long loggerGroupId) {
		this.loggerGroupId = loggerGroupId;
	}
	
	public long getResourceLogCount() {
		return resourceLogCount;
	}

	public void setResourceLogCount(long resourceLogCount) {
		this.resourceLogCount = resourceLogCount;
	}
	
	public long getResolvedLogCount() {
		return resolvedLogCount;
	}
	public void setResolvedLogCount(long resolvedLogCount) {
		this.resolvedLogCount = resolvedLogCount;
	}
	
	public int getTotalChildAlarmCount() {
		return totalChildAlarmCount;
	}

	public void setTotalChildAlarmCount(int totalChildAlarmCount) {
		this.totalChildAlarmCount = totalChildAlarmCount;
	}
	
	public long getAlarmCount() {
		return alarmCount;
	}

	public void setAlarmCount(long alarmCount) {
		this.alarmCount = alarmCount;
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
	
	public int getType() {
		if (type != null) {
			return type.getIntVal();
		}
		return -1;
	}

	public void setType(int typeint) {
		this.type = Type.getAllOptions().get(new Integer(typeint));
	}
	
	public enum Type {
		
		READING_RULE(1),
		;

		int intVal;
		private Type(int intVal) {
			this.intVal = intVal;
		}
		
		public int getIntVal() {
			return intVal;
		}

		private static final Map<Integer, Type> optionMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, Type> initTypeMap() {
			Map<Integer, Type> typeMap = new HashMap<>();

			for (Type type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, Type> getAllOptions() {
			return optionMap;
		}
	}
	
	public enum Status {
		
		IN_PROGRESS(1),
		RESOLVED(2),
		FAILED(3),
		EVENT_GENERATING_STATE(4),
		ALARM_PROCESSING_STATE(5)
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

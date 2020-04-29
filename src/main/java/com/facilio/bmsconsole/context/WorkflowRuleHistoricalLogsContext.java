package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class WorkflowRuleHistoricalLogsContext {
	
	private long id = -1;
	private long orgId;
	private long parentRuleResourceId;
	private long splitStartTime;
	private long splitEndTime;
	private Status status;	
	private LogState logState;
	private long calculationStartTime;
	private long calculationEndTime;
	private String errorMessage;
	
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

	public long getParentRuleResourceId() {
		return parentRuleResourceId;
	}

	public void setParentRuleResourceId(long parentRuleResourceId) {
		this.parentRuleResourceId = parentRuleResourceId;
	}

	public long getSplitStartTime() {
		return splitStartTime;
	}

	public void setSplitStartTime(long splitStartTime) {
		this.splitStartTime = splitStartTime;
	}

	public long getSplitEndTime() {
		return splitEndTime;
	}

	public void setSplitEndTime(long splitEndTime) {
		this.splitEndTime = splitEndTime;
	}

	public int getLogState() {
		if (logState != null) {
			return logState.getIntVal();
		}
		return -1;
	}

	public LogState getLogStateAsEnum() {
		return logState;
	}

	public void setLogStateAsEnum(LogState logState) {
		this.logState = logState;
	}

	public void setLogState(int logStateint) {
		this.logState = LogState.getAllOptions().get(logStateint);
	}
	
	public Status getStatusAsEnum() {
		return status;
	}
	public int getStatus() {
		if (status != null) {
			return status.getIntVal();
		}
		return -1;
	}
	public void setStatus(int statusint) {
		this.status = Status.getAllOptions().get(statusint);
	}
	public void setStatusAsEnum(Status status) {
		this.status = status;
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
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public enum Status {
		
		IN_PROGRESS(1),
		RESOLVED(2),
		FAILED(3),
		YET_TO_BE_SCHEDULED(4),
		SKIPPED(5),
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
	public enum LogState {
		
		IS_FIRST_JOB(1),
		IS_LAST_JOB(2),
		FIRST_AS_WELL_AS_LAST(3),
		;

		int intVal;
		private LogState(int intVal) {
			this.intVal = intVal;
		}
		
		public int getIntVal() {
			return intVal;
		}

		private static final Map<Integer, LogState> optionMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, LogState> initTypeMap() {
			Map<Integer, LogState> typeMap = new HashMap<>();

			for (LogState logState : values()) {
				typeMap.put(logState.getIntVal(), logState);
			}
			return typeMap;
		}

		public static Map<Integer, LogState> getAllOptions() {
			return optionMap;
		}
	}

}

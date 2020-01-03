package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class LoggerContext extends ModuleBaseWithCustomFields{
	private long parentId;
	private Long resourceId;
	private Long dependentId;
	private Long loggerGroupId;
	private Status status;
	private ResourceContext resourceContext;
	private long actionCount;
	private long resourceLogCount;
	private long resolvedLogCount;
	private int totalChildActionCount;
	private long startTime;
	private long endTime;
	private long calculationStartTime;
	private long calculationEndTime;
	private long createdBy;
	private long createdTime;
	
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public Long getDependentId() {
		return dependentId;
	}
	public void setDependentId(Long dependentId) {
		this.dependentId = dependentId;
	}
	public Long getLoggerGroupId() {
		return loggerGroupId;
	}
	public void setLoggerGroupId(Long loggerGroupId) {
		this.loggerGroupId = loggerGroupId;
	}
	public Map<String, Object> getLogInfo() {
		return super.getData();
	}
	public void setLogInfo(Map<String, Object> logInfoMap) {
		super.setData(logInfoMap);
	}
	public void setLogInfo(String key, Object value) {
		super.setDatum(key, value);
	}
	public Object getLogInfo(String key) {
		return super.getDatum(key);
	}
	public Long getResourceId() {
		return resourceId;
	}
	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}
	public ResourceContext getResourceContext() {
		return resourceContext;
	}
	public void setResourceContext(ResourceContext resourceContext) {
		this.resourceContext = resourceContext;
	}
	public long getActionCount() {
		return actionCount;
	}
	public void setActionCount(long actionCount) {
		this.actionCount = actionCount;
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
	public int getTotalChildActionCount() {
		return totalChildActionCount;
	}
	public void setTotalChildActionCount(int totalChildActionCount) {
		this.totalChildActionCount = totalChildActionCount;
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

	public int getStatus() {
		if (status != null) {
			return status.getIntVal();
		}
		return -1;
	}

	public void setStatus(int statusint) {
		this.status = Status.getAllOptions().get(new Integer(statusint));
	}
	
	public enum Status {
		
		IN_PROGRESS(1),
		RESOLVED(2),
		FAILED(3),
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

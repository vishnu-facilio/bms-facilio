package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HistoricalLoggerContext {
	
	private long id = -1;
	private long orgId;
	private ResourceContext parentResourceContext;
	private long parentId;
	private Type type;
	private Long dependentId;
	private Long loggerGroupId;
	private Status status;
	private long startTime;
	private long endTime;
	private long calculationStartTime;
	private long calculationEndTime;
	private long createdBy;
	private long createdTime;
	
	public ResourceContext getParentResourceContext() {
		return parentResourceContext;
	}
	public void setParentResourceContext(ResourceContext parentResourceContext) {
		this.parentResourceContext = parentResourceContext;
	}
	
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
	public Long getloggerGroupId() {
		return loggerGroupId;
	}
	public void setloggerGroupId(Long loggerGroupId) {
		this.loggerGroupId = loggerGroupId;
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
	public int getType() {
		if (type != null) {
			return type.getIntVal();
		}
		return -1;
	}

	public void setType(int typeint) {
		this.type = Type.getAllOptions().get(new Integer(typeint));
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
	
	public enum Type {
			
			VM_CALCULATION(1),
			ENPI(2),
			FORMULA_FIELD(3),
			ENERGY_STAR_SYNC(4),
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

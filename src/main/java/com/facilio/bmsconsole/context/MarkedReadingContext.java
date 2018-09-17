package com.facilio.bmsconsole.context;

public class MarkedReadingContext  {

	private long ttime = -1;
	public long getTtime() {
		return ttime;
	}
	public void setTtime(long ttime) {
		this.ttime = ttime;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long moduleId = -1;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long fieldId = -1;
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	
	private long reourceId = -1;
	public long getResourceId() {
		return reourceId;
	}
	public void setResourceId(long resourceId) {
		this.reourceId = resourceId;
	}
	
	private long dataId = -1;
	public long getDataId() {
		return dataId;
	}
	public void setDataId(long dataId) {
		this.dataId = dataId;
	}
	
	private MarkType markType;
	public int getMarkType() {
		if (markType != null) {
			return markType.getValue();
		}
		return -1;
	}
	public void setMarkType(int markType) {
		this.markType = MarkType.valueOf(markType);
	}
	public MarkType getMarkTypeEnum() {
		return markType;
	}
	public void setMarkType(MarkType markType) {
		this.markType = markType;
	}
	
	
	private ReadingContext reading;
	public ReadingContext getReading() {
		return this.reading;
	}
	public void setReading(ReadingContext reading) {
		this.reading =reading;
		if(reading==null) {
			return;
		}
		 setResourceId( reading.getParentId());
		 setTtime( reading.getTtime());
	}
	
	private String actualValue;
	public String getActualValue() {
		return this.actualValue;
	}
	public void setActualValue(String actualValue) {
		this.actualValue = actualValue;
	}
	
	private String modifiedValue;
	public String getModifiedValue() {
		return this.modifiedValue;
	}
	public void setModifiedValue(String modifiedValue) {
		this.modifiedValue = modifiedValue;
	}
	
	public enum MarkType {
		NEGATIVE_VALUE,
		ZERO_VALUE,
		DECREMENTAL_VALUE,
		HIGH_VALUE,
		TOO_HIGH_VALUE,
		HIGH_VALUE_HOURLY_VIOLATION,
		HIGH_VALUE_DAILY_VIOLATION,
		HIGH_VALUE_WEEKLY_VIOLATION,
		RESET_VALUE;
		
		public int getValue() {
			return ordinal()+1;
		}
		
		public static MarkType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	public String toString() {
		
		return "Marked Reading resource: "+getResourceId()+" : "+getActualValue()+ " : "+getModifiedValue()+" : "+getMarkType()+" : "+getFieldId();
	}
} 

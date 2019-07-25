package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class ResetCounterMetaContext extends ModuleBaseWithCustomFields{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	private long resourceId = -1;
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	
	private long fieldId = -1;
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	
	private long ttime = -1;
	public long getTtime() {
		return ttime;
	}
	public void setTtime(long ttime) {
		this.ttime = ttime;
	}
	
	private long readingDataId = -1;
	public long getReadingDataId() {
		return readingDataId;
	}
	public void setReadingDataId(long readingDataId) {
		this.readingDataId = readingDataId;
	}

	String startvalue;

	public String getStartvalue() {
		return startvalue;
	}

	public void setStartvalue(String startvalue) {
		this.startvalue = startvalue;
	}

	String endvalue;

	public String getEndvalue() {
		return endvalue;
	}

	public void setEndvalue(String endvalue) {
		this.endvalue = endvalue;
	}

}

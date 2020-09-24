package com.facilio.bmsconsole.context;

import com.facilio.modules.fields.FacilioField;

import java.io.Serializable;

public class ResetCounterMetaContext implements Serializable {
	/**
	 * 
	 */
    private Object value;
    
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
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
    
	FacilioField field;

	public FacilioField getField() {
		return field;
	}

	public void setField(FacilioField field) {
		this.field = field;
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

	long startvalue = -1;

	public long getStartvalue() {
		return startvalue;
	}

	public void setStartvalue(long startvalue) {
		this.startvalue = startvalue;
	}
	
	long endvalue = -1;

	public long getEndvalue() {
		return endvalue;
	}

	public void setEndvalue(long endvalue) {
		this.endvalue = endvalue;
	}
	
	ReadingContext reading;

	public ReadingContext getReading() {
		return reading;
	}

	public void setReading(ReadingContext reading) {
		this.reading = reading;
	}

}

package com.facilio.bmsconsole.modules;

public class UpdateChangeSet {
	
	private long recordId = -1;
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}

	private long fieldId = -1;
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	
	private Object oldValue;
	public Object getOldValue() {
		return oldValue;
	}
	public void setOldValue(Object oldValue) {
		this.oldValue = oldValue;
	}
	
	private Object newValue;
	public Object getNewValue() {
		return newValue;
	}
	public void setNewValue(Object newValue) {
		this.newValue = newValue;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder()
									.append("Field ID : ")
									.append(fieldId)
									.append("::Old Value : ")
									.append(oldValue)
									.append("::New Value : ")
									.append(newValue)
									;
		return builder.toString();
	}
}

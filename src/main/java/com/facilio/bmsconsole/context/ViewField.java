package com.facilio.bmsconsole.context;

import com.facilio.modules.fields.FacilioField;

public class ViewField{
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
	
	private long parentFieldId = -1;
	public long getParentFieldId() {
		return parentFieldId;
	}
	public void setParentFieldId(long parentFieldId) {
		this.parentFieldId = parentFieldId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private long viewId = -1;
	public long getViewId() {
		return viewId;
	}
	public void setViewId(long viewId) {
		this.viewId = viewId;
	}
	
	private String columnDisplayName;
	public String getColumnDisplayName() {
		return columnDisplayName;
	}
	public void setColumnDisplayName(String columnDisplayName) {
		this.columnDisplayName = columnDisplayName;
	}
	
	private FacilioField field;
	public FacilioField getField() {
		return field;
	}
	public void setField(FacilioField field) {
		this.field = field;
	}
	
	private FacilioField parentField;
	public FacilioField getParentField() {
		return parentField;
	}
	public void setParentField(FacilioField parentField) {
		this.parentField = parentField;
	}
	
	public ViewField() { }
	
	public ViewField(String name, String displayName) {
		this.name = name;
		this.columnDisplayName = displayName;
	}
	
}

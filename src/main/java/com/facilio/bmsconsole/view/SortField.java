package com.facilio.bmsconsole.view;

import com.facilio.modules.fields.FacilioField;

public class SortField {
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
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
	
	private String fieldName;
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	private long viewId = -1;
	public long getViewId() {
		return viewId;
	}
	public void setViewId(long viewId) {
		this.viewId = viewId;
	}
	
	private FacilioField sortField;
	private Boolean isAscending;
	
	public SortField() {}
	 
	public SortField(FacilioField sortField, boolean isAscending) {
		this.sortField = sortField;
		this.setIsAscending(isAscending);
	}

	public SortField(String fieldName, long fieldId, boolean isAscending) {
		this.isAscending = isAscending;
		this.fieldName = fieldName;
		this.fieldId = fieldId;
	}

	public FacilioField getSortField() {
		return sortField;
	}

	public void setSortField(FacilioField sortField) {
		this.sortField = sortField;
	}
	public Boolean getIsAscending() {
		return isAscending;
	}
	public void setIsAscending(Boolean isAscending) {
		this.isAscending = isAscending;
	}
	
	private long sortableFieldId = -1;
	public long getSortableFieldId() {
		return sortableFieldId;
	}
	public void setSortableFieldId(long sortableFieldId) {
		this.sortableFieldId = sortableFieldId;
	}
}

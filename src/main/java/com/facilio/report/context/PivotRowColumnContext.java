package com.facilio.report.context;

public class PivotRowColumnContext extends PivotColumnContext {

	private long lookupFieldId = -1;

	public long getLookupFieldId() {
		return lookupFieldId;
	}

	public void setLookupFieldId(long lookupFieldId) {
		this.lookupFieldId = lookupFieldId;
	}

	private long subModuleFieldId = -1;

	public long getSubModuleFieldId() {
		return subModuleFieldId;
	}

	public void setSubModuleFieldId(long subModuleFieldId) {
		this.subModuleFieldId = subModuleFieldId;
	}

	private int sortOrder;

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	private ReportPivotFieldContext sortField;

	public ReportPivotFieldContext getSortField() {
		return sortField;
	}

	public void setSortField(ReportPivotFieldContext sortField) {
		this.sortField = sortField;
	}

	public String getFieldDisplayName() {
		return fieldDisplayName;
	}

	public void setFieldDisplayName(String fieldDisplayName) {
		this.fieldDisplayName = fieldDisplayName;
	}

	private String fieldDisplayName;
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getField() + "";
	}
}

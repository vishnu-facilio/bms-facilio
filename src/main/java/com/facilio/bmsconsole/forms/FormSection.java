package com.facilio.bmsconsole.forms;

import java.util.ArrayList;
import java.util.List;

public class FormSection {
	private static final long serialVersionUID = 1L;
	
	public FormSection() {}
	
	public FormSection(long sequenceNumber, List<FormField> fields) {
		this.sequenceNumber = sequenceNumber;
		this.fields = fields;
	}
	
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long formId = -1;
	public long getFormId() {
		return formId;
	}
	public void setFormId(long formId) {
		this.formId = formId;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	private long sequenceNumber = -1;
	public long getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
	private Boolean showLabel;
	public Boolean getShowLabel() {
		return showLabel;
	}
	public void setShowLabel(Boolean showLabel) {
		this.showLabel = showLabel;
	}
	public void setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
	}
	public boolean isShowLabel() {
		if (showLabel == null) {
			return false;
		}
		return showLabel.booleanValue();
	}
	
	private List<FormField> fields;
	public List<FormField> getFields() {
		return fields;
	}
	public void setFields(List<FormField> fields) {
		this.fields = fields;
	}
	
	public void addField(FormField field) {
		if (this.fields == null) {
			this.fields = new ArrayList<>();
		}
		this.fields.add(field);
	}
}

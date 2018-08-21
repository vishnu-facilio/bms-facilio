package com.facilio.bmsconsole.forms;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioField.FieldDisplayType;

public class FormField {
	private static final long serialVersionUID = 4252438995947509456L;
	
	private long id = -1;
	
	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId;
	
	public long getOrgId() {
		return this.orgId;
	}
	
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long formId = -1;
	
	public long getFormId() {
		return this.formId;
	}
	
	public void setFormId(long formId) {
		this.formId = formId;
	}
	
	private Boolean required = false;
	
	public boolean getRequired() {
		return isRequired();
	}
	
	public boolean isRequired() {
		if(required != null) {
			return required.booleanValue();
		}
		return false;
	}
	
	public void setRequired(boolean required) {
		this.required = required;
	}
	
	private int sequenceNumber;
	
	public int getSequenceNumber() {
		return this.sequenceNumber;
	}
	
	public void setSequenceNumber(int num) {
		this.sequenceNumber = num;
	}
	
	public enum Required {
		REQUIRED,
		OPTIONAL
	}
	
	public FormField() {}
	
	public FormField(long fieldId, String name, FieldDisplayType displayType, String displayName, Required required, int sequenceNumber) {
		this(name, displayType, displayName, required, sequenceNumber);
		this.setFieldId(fieldId);
	}
	
	public FormField(String name, FieldDisplayType displayType, String displayName, Required required, int sequenceNumber) {
		this.name = name;
		this.displayType = displayType;
		this.displayName = displayName;
		this.required = required == Required.REQUIRED;
		this.sequenceNumber = sequenceNumber;
	}
	
	private String displayName;
	
	public void setDisplayName(String name) {
		this.displayName = name;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	private String name;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

	private FacilioField field;
	
	public FacilioField getField() {
		return field;
	}

	public void setField(FacilioField field) {
		this.field = field;
	}
	
	private long fieldId = -1;
	
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	
	public long getFieldId() {
		return this.fieldId;
	}
	
	private FieldDisplayType displayType;
	
	public FieldDisplayType getDisplayType() {
		return displayType;
	}
	
	public void setDisplayType(FieldDisplayType displayType) {
		this.displayType = displayType;
	}
	
	public void setDisplayType(int displayType) {
		this.displayType = FieldDisplayType.TYPE_MAP.get(displayType);
	}
	
	public void setDisplayTypeInt(int displayType) {
		this.displayType = FieldDisplayType.TYPE_MAP.get(displayType);
	}
	
	public int getDisplayTypeInt() {
		if (displayType != null) {
			return displayType.getIntValForDB();
		}
		return -1;
	}
}

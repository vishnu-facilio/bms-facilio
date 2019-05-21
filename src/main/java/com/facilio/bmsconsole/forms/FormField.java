package com.facilio.bmsconsole.forms;

import java.io.Serializable;

import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;

public class FormField implements Serializable {
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
	
	private String lookupModuleName;
	public void setLookupModuleName(String lookupModuleName) {
		this.lookupModuleName = lookupModuleName;
	}
	
	public String getLookupModuleName() {
		return this.lookupModuleName;
	}
	
	private int span = -1;
	public int getSpan() {
		return span;
	}
	public void setSpan(int span) {
		this.span = span;
	}
	
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

	public enum Required {
		REQUIRED,
		OPTIONAL
	}
	private FacilioField field;
	
	public FacilioField getField() {
		return field;
	}

	public void setField(FacilioField field) {
		this.field = field;
	}

	public FormField() {}
	
	public FormField(long fieldId, String name, FieldDisplayType displayType, String displayName, Required required, int sequenceNumber, int span) {
		this(name, displayType, displayName, required, sequenceNumber, span);
		this.setFieldId(fieldId);
	}
	
	public FormField(String name, FieldDisplayType displayType, String displayName, Required required, String lookupModuleName, int sequenceNumber, int span) {
		this(name, displayType, displayName, required, sequenceNumber, span);
		this.setLookupModuleName(lookupModuleName);
	}
	
	public FormField(String name, FieldDisplayType displayType, String displayName, Required required, int sequenceNumber, int span) {
		this.name = name;
		this.displayType = displayType;
		this.displayName = displayName;
		this.required = required == Required.REQUIRED;
		this.sequenceNumber = sequenceNumber;
		this.span = span;
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
	
	private long fieldId = -1;
	
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	
	public long getFieldId() {
		return this.fieldId;
	}
	private FieldDisplayType displayType;
	public int getDisplayType() {
		if(displayType != null) {
			return displayType.getIntValForDB();
		}
		return -1;
	}
	public void getDisplayType(int displayType) {
		this.displayType = FieldDisplayType.TYPE_MAP.get(displayType);
	}
	public void setDisplayType(int displayType) {
		this.displayType = FieldDisplayType.TYPE_MAP.get(displayType);
	}
	public void setDisplayType(FieldDisplayType displayType) {
		this.displayType = displayType;
	}
	public String getDisplayTypeVal() {
		if(displayType != null) {
			return displayType.getHtml5Type();
		}
		return null;
	}
	public FieldDisplayType getDisplayTypeEnum() {
		return displayType;
	}
	
	private Boolean allowCreate = false;
	
	public Boolean getAllowCreate() {
		return allowCreate;
	}

	public FormField setAllowCreate(Boolean allowCreate) {
		this.allowCreate = allowCreate;
		return this;
	}
	
	private String createFormName;

	public String getCreateFormName() {
		return createFormName;
	}

	public FormField setCreateFormName(String createFormName) {
		this.createFormName = createFormName;
		return this;
	}
	
	private long sectionId = -1;
	public long getSectionId() {
		return sectionId;
	}
	public void setSectionId(long sectionId) {
		this.sectionId = sectionId;
	}
}

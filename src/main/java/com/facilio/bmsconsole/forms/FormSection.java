package com.facilio.bmsconsole.forms;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.facilio.modules.FacilioEnum;
import com.facilio.util.FacilioUtil;

public class FormSection {
	private static final long serialVersionUID = 1L;
	
	public FormSection() {}
	
	public FormSection(String name, long sequenceNumber, List<FormField> fields, boolean showLabel) {
		this.name = name;
		this.sequenceNumber = sequenceNumber;
		this.fields = fields;
		this.showLabel = showLabel;
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

	private long subFormId = -1;
	public long getSubFormId() {
		return subFormId;
	}
	public void setSubFormId(long subFormId) {
		this.subFormId = subFormId;
	}

	private FacilioForm subForm;
	public FacilioForm getSubForm() {
		return subForm;
	}
	public void setSubForm(FacilioForm subForm) {
		this.subForm = subForm;
	}
	
	private JSONObject subFormValue;
	public JSONObject getSubFormValue() {
		return subFormValue;
	}
	public void setData(JSONObject subFormValue) {
		this.subFormValue = subFormValue;
	}
	
	public String getSubFormValueStr() {
		if (subFormValue != null) {
			return subFormValue.toJSONString();
		}
		return null;
	}
	public void setSubFormValueStr(String subFormValueStr) throws ParseException {
		if (subFormValueStr != null) {
			this.subFormValue = FacilioUtil.parseJson(subFormValueStr);
		}
	}

	private SectionType sectionType;
	public SectionType getSectionTypeEnum() {
		return sectionType;
	}
	public void setSectionType(SectionType sectionType) {
		this.sectionType = sectionType;
	}
	public void setSectionType(int sectionTypeInt) {
		this.sectionType = SectionType.valueOf(sectionTypeInt);
	}
	public int getSectionType() {
		if (this.sectionType != null) {
			return this.sectionType.getIndex();
		}
		return -1;
	}

	public enum SectionType implements FacilioEnum {
		FIELDS("Fields"),
		SUB_FORM("Sub Form"),
		;

		SectionType(String name) {
			this.name = name;
		}

		private String name;
		public String getName() {
			return name;
		}

		@Override
		public int getIndex() {
			return ordinal() + 1;
		}

		public static SectionType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}

		@Override
		public String getValue() {
			return getName();
		}
	}
}

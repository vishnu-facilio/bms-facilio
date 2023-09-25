package com.facilio.bmsconsole.forms;

import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.Criteria;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.util.FacilioUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.Serializable;

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

	@JsonInclude
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
	public void setValue(String value) {
		this.value = value;
	}
	public void setValueObject(Object value) {
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

	private JSONObject fieldData;
	
	public JSONObject getFieldData() {
		return fieldData;
	}

	public void setFieldData(JSONObject fieldData) throws Exception {
		this.fieldData = fieldData;
		if(fieldData != null)
		{
			setField(FieldUtil.parseFieldJson(this.fieldData));
		}
	}

	public FormField() {}
	
	public FormField(long fieldId, String name, FieldDisplayType displayType, String displayName, Required required, int sequenceNumber, int span) {
		this(name, displayType, displayName, required, sequenceNumber, span);
		this.setFieldId(fieldId);
	}
	
	public FormField(long fieldId, String name, FieldDisplayType displayType, String displayName, Required required, int sequenceNumber, int span,Boolean hideField) {
		this(name, displayType, displayName, required, sequenceNumber, span);
		this.setFieldId(fieldId);
		this.setHideField(hideField);
	}
	
	public FormField(String name, FieldDisplayType displayType, String displayName, Required required, String lookupModuleName, int sequenceNumber, int span) {
		this(name, displayType, displayName, required, sequenceNumber, span);
		this.setLookupModuleName(lookupModuleName);
	}
	
	public FormField(String name, FieldDisplayType displayType, String displayName, Required required, String lookupModuleName, int sequenceNumber, int span,Boolean isDisabled) {
		this(name, displayType, displayName, required, lookupModuleName, sequenceNumber, span);
		this.isDisabled = isDisabled;
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
		if (displayName == null && field != null) {
			return field.getDisplayName();
		}
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
	
	@JsonIgnore
	public boolean getShowField() {
		if (getHideField() != null) {
			return !getHideField();
		}
		return true;
	}
	
	@JsonIgnore
	public void setShowField(boolean showField) {
		setHideField(!showField);
	}
	
	private Boolean hideField;
	public Boolean getHideField() {
		return hideField;
	}
	public void setHideField(Boolean hideField) {
		this.hideField = hideField;
	}
	@JSON(serialize = false)
	@JsonIgnore
	public boolean isFieldHidden() {
		if (hideField == null) {
			return false;
		}
		return hideField;
	}
	
	private Boolean isDisabled;

	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	private Boolean allowCreate = false;
	
	public Boolean getAllowCreate() {
		return allowCreate;
	}
	
	public void setAllowCreate(Boolean allowCreate) {
		this.allowCreate = allowCreate ;
	}
	
	public FormField setAllowCreateOptions(Boolean allowCreate) {
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

	private JSONObject config;
	public String getConfigStr() {
		if (config != null) {
			return config.toJSONString();
		}
		return null;
	}
	public JSONObject getConfig() {
		return config;
	}
	// temp
	public JSONObject getConfigJSON() {
		return getConfig();
	}
	@JSON(serialize = false)
	public void setConfigStr(String config) throws ParseException {
		if (StringUtils.isNotEmpty(config)) {
			this.config = FacilioUtil.parseJson(config);
		}
	}
	public void setConfig(JSONObject config) {
		this.config = config;
	}
	
	public void addToConfig(String key, Object value) {
		if (this.config == null) {
			this.config = new JSONObject();
		}
		this.config.put(key, value);
	}
	
	// For date field
	public void showTodayDate() {
		setValue(Criteria.CURRENT_DATE);
	}
	
	private JSONObject filters;
	@JSON(serialize = false)
	public String getFiltersStr() {
		if (filters != null) {
			return filters.toJSONString();
		}
		return null;
	}
	@JSON(serialize = false)
	public void setFiltersStr(String filters) throws ParseException {
		if (StringUtils.isNotEmpty(filters)) {
			this.filters = FacilioUtil.parseJson(filters);
		}
	}
	public JSONObject getFilters() {
		return filters;
	}
	public void setFilters(JSONObject filters) {
		this.filters = filters;
	}
	public void addToFilters(String key, Object value) {
		if (this.filters == null) {
			this.filters = new JSONObject();
		}
		this.filters.put(key, value);
	}
	
	
	private Boolean showFloorPlanConfig;
	@JsonIgnore
	public Boolean getShowFloorPlanConfig() {
		return showFloorPlanConfig;
	}
	@JsonIgnore
	public void setShowFloorPlanConfig(Boolean showFloorPlanConfig) {
		this.showFloorPlanConfig = showFloorPlanConfig;
	}
	
	private JSONObject validations;
	public JSONObject getValidations() {
		return validations;
	}
	public void setValidations(JSONObject validations) {
		this.validations = validations;
	}
	
	private void addValidation(String key, Object value) {
		if (validations == null) {
			validations = new JSONObject();
		}
		validations.put(key, value);
	}

	public void setMaxLength(int maxLength) {
		addValidation("maxLength", maxLength);
	}

	public void setEmailValidation(){
		addValidation("emailRegexPattern", FacilioConstants.FormContextNames.EMAIL_REGEX);
	}

	public double getFormFieldSequenceNumber() {
		return formFieldSequenceNumber;
	}
	public void setFormFieldSequenceNumber(double formFieldSequenceNumber) {
		this.formFieldSequenceNumber = formFieldSequenceNumber;
	}
	private double formFieldSequenceNumber;

	public long getSysModifiedTime() {
		return sysModifiedTime;
	}

	public void setSysModifiedTime(long sysModifiedTime) {
		this.sysModifiedTime = sysModifiedTime;
	}

	public long getSysCreatedTime() {
		return sysCreatedTime;
	}

	public void setSysCreatedTime(long sysCreatedTime) {
		this.sysCreatedTime = sysCreatedTime;
	}

	public long getSysModifiedBy() {
		return sysModifiedBy;
	}

	public void setSysModifiedBy(long sysModifiedBy) {
		this.sysModifiedBy = sysModifiedBy;
	}

	public long getSysCreatedBy() {
		return sysCreatedBy;
	}

	public void setSysCreatedBy(long sysCreatedBy) {
		this.sysCreatedBy = sysCreatedBy;
	}

	private long sysModifiedTime;
	private long sysCreatedTime;
	private long sysModifiedBy;
	private long sysCreatedBy;
}

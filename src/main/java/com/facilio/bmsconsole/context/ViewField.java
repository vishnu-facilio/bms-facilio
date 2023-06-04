package com.facilio.bmsconsole.context;

import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

	private String parentFieldName;
	
	public ViewField(String name, String displayName) {
		this.name = name;
		this.columnDisplayName = displayName;
	}

	public ViewField(String name, String displayName, String parentFieldName) {
		this.name = name;
		this.columnDisplayName = displayName;
		this.parentFieldName =parentFieldName;
	}

	public ViewField(String name, String displayName, long fieldId, long parentFieldId, String customization) {
		this.name = name;
		this.fieldName = name;
		this.fieldId = fieldId;
		this.parentFieldId = parentFieldId;
		this.columnDisplayName = displayName;
		try {
			this.customization = FacilioUtil.parseJson(customization);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public String getParentFieldName() {
		return parentFieldName;
	}

	public void setParentFieldName(String parentFieldName) {
		this.parentFieldName = parentFieldName;
	}

	private JSONObject customization;
	public String getCustomization() {
		return (customization != null) ? customization.toJSONString() : null;
	}
	public void setCustomization(JSONObject customization) {
		this.customization = customization;
	}
	public void setCustomization(String data) throws ParseException {
		if (StringUtils.isNotEmpty(data)) {
			this.customization = (JSONObject) new JSONParser().parse(data);
		}
	}
}

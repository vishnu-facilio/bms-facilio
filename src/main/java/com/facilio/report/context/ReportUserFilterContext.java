package com.facilio.report.context;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.facilio.db.criteria.operators.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter @Setter
public class ReportUserFilterContext {

	public static final String ALL = "all";
	public static final String OTHERS = "others";
	
	private String name;
	private long fieldId;
	private FacilioField field;
	private JSONObject component;
	private ChooseValue chooseValue;
	private List<String> defaultValues;
	private List<String> values;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	
	@JsonIgnore
	@JSON(serialize=false)
	public FacilioField getField() {
		return field;
	}
	public void setField(FacilioField field) {
		this.field = field;
		if (field != null) {
			this.fieldId = field.getFieldId();
		}
	}
	
	public JSONObject getComponent() {
		return component;
	}
	public void setComponent(JSONObject component) {
		this.component = component;
	}
	
	public ChooseValue getChooseValue() {
		return chooseValue;
	}
	public void setChooseValue(ChooseValue chooseValue) {
		this.chooseValue = chooseValue;
	}
	
	public List<String> getDefaultValues() {
		return defaultValues;
	}
	public void setDefaultValues(List<String> defaultValues) {
		this.defaultValues = defaultValues;
	}
	
	@JsonIgnore
	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}
	
	@JsonIgnore
	@JSON(serialize=false)
	private List<String> getData() {
		if (CollectionUtils.isNotEmpty(getValues())) {
			return getValues();
		}
		else if (CollectionUtils.isNotEmpty(getDefaultValues())) {
			return getDefaultValues();
		} else if (chooseValue != null && CollectionUtils.isNotEmpty(chooseValue.getValues())) {
			return chooseValue.getValues();
		}
		return null;
	}
	
	@JsonIgnore
	public Criteria getCriteria() {
		if (field == null) {
			return null;
		}
		
		List<String> data = getData();
		if (CollectionUtils.isEmpty(data)) {
			return null;
		}
		boolean isSelectAll = false;
		if(values == null || (values!= null && values.size() == 0)){
			isSelectAll= true;
		}
		if (data.contains(ALL)) {
			isSelectAll = true;
			if (chooseValue != null) {
				if (chooseValue.isOtherEnabled()) {
					return null;
				} else if (CollectionUtils.isNotEmpty(chooseValue.getValues())) {
					data = chooseValue.getValues();
				}
			}
		}
		
		Criteria c = new Criteria();
		List<String> selectedValues = new ArrayList<>();
		
		Condition otherCriteria = null;

		for (String s : data) {
			if (s.equals(OTHERS) && chooseValue != null && chooseValue.isOtherCriteria()) {
				otherCriteria = new Condition();
				otherCriteria.setField(field);
				otherCriteria.setValue(StringUtils.join(chooseValue.getValues(), ","));
				otherCriteria.setOperator(getOperator(true));
				continue;
			}
			if (!s.equals(ALL)) {
				selectedValues.add(s);
			}
		}
		
		if (CollectionUtils.isNotEmpty(selectedValues)) {
			Condition condition = new Condition();
			condition.setField(field);
			if(isSelectAll) {
				condition.setOperator(CommonOperators.IS_NOT_EMPTY);
			}
			else{
				condition.setValue(StringUtils.join(selectedValues, ","));
				condition.setOperator(getOperator(false));
			}
			
			c.addAndCondition(condition);
		}
		if (otherCriteria != null) {
			c.addOrCondition(otherCriteria);
		}
		
		if (c.isEmpty()) {
			return null;
		}
		return c;
	}
	
	private Operator getOperator(boolean other) {
		switch (field.getDataTypeEnum()) {
		case LOOKUP:
			if(field.getModule().getName().equalsIgnoreCase(FacilioConstants.ContextNames.RESOURCE) 
				&& field.getName().equalsIgnoreCase(FacilioConstants.ContextNames.SPACE)) {
				return other ? PickListOperators.ISN_T : BuildingOperator.BUILDING_IS;
			}
			return other ? PickListOperators.ISN_T : PickListOperators.IS;
		case ENUM:
			return other ? EnumOperators.ISN_T : EnumOperators.IS;
		}
		return null;
	}

	public static class ChooseValue {
		private String type;
		private List<String> values;
		private boolean otherEnabled;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public List<String> getValues() {
			return values;
		}
		public void setValues(List<String> values) {
			this.values = values;
		}
		public boolean isOtherEnabled() {
			return otherEnabled;
		}
		public void setOtherEnabled(boolean otherEnabled) {
			this.otherEnabled = otherEnabled;
		}
		
		@JsonIgnore
		@JSON(serialize = false)
		public boolean isOtherCriteria() {
			return (CollectionUtils.isNotEmpty(values));
		}
	}
	private String moduleName;
	private String fieldName;
}

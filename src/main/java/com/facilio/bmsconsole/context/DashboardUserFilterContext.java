package com.facilio.bmsconsole.context;

import java.util.List;

import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;

import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;

public class DashboardUserFilterContext extends ModuleBaseWithCustomFields {


	
	// default values is type string to handle  ,'all' ,'others' cases
		

	
	public String[] getDefaultValues() {
		return this.filterPojo.getDefaultValues();
	}

	public void setDefaultValues(String[] defaultValues) {
		this.filterPojo.setDefaultValues(defaultValues);
	}



	private FacilioField field;
	public static enum ComponentType implements FacilioEnum {
		 SINGLE_SELECT("Select option"),MULTI_SELECT("Multiple options");

		public static ComponentType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}

		private String name;
		
		
		ComponentType(String name) {
			this.name = name;
		}

		@Override
		public int getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return this.name;
		}

		}

	public static enum OptionType implements FacilioEnum {
		ALL("All"), SOME("Some");

		public static OptionType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}

		private String name;

		OptionType(String name) {
			this.name = name;
		}

		@Override
		public int getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return this.name;
		}

	}

	public static class DashboardUserFilter {

		private String[] defaultValues;
		
		public String[] getDefaultValues() {
			return defaultValues;
		}

		public void setDefaultValues(String[] defaultValues) {
			this.defaultValues = defaultValues;
		}

		ComponentType componentType;

		long fieldId;

		Boolean isOthersOptionEnabled;
		String label;

		OptionType optionType;

		

		List<Long> selectedOptions;

		public ComponentType getComponentType() {
			return componentType;
		}

		public long getFieldId() {
			return fieldId;
		}

		public Boolean getIsOthersOptionEnabled() {
			return isOthersOptionEnabled;
		}

		public String getLabel() {
			return label;
		}

		public OptionType getOptionType() {
			return optionType;
		}



		public List<Long> getSelectedOptions() {
			return selectedOptions;
		}

		public void setComponentType(ComponentType componentType) {
			this.componentType = componentType;
		}

		public void setFieldId(long fieldId) {
			this.fieldId = fieldId;
		}

		public void setIsOthersOptionEnabled(Boolean isOthersOptionEnabled) {
			this.isOthersOptionEnabled = isOthersOptionEnabled;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public void setOptionType(OptionType optionType) {
			this.optionType = optionType;
		}



		public void setSelectedOptions(List<Long> selectedOptions) {
			this.selectedOptions = selectedOptions;
		}

	}


	private static final long serialVersionUID = 1L;

	private long dashboardFilterId;

	// to do , serialize and deserialize context fields to json , than via POJO
	private DashboardUserFilter filterPojo = new DashboardUserFilter();

	private int filterOrder;
	
	
	
	public int getComponentType() {

		if (this.filterPojo.getComponentType() != null) {
			return this.filterPojo.getComponentType().getIndex();
		}
		return -1;
	}

	@JSON(serialize = false)
	public long getDashboardFilterId() {
		return dashboardFilterId;
	}

	@JSON(serialize = false)
	public String getDashboardUserFilterJson() throws Exception {

		if (this.filterPojo != null) {
			// FieldUtil.getAsBeanFromJson(this.dashboardUserFilterJson,
			// DashboardUserFilterContext.DashboardUserFilter.class);
			return FieldUtil.getAsJSON(this.filterPojo).toJSONString();
		} else {
			return null;
		}
	}

	public long getFieldId() {
		return this.filterPojo.getFieldId();
	}

	public Boolean getIsOthersOptionEnabled() {
		return this.filterPojo.getIsOthersOptionEnabled();
	}

	public String getLabel() {
		return this.filterPojo.getLabel();
	}

	public int getOptionType() {
		if (this.filterPojo.getOptionType() != null) {
			return this.filterPojo.getOptionType().getIndex();
		}

		return -1;

	}

	



	public List<Long> getSelectedOptions() {
		return this.filterPojo.getSelectedOptions();
	}

	public void setComponentType(int componentType) {
		this.filterPojo.setComponentType(ComponentType.valueOf(componentType));
	}

	

	public void setDashboardFilterId(long dashboardFilterId) {
		this.dashboardFilterId = dashboardFilterId;
	}

	public void setDashboardUserFilterJson(String dashboardUserFilterJson) throws Exception {

		if (dashboardUserFilterJson != null) {
				
			JSONObject jsonObject=FacilioUtil.parseJson(dashboardUserFilterJson);
			this.filterPojo = FieldUtil.getAsBeanFromJson(jsonObject,
					DashboardUserFilterContext.DashboardUserFilter.class);

		}
	}

	public void setFieldId(long fieldId) {
		this.filterPojo.setFieldId(fieldId);
	}

	public void setIsOthersOptionEnabled(Boolean isOthersOptionEnabled) {
		this.filterPojo.setIsOthersOptionEnabled(isOthersOptionEnabled);
	}

	public void setLabel(String label) {
		this.filterPojo.setLabel(label);
	}
 

	public void setOptionType(int optionType) {
		this.filterPojo.optionType = OptionType.valueOf(optionType);
	}





	public void setSelectedOptions(List<Long> selectedOptions) {
		this.filterPojo.setSelectedOptions(selectedOptions); 
	}

	public FacilioField getField() {
		return field;
	}

	public void setField(FacilioField field) {
		this.field = field;
	}

	public int getFilterOrder() {
		return filterOrder;
	}

	public void setFilterOrder(int filterOrder) {
		this.filterOrder = filterOrder;
	}

}

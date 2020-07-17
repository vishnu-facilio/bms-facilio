package com.facilio.bmsconsole.context;

import java.util.List;

import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;

import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.util.FacilioUtil;

public class DashboardUserFilterContext extends ModuleBaseWithCustomFields {

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

		ComponentType componentType;

		long fieldId;

		Boolean isOthersOptionEnabled;
		String label;

		OptionType optionType;

		int order;

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

		public int getOrder() {
			return order;
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

		public void setOrder(int order) {
			this.order = order;
		}

		public void setSelectedOptions(List<Long> selectedOptions) {
			this.selectedOptions = selectedOptions;
		}

	}


	private static final long serialVersionUID = 1L;

	private long dashboardFilterId;

	// to do , serialize and deserialize context fields to json , than via POJO
	private DashboardUserFilter filterPojo = new DashboardUserFilter();

	
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

	

	public int getOrder() {
		return this.filterPojo.getOrder();
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



	public void setOrder(int order) {
		this.filterPojo.setOrder(order);
	}

	public void setSelectedOptions(List<Long> selectedOptions) {
		this.filterPojo.setSelectedOptions(selectedOptions); 
	}

}

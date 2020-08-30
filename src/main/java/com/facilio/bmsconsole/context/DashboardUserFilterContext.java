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
	public static class DashboardUserFilter {

		private String[] defaultValues;
		
		List<Long> selectedOptions;

		public String[] getDefaultValues() {
			return this.defaultValues;
		}

	

		public List<Long> getSelectedOptions()
		{
			return this.selectedOptions;
		}

		public void setDefaultValues(String[] defaultValues) {
			this.defaultValues = defaultValues;
		}




		public void setSelectedOptions(List<Long> selectedOptions) {
			this.selectedOptions = selectedOptions;
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
	private static final long serialVersionUID = 1L;
	ComponentType componentType;
	long fieldId;
	
	Boolean isOthersOptionEnabled;
	String label;
	OptionType optionType;
	Boolean isAllOptionEnabled;
	private FacilioField field;
	private long dashboardFilterId;
	
	// to do , serialize and deserialize context fields to json , than via POJO
	private DashboardUserFilter filterPojo = new DashboardUserFilter();
	private int filterOrder;
	public int getComponentType()
	{
		if(this.componentType!=null)
		{
			return this.componentType.getIndex();
		}
		else {
			return -1;
		}
	}
	public ComponentType getComponentTypeEnum() {
		return this.componentType;
	}

	@JSON(serialize = false)
	public long getDashboardFilterId() {
		return this.dashboardFilterId;
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

	public String[] getDefaultValues() {
		return this.filterPojo.getDefaultValues();
	}







	

	public FacilioField getField() {
		return this.field;
	}

	public long getFieldId() {
		return this.fieldId;
	}

	public int getFilterOrder() {
		return this.filterOrder;
	}

	
	public Boolean getIsAllOptionEnabled() {
		return this.isAllOptionEnabled;
	}

	public Boolean getIsOthersOptionEnabled() {
		return this.isOthersOptionEnabled;
	}



	public String getLabel() {
		return label;
	}
	public int getOptionType()
	{
		if(this.optionType!=null)
		{
			return this.optionType.getIndex();
		}
		else {
			return -1;
		}
	}

	public OptionType getOptionTypeEnum() {
		return this.optionType;
	}

	public List<Long> getSelectedOptions() {
		return this.filterPojo.getSelectedOptions();
	}


	public void setComponentType(ComponentType componentType)
	{
		this.componentType=componentType;
	}

	public void setComponentType(int componentType) {
		this.componentType = ComponentType.valueOf(componentType);
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
	
	
	


	public void setDefaultValues(String[] defaultValues) {
		this.filterPojo.setDefaultValues(defaultValues);
	}

	public void setField(FacilioField field) {
		this.field = field;
	}



	



	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}


	

	public void setFilterOrder(int filterOrder) {
		this.filterOrder = filterOrder;
	}

	public void setIsAllOptionEnabled(Boolean isAllOptionEnabled) {
		this.isAllOptionEnabled = isAllOptionEnabled;
	}



	public void setIsOthersOptionEnabled(Boolean isOthersOptionEnabled) {
		this.isOthersOptionEnabled = isOthersOptionEnabled;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setOptionType(int optionType) {
		this.optionType = OptionType.valueOf(optionType);
	}

	public void setOptionType(OptionType optionType)
	{
		this.optionType=optionType;
	}

}

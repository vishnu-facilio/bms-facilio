package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.modules.*;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;

import com.facilio.db.criteria.Criteria;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;

public class DashboardUserFilterContext extends ModuleBaseWithCustomFields {

	private Map<Long,FacilioField> widgetFieldMap=new HashMap<Long, FacilioField>();
	Map<Long,FacilioField>  cascadingFilters;
public Map<Long, FacilioField> getCascadingFilters() {
		return cascadingFilters;
	}
	public void setCascadingFilters(Map<Long, FacilioField> cascadingFilters) {
		this.cascadingFilters = cascadingFilters;
	}

	//	private long moduleId=-1;
	String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	private FacilioModule module;
	
	private List<String> selectedOptionsRecordIds;
	
	
	
	
	
	// default values is type string to handle  ,'all' ,'others' cases
		
	public List<String> getSelectedOptionsRecordIds() {
		return selectedOptionsRecordIds;
	}
	@JSON(serialize = false)
	public void setSelectedOptionsRecordIds(List<String> selectedOptionsRecordIds) {
		this.selectedOptionsRecordIds = selectedOptionsRecordIds;
	}
	public FacilioModule getModule() {
		return module;
	}
	public void setModule(FacilioModule module) {
		this.module = module;
	}
//	public long getModuleId() {
//		return moduleId;
//	}
//	public void setModuleId(long moduleId) {
//		this.moduleId = moduleId;
//	}
	public static enum ComponentType implements FacilioIntEnum {
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
		public Integer getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return this.name;
		}

		}

		public static enum FilterDisplayType implements FacilioIntEnum{
			SLIDER("Select Slider"),
			DROPDOWN("Select DropDown");

			public static FilterDisplayType valueOf(int value) {
				if (value > 0 && value <= values().length) {
					return values()[value - 1];
				}
				return null;
			}
			private String name;
			FilterDisplayType(String name) {
				this.name = name;
			}
			@Override
			public Integer getIndex() {
				return ordinal() + 1;
			}

			@Override
			public String getValue() {
				return this.name;
			}
		}
	public static class DashboardUserFilter {

		private String[] defaultValues;
		
		List<String> selectedOptions =new ArrayList<String>();

		public String[] getDefaultValues() {
			return this.defaultValues;
		}

		public Integer getDateTimeOperator() {
			return dateTimeOperator;
		}

		public void setDateTimeOperator(Integer dateTimeOperator) {
			this.dateTimeOperator = dateTimeOperator;
		}

		private Integer dateTimeOperator;

		public List<String> getSelectedOptions()
		{
			return this.selectedOptions;
		}

		public void setDefaultValues(String[] defaultValues) {
			this.defaultValues = defaultValues;
		}




		public void setSelectedOptions(List<String> selectedOptions) {
			if(selectedOptions==null)
			{
				this.selectedOptions=new ArrayList<String>();
			}
			else {
				this.selectedOptions = selectedOptions;
			}
			
		}

		public Integer getDisplayType() {
			return displayType;
		}

		public void setDisplayType(Integer filterDisplayType) {
			this.displayType = filterDisplayType;
		}

		Integer displayType;


		public List<String> getSelectedDayOrHourValues() {
			return this.selectedDayOrHourValues;
		}

		public void setSelectedDayOrHourValues(List<String> selectedDayOrHourValues) {
			this.selectedDayOrHourValues = selectedDayOrHourValues;
		}

		List<String> selectedDayOrHourValues;

		public List<Long> getSelectedSliderRangeValues() {
			return this.selectedSliderRangeValues;
		}

		public void setSelectedSliderRangeValues(List<Long> selectedSliderRangeValues) {
			this.selectedSliderRangeValues = selectedSliderRangeValues;
		}

		List<Long> selectedSliderRangeValues;


		public String getParentModuleName() {
			return parentModuleName;
		}

		public void setParentModuleName(String parentModuleName) {
			this.parentModuleName = parentModuleName;
		}

		public String parentModuleName ;

		public List getWidget_field_mapping() {
			return widget_field_mapping;
		}

		public void setWidget_field_mapping(List widget_field_mapping) {
			this.widget_field_mapping = widget_field_mapping;
		}

		public List widget_field_mapping;


		public boolean isHideFilter() {
			return hideFilter;
		}

		public void setHideFilter(boolean hideFilter) {
			this.hideFilter = hideFilter;
		}

		public boolean hideFilter;

	}
	public static enum OptionType implements FacilioIntEnum {
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
		public Integer getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return this.name;
		}

	}
	private static final long serialVersionUID = 1L;
	ComponentType componentType;
	FilterDisplayType filterDisplayType;
	long fieldId=-1;
	
	Boolean isOthersOptionEnabled;
	String label;
	OptionType optionType;
	Boolean isAllOptionEnabled;
	Boolean showOnlyRelevantValues;
	public Boolean getShowOnlyRelevantValues() {
		return showOnlyRelevantValues;
	}
	public void setShowOnlyRelevantValues(Boolean showOnlyRelevantValues) {
		this.showOnlyRelevantValues = showOnlyRelevantValues;
	}

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
	public List<Long> getSelectedSliderRangeValues() {
		return this.filterPojo.getSelectedSliderRangeValues();
	}
	public List<String> getSelectedDayOrHourValues() {
		return this.filterPojo.getSelectedDayOrHourValues();
	}
	public Integer getFilterDisplayType() {
		return this.filterDisplayType!=null ? this.filterDisplayType.getIndex(): this.filterPojo.getDisplayType() != null ? this.filterPojo.getDisplayType() : 1;
	}
	public FilterDisplayType getFilterDisplayTypeEnum(){
		return this.filterDisplayType;
	}
	public Integer getDateTimeOperator() {
		return this.filterPojo.getDateTimeOperator() != null ? this.filterPojo.getDateTimeOperator() : 103;
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

	public List<String> getSelectedOptions() {
		if(this.filterPojo.getSelectedOptions()==null)
		{
			return new ArrayList<String>(); 
		}
		else {
		return this.filterPojo.getSelectedOptions();
		}
	}

	public void setSelectedOptions(List<String> selectedOptions) {
		this.filterPojo.setSelectedOptions(selectedOptions);
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
	public void setSelectedSliderRangeValues(List<Long> setSelectedSliderRangeValues) {
		this.filterPojo.setSelectedSliderRangeValues(setSelectedSliderRangeValues);
	}

	public void setSelectedDayOrHourValues(List<String> selectedDayOrHourValues) {
		this.filterPojo.setSelectedDayOrHourValues(selectedDayOrHourValues);
	}
	public void setFilterDisplayType(Integer filterDisplayType) {
		this.filterPojo.setDisplayType(filterDisplayType);
		this.filterDisplayType = FilterDisplayType.valueOf(filterDisplayType);
	}
	public void setFilterDisplayTypeEnum(FilterDisplayType filterDisplayType) {
		this.filterDisplayType = filterDisplayType;
	}
	public void setDateTimeOperator(Integer dateTimeOperator){
		this.filterPojo.setDateTimeOperator(dateTimeOperator);
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
	public Map<Long,FacilioField> getWidgetFieldMap() {
		return this.widgetFieldMap;
	}
	public void setWidgetFieldMap(Map<Long,FacilioField> widgetFieldMap) {
		this.widgetFieldMap = widgetFieldMap;
	}
	
	private long criteriaId = -1;
	
	public long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	
	private Criteria criteria;
	
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	public void setParentModuleName(String parentModuleName){
		this.filterPojo.setParentModuleName(parentModuleName);
	}
	public String getParentModuleName(){
		return this.filterPojo.getParentModuleName();
	}

	public List getWidget_field_mapping() {
		return this.filterPojo.getWidget_field_mapping();
	}
	public void setWidget_field_mapping(List widget_field_mapping) {
		this.filterPojo.setWidget_field_mapping(widget_field_mapping);
	}

	public Long getWidget_id() {
		return widget_id;
	}

	public void setWidget_id(Long widget_id) {
		this.widget_id = widget_id;
	}

	public Long widget_id;

	public String getLink_name() {
		return link_name;
	}

	public void setLink_name(String link_name) {
		this.link_name = link_name;
	}

	public String link_name;

	public void setHideFilter(boolean hideFilter){
		this.filterPojo.setHideFilter(hideFilter);
	}
	public boolean getHideFilter(){
		return this.filterPojo.isHideFilter();
	}
}

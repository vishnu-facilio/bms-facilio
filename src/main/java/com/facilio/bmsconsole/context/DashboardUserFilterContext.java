package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.modules.*;
import com.facilio.report.context.ReportDataPointContext;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;

import com.facilio.db.criteria.Criteria;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;

public class DashboardUserFilterContext extends ModuleBaseWithCustomFields {

	private Map<Object,FacilioField> widgetFieldMap=new HashMap<>();
	private Map<Object,List<ReportDataPointContext>> readingWidgetFieldMap = new HashMap<>();
	private Map<Object,FacilioField> widgetExcludeFieldMap=new HashMap<>();
	private Map<Long,String> widgetModuleMap=new HashMap<>();
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
			DROPDOWN("Select DropDown"),
			LIST("Select from List");

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
	long commonFieldId=-1;
	
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
	public long getCommonFieldId() {
		return this.commonFieldId;
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
	public void setCommonFieldId(long commonFieldId) {
		this.commonFieldId = commonFieldId;
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
	public Map<Object,FacilioField> getWidgetFieldMap() {
		return this.widgetFieldMap;
	}
	public void setWidgetFieldMap(Map<Object,FacilioField> widgetFieldMap) {
		this.widgetFieldMap = widgetFieldMap;
	}
	public Map<Long,String> getWidgetModuleMap() {
		return this.widgetModuleMap;
	}
	public void setWidgetModuleMap(Map<Long,String> widgetModuleMap) {
		this.widgetModuleMap = widgetModuleMap;
	}
	public Map<Object, List<ReportDataPointContext>> getReadingWidgetFieldMap() {
		return this.readingWidgetFieldMap;
	}
	public void setReadingWidgetFieldMap(Map<Object,List<ReportDataPointContext>> readingWidgetFieldMap) {
		this.readingWidgetFieldMap = readingWidgetFieldMap;
	}
	public Map<Object,FacilioField> getWidgetExcludeFieldMap() {
		return this.widgetExcludeFieldMap;
	}
	public void setWidgetExcludeFieldMap(Map<Object,FacilioField> widgetExcludeFieldMap) {
		this.widgetExcludeFieldMap = widgetExcludeFieldMap;
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
	private JSONObject metaJSON;
	public JSONObject getMetaJSON() {
		if(metaJSON == null) {
			metaJSON =  new JSONObject();
		}
		return metaJSON;
	}
	public void setMetaJSON(JSONObject metaJSON) {
		this.metaJSON = metaJSON;
	}
	private long sectionId;
	public void setSectionId(long sectionId){
		this.sectionId = sectionId;
	}
	public long getSectionId() {
		return this.sectionId;
	}
	public void setxPosition(int xPosition) {
		getMetaJSON().put("xPosition", xPosition);
	}
	public Integer getxPosition(){
		if(getMetaJSON().get("xPosition") != null){
			return Integer.parseInt(getMetaJSON().get("xPosition").toString());
		}
		return null;
	}
	public void setyPosition(Integer yPosition){
		getMetaJSON().put("yPosition", yPosition);
	}
	public Integer getyPosition(){
		if(getMetaJSON().get("yPosition") != null){
			return Integer.parseInt(getMetaJSON().get("yPosition").toString());
		}
		return null;
	}
	public void setLayoutPosition(Integer layoutPosition){
		getMetaJSON().put("layoutPosition", layoutPosition);
	}
	public Integer getLayoutPosition(){
		if(getMetaJSON().get("layoutPosition") != null){
			return Integer.parseInt(getMetaJSON().get("layoutPosition").toString());
		}
		return null;
	}
	public void setLayoutWidth(Integer layoutWidth){
		getMetaJSON().put("layoutWidth", layoutWidth);
	}
	public Integer getLayoutWidth(){
		if(getMetaJSON().get("layoutWidth") != null){
			return Integer.parseInt(getMetaJSON().get("layoutWidth").toString());
		}
		return null;
	}
	public void setLayoutHeight(Integer layoutHeight){
		getMetaJSON().put("layoutHeight", layoutHeight);
	}
	public Integer getLayoutHeight(){
		if(getMetaJSON().get("layoutHeight") != null){
			return Integer.parseInt(getMetaJSON().get("layoutHeight").toString());
		}
		return null;
	}
	private int xPosition=-1;
	private int yPosition=-1;
	private int layoutWidth=-1;
	private int layoutHeight=-1;
	private int layoutPosition=-1;
}

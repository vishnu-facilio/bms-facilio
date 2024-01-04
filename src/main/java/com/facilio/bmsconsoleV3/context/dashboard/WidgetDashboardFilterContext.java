package com.facilio.bmsconsoleV3.context.dashboard;

import com.facilio.bmsconsole.context.DashboardFieldMappingContext;
import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.context.DashboardUserFilterContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.util.DashboardFilterUtil;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

@Setter
@Getter
public class WidgetDashboardFilterContext extends DashboardWidgetContext
{
    private static final Logger LOGGER = Logger.getLogger(WidgetDashboardFilterContext.class.getName());
    public static class WidgetDashboardFilter {

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
    public static enum ComponentType implements FacilioIntEnum {
        SINGLE_SELECT("Select option"),MULTI_SELECT("Multiple options");

        public static WidgetDashboardFilterContext.ComponentType valueOf(int value) {
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

        public static WidgetDashboardFilterContext.FilterDisplayType valueOf(int value) {
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
    public static enum OptionType implements FacilioIntEnum {
        ALL("All"), SOME("Some");

        public static WidgetDashboardFilterContext.OptionType valueOf(int value) {
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
    public String moduleName;
    public String label;
    public FacilioField field;
    public Long fieldId=-1l;
    public long commonFieldId=-1;
    public FacilioModule module;
    public Long dashboardFilterId;
    public Integer filterOrder;
    public DashboardFilterContext dashboardFilter;
    public long widget_id;
    public List<DashboardFieldMappingContext> fieldMappingMap;
    public List<String> defaultValues;
    public String dashboardUserFilterJson;
    public long criteriaId = -1;
    public Criteria criteria;
    public Integer dateTimeOperator;
    public List<String> selectedOptions =new ArrayList<String>();
    List<Long> selectedSliderRangeValues;
    List<String> selectedDayOrHourValues;
    ComponentType componentType;
    FilterDisplayType filterDisplayType;
    Boolean isAllOptionEnabled;
    Boolean isOthersOptionEnabled;
    Boolean showOnlyRelevantValues;
    private String filterLinkName;
    private String widgetLinkName;
    private Map<String,String> linkNameMap;
    OptionType optionType;

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
    public void setOptionType(int optionType) {
        this.optionType = OptionType.valueOf(optionType);
    }

    public void setFilterDisplayType(Integer filterDisplayType) {
        this.filterPojo.setDisplayType(filterDisplayType);
        this.filterDisplayType = WidgetDashboardFilterContext.FilterDisplayType.valueOf(filterDisplayType);
    }
    public void setComponentType(WidgetDashboardFilterContext.ComponentType componentType)
    {
        this.componentType=componentType;
    }
    public void setComponentType(int componentType) {
        if(componentType != 0){
            this.componentType = WidgetDashboardFilterContext.ComponentType.valueOf(componentType);
        }
        else{
            this.componentType = WidgetDashboardFilterContext.ComponentType.valueOf("SINGLE_SELECT");
        }
    }
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
    public WidgetDashboardFilter filterPojo = new WidgetDashboardFilter();
    public String getDashboardUserFilterJson() throws Exception {
        if (this.filterPojo != null) {
            return FieldUtil.getAsJSON(this.filterPojo).toJSONString();
        } else {
            return null;
        }
    }
    public void setDashboardUserFilterJson(String dashboardUserFilterJson) throws Exception {
        if (dashboardUserFilterJson != null) {
            JSONObject jsonObject= FacilioUtil.parseJson(dashboardUserFilterJson);
            this.filterPojo = FieldUtil.getAsBeanFromJson(jsonObject,
                    WidgetDashboardFilter.class);
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
    public void setDateTimeOperator(Integer dateTimeOperator){
        this.filterPojo.setDateTimeOperator(dateTimeOperator);
    }
    public void setSelectedOptions(List<String> selectedOptions) {
        this.filterPojo.setSelectedOptions(selectedOptions);
    }

    @Override
    public JSONObject widgetJsonObject(boolean optimize) {
        JSONObject resultJson = new JSONObject();
        resultJson.put("id", getId());
        resultJson.put("link_name", getLinkName());
        resultJson.put("type", getWidgetType().getName());
        resultJson.put("helpText",getHelpText());
        resultJson.put("dashboardFilterId", getDashboardFilterId());
        resultJson.put("moduleName", moduleName);
        resultJson.put("sectionId",getSectionId());

        JSONObject layoutJson = new JSONObject();
        layoutJson.put("height", getLayoutHeight());
        layoutJson.put("width", getLayoutWidth());
        layoutJson.put("x", getxPosition());
        layoutJson.put("y", getyPosition());
        layoutJson.put("position", getLayoutPosition());
        resultJson.put("layout", layoutJson);

        JSONObject mlayoutJson = new JSONObject();
        mlayoutJson.put("height", getmLayoutHeight());
        mlayoutJson.put("width", getmLayoutWidth());
        mlayoutJson.put("x", getmXPosition());
        mlayoutJson.put("y", getmYPosition());
        mlayoutJson.put("position", getmLayoutPosition());
        resultJson.put("mLayout", mlayoutJson);

        JSONObject headerJson = new JSONObject();
        headerJson.put("title", getHeaderText());
        resultJson.put("header", headerJson);

        JSONObject dataOptionsJson = new JSONObject();
        dataOptionsJson.put("name", "dummy");
        resultJson.put("dataOptions", dataOptionsJson);
        resultJson.put("customActions", getCustomActions());

        JSONObject widgetJson = new JSONObject();
        widgetJson.put("widget", resultJson);
        widgetJson.put("label", getWidgetName());
        return widgetJson;
    }
    @Override
    public JSONObject widgetMobileJsonObject(boolean optimize, int index) {

        JSONObject widgetJson = new JSONObject();
        widgetJson.put("id", getId());
        widgetJson.put("label", getWidgetName());
        widgetJson.put("link_name", getLinkName());
        widgetJson.put("type", getWidgetType().getName());
        widgetJson.put("dashboardFilterId", getDashboardFilterId());
        widgetJson.put("title", getHeaderText());
        widgetJson.put("sequence", index);
        try{
            if(widget_id > 0){
                DashboardUserFilterContext filterData = DashboardFilterUtil.getDashboardUserFiltersForWidgetId(widget_id);
                widgetJson.put("filterData", DashboardUtil.getDashboardMobileUserFilterContext(filterData));
            }
        }
        catch(Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return widgetJson;
    }
}

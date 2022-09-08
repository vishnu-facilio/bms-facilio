package com.facilio.bmsconsoleV3.context.dashboard;

import com.facilio.bmsconsole.context.DashboardUserFilterContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

@Setter
@Getter
@Log4j
public class WidgetDashboardFilterContext extends DashboardWidgetContext
{
    private static final long serialVersionUID = 1L;
    public String moduleName;
    public String label;
    public FacilioField field;
    public Long fieldId=-1l;
    public FacilioModule module;
    public Long dashboardFilterId;
    public Integer filterOrder;
//    public WidgetType widgetType = WidgetType.FILTER;
//    public Integer  type = WidgetType.FILTER.getValue();

    @Override
    public JSONObject widgetJsonObject(boolean optimize) {
        JSONObject resultJson = new JSONObject();
        resultJson.put("moduleName", moduleName);
        resultJson.put("dashboardFilterId", getDashboardFilterId());
        resultJson.put("filterOrder", getFilterOrder());
        resultJson.put("widgetType",  getWidgetType());
        resultJson.put("helpText",getLabel());
        resultJson.put("type", getWidgetType().getName());
        resultJson.put("x", getX());
        resultJson.put("y", getY());
        resultJson.put("w", getW());
        resultJson.put("h", getH());
        resultJson.put("minW", getMinW());
        resultJson.put("maxW", getMaxW());
        resultJson.put("link_name", getLinkName());

        JSONObject widgetJson = new JSONObject();
        widgetJson.put("widget", resultJson);
        widgetJson.put("label", getLabel());
        return widgetJson;
    }
}

package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.DashboardWidgetContext;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class WidgetSectionContext extends DashboardWidgetContext {

    private static final long serialVersionUID = 1L;
    private String name;
    private String desc;
    private String banner_meta;

    private List<DashboardWidgetContext> widgets_in_section = new ArrayList<DashboardWidgetContext>();
    private Boolean noResize;
    private Boolean collapsed;

    @Override
    public JSONObject widgetJsonObject(boolean optimize) {
        JSONObject resultJson = new JSONObject();
        resultJson.put("name", getName());
        resultJson.put("desc", getDesc());
        resultJson.put("id", getId());
        resultJson.put("type", getWidgetType().getName());
        resultJson.put("helpText",getHelpText());
        resultJson.put("noResize", getNoResize());
        resultJson.put("collapsed", getCollapsed());

        resultJson.put("x", getX());
        resultJson.put("y", getY());
        resultJson.put("w", getW());
        resultJson.put("h", getH());
        resultJson.put("minW", getMinW());
        resultJson.put("maxW", getMaxW());
        if(getBanner_meta() != null) {
            resultJson.put("banner_meta", getBanner_meta());
        }

        JSONArray childrenArray = new JSONArray();
        resultJson.put("section", childrenArray);
        if(getWidgets_in_section() != null && getWidgets_in_section().size() > 0) {
            resultJson.put("section", childrenArray);
            for(DashboardWidgetContext widget_context : getWidgets_in_section())
            {
                childrenArray.add(widget_context.widgetJsonObject(false));
            }
        }

        JSONObject widgetJson = new JSONObject();
        widgetJson.put("widget", resultJson);
        widgetJson.put("label", getWidgetName());
        return widgetJson;
    }
    @Override
    public JSONObject widgetMobileJsonObject(boolean optimize) {

        JSONObject widgetJson = new JSONObject();
        widgetJson.put("label", getWidgetName());
        widgetJson.put("id", getId());
        widgetJson.put("name", getName());
        JSONArray childrenArray = new JSONArray();
        if(getWidgets_in_section() != null && getWidgets_in_section().size() > 0) {
            widgetJson.put("widgets", childrenArray);
            for(DashboardWidgetContext widget_context : getWidgets_in_section())
            {
                childrenArray.add(widget_context.widgetMobileJsonObject(false));
            }
        }
        return widgetJson;
    }
}

package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.DashboardWidgetContext;
import lombok.Getter;
import lombok.Setter;
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
        resultJson.put("section", getWidgets_in_section());
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

        JSONObject widgetJson = new JSONObject();
        widgetJson.put("widget", resultJson);
        widgetJson.put("label", getWidgetName());
        return widgetJson;
    }
}

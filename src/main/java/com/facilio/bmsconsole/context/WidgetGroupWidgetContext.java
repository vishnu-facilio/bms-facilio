package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.page.PageWidget;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Setter;
import org.json.simple.JSONObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WidgetGroupWidgetContext extends PageSectionWidgetContext{

    public WidgetGroupWidgetContext() {
    }

    @JsonIgnore
    @Setter
    private WidgetGroupSectionContext parentContext;

    public WidgetGroupSectionContext widgetGroupWidgetDone() {
        return this.parentContext;
    }


    public WidgetGroupWidgetContext(String name, String displayName, PageWidget.WidgetType widgetType, Double sequenceNumber, long positionX, long positionY, JSONObject widgetParams, JSONObject widgetDetail) {
        super(name, displayName, widgetType, sequenceNumber, positionX, positionY, widgetParams, widgetDetail);
    }

    public WidgetGroupWidgetContext(String name, String displayName, PageWidget.WidgetType widgetType, String configName, Double sequenceNumber, long positionX, long positionY, JSONObject widgetParams, JSONObject widgetDetail) {
        super(name, displayName, widgetType, configName, sequenceNumber, positionX, positionY, widgetParams, widgetDetail);
    }

    public WidgetGroupWidgetContext(String name, String displayName, PageWidget.WidgetType widgetType, long widgetConfigId, Double sequenceNumber, long positionX, long positionY, JSONObject widgetParams, JSONObject widgetDetail) {
        super(name, displayName, widgetType, widgetConfigId, sequenceNumber, positionX, positionY, widgetParams, widgetDetail);
    }
}

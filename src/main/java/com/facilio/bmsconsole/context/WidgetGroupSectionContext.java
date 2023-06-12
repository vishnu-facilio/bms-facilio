package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.page.PageWidget;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class WidgetGroupSectionContext {
    public WidgetGroupSectionContext() {

    }
    private long id = -1;
    private long orgId = -1;
    private long widgetId = -1;
    private String name;
    private String displayName;
    private Double sequenceNumber=-1D;
    private String description;
    private Long sysCreatedBy;
    private Long sysCreatedTime;
    private Long sysModifiedBy;
    private Long sysModifiedTime;
    private List<WidgetGroupWidgetContext> widgets;
    @JsonIgnore
    private WidgetGroupContext parentContext;
    public WidgetGroupContext widgetGroupSectionDone() {
        return this.parentContext;
    }

    public WidgetGroupWidgetContext addWidget(String name, String displayName, PageWidget.WidgetType widgetType, String configName, long positionX, long positionY, JSONObject widgetParams, JSONObject widgetDetail) throws Exception{

        double sequenceNumber = CollectionUtils.isNotEmpty(this.getWidgets()) ? ((this.getWidgets().size()+1) * 10D ) : 10; //(number of widget in section incremented by one * 10) to get sequence number
        WidgetGroupWidgetContext widget = new WidgetGroupWidgetContext(name, displayName, widgetType, configName, sequenceNumber, positionX, positionY, widgetParams, widgetDetail);
        if(this.getWidgets() == null) {
            this.setWidgets(new ArrayList<>(Arrays.asList(widget)));
        }
        else {
            this.getWidgets().add(widget);
        }
        widget.setParentContext(this);
        return widget;
    }

    public WidgetGroupSectionContext(String name, String displayName, Double sequenceNumber, String description) {
        this.name = name;
        this.displayName = displayName;
        this.sequenceNumber = sequenceNumber;
        this.description = description;
    }
}

package com.facilio.bmsconsole.context;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.modules.FieldUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageSectionWidgetContext {
    public PageSectionWidgetContext() {
    }

    private long id = -1;
    private long orgId = -1;
    private long sectionId = -1;
    private String name;
    private String displayName;
    private Long widgetConfigId;
    private String widgetConfigName;

    public PageSectionWidgetContext(String name, String displayName, PageWidget.WidgetType widgetType, Double sequenceNumber, long positionX, long positionY, JSONObject widgetParams, JSONObject widgetDetail) {
        this.name = name;
        this.displayName = displayName;
        this.widgetType = widgetType;
        this.sequenceNumber = sequenceNumber;
        this.positionX = positionX;
        this.positionY = positionY;
        this.widgetParams = widgetParams;
        this.widgetDetail = widgetDetail;
    }

    public PageSectionWidgetContext(String name, String displayName, PageWidget.WidgetType widgetType, String configName, Double sequenceNumber, long positionX, long positionY, JSONObject widgetParams, JSONObject widgetDetail) {
        this(name, displayName, widgetType, sequenceNumber, positionX, positionY, widgetParams, widgetDetail);
        this.setWidgetConfigName(configName);
    }

    public PageSectionWidgetContext(String name, String displayName, PageWidget.WidgetType widgetType, long widgetConfigId, Double sequenceNumber, long positionX, long positionY, JSONObject widgetParams, JSONObject widgetDetail) {
        this(name, displayName, widgetType, sequenceNumber, positionX, positionY, widgetParams, widgetDetail);
        this.setWidgetConfigId(widgetConfigId);
    }

    public void setWidgetType(PageWidget.WidgetType widgetType) throws Exception {
        this.widgetType = widgetType;
        this.setWidgetTypeObj(FieldUtil.getAsProperties(widgetType));
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private PageWidget.WidgetType widgetType;
    private Map<String, Object> widgetTypeObj;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private WidgetConfigContext.ConfigType configType;
    private Double sequenceNumber = -1D;
    private long positionX = -1;
    private long positionY = -1;
    private long width = -1;
    private long height = -1;
    private Long sysCreatedBy;
    private Long sysCreatedTime;
    private Long sysModifiedBy;
    private Long sysModifiedTime;

    public void setWidgetParams(JSONObject widgetParams){
        this.widgetParams = widgetParams;
    }
    public void setWidgetParams(String widgetParams) throws Exception {
        if (StringUtils.isNotEmpty(widgetParams)) {
            this.widgetParams = (JSONObject) new JSONParser().parse(widgetParams);
        }
    }


    public JSONObject getWidgetParams(){
        return this.widgetParams;
    }
    @JsonIgnore
    public String getWidgetParamsAsString(){
        if(this.widgetParams != null) {
            return widgetParams.toJSONString();
        }
        return null;
    }
    private JSONObject widgetParams;
    private JSONObject widgetDetail;
    private Boolean hasLicenseEnabled;

    @JsonIgnore
    private PageSectionContext parentContext;
    public PageSectionContext widgetDone() {
        return this.parentContext;
    }
}


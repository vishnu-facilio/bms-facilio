package com.facilio.bmsconsole.context;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.modules.FacilioIntEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Getter
@Setter
public class PageSectionWidgetContext {
    private long id = -1;
    private long orgId = -1;
    private long sectionId = -1;
    private String name;
    private String displayName;
    private long widgetConfigId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private PageWidget.WidgetType widgetType;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ConfigType configType;
    private Double sequenceNumber = -1D;
    private long positionX = -1;
    private long positionY = -1;
    private long width = -1;
    private long height = -1;
    private Long sysCreatedBy;
    private Long sysCreatedTime;
    private Long sysModifiedBy;
    private Long sysModifiedTime;
    private JSONObject widgetDetail;

    public enum ConfigType implements FacilioIntEnum {
        FIXED("fixed"),
        FLEXIBLE("flexible");
        private final String name;
        ConfigType(String name){
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public static ConfigType valueOf (int value) {
            if (value > 0 && value <= values().length) {
                return values() [value - 1];
            }
            return null;
        }
    }
}


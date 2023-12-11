package com.facilio.bmsconsole.context;

import com.facilio.modules.FacilioIntEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WidgetConfigContext {

    public WidgetConfigContext(){

    }
    public WidgetConfigContext(String name, String displayName, long minHeight, PagesContext.PageLayoutType layoutType) {
        this(name, displayName, ConfigType.FLEXIBLE, minHeight, -1, layoutType);
    }
    public WidgetConfigContext(String name, String displayName, long minHeight, long width, PagesContext.PageLayoutType layoutType) {
        this(name, displayName, ConfigType.FIXED, minHeight, width, layoutType);
    }
    public WidgetConfigContext(String name, String displayName, ConfigType configType, long minHeight, long minWidth, PagesContext.PageLayoutType layoutType){
        this.name = name;
        this.displayName = displayName;
        this.configType = configType;
        this.minHeight = minHeight;
        this.minWidth = minWidth;
        this.layoutType = layoutType == null? PagesContext.PageLayoutType.WEB : layoutType;
    }

    private long id =-1;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PagesContext.PageLayoutType layoutType;
    private String displayName;
    private long widgetId =-1;
    private long minHeight =-1;
//    private long maxHeight = -1;
    private long minWidth = -1;
//    private long maxWidth = -1;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ConfigType configType;
    public enum ConfigType implements FacilioIntEnum {

        FIXED("fixed"),
        FLEXIBLE("flexible"),
        SINGLE_PAGE_WIDGET("singlePageWidget");

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

    @Getter
    public enum WidgetHeight implements FacilioIntEnum {
        SMALL(2),
        MEDIUM(4),
        LARGE(6);

        private final long height;

        WidgetHeight(long height) {
            this.height = height;
        }
    }

    @Getter
    public enum WidgetWidth implements FacilioIntEnum {
        ONE_FOURTH(3),
        HALF(6),
        THREE_FOURTH(9),
        FULL(12);
        private final long width;

        WidgetWidth(long width) {
            this.width = width;
        }
    }
}

package com.facilio.bmsconsole.context;

import com.facilio.modules.FacilioIntEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WidgetConfigContext {

    public WidgetConfigContext(){

    }
    public WidgetConfigContext(ConfigType configType, long minHeight, long minWidth){
        this.configType = configType;
        this.minHeight = minHeight;
        this.minWidth = minWidth;
    }
    private long id =-1;
    private long widgetId =-1;
    private long minHeight =-1;
    private long maxHeight = -1;
    private long minWidth = -1;
    private long maxWidth = -1;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ConfigType configType;
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

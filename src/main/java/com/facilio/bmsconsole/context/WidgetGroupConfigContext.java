package com.facilio.bmsconsole.context;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioStringEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WidgetGroupConfigContext {
    public WidgetGroupConfigContext(){

    }

    private long widgetId;
    private ConfigType configType;

    public WidgetGroupConfigContext(ConfigType configType) {
        this.configType = configType;
    }

    public enum ConfigType implements FacilioStringEnum {
        TAB("tab");
//        CAROUSEL("carousel");
        private final String name;
        ConfigType(String name){
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public static WidgetGroupConfigContext.ConfigType valueOf (int value) {
            if (value > 0 && value <= values().length) {
                return values() [value - 1];
            }
            return null;
        }
    }
}

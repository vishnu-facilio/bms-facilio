package com.facilio.bmsconsole.context;

import com.facilio.modules.FacilioStringEnum;
import lombok.Getter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum IconType implements FacilioStringEnum {
    HOME("Home"),
    SETTINGS("Settings"),
    CONTRACTS("Contracts"),
    COMMUNITY("Community"),
    CONNECTED_APPS("Connected Apps");

    private static final Map<String, IconType> ICON_NAME_VS_TYPE = initIconNamevsType();

    private final String iconName;

    IconType(String iconName) {
        this.iconName = iconName;
    }

    public static IconType valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
    }
    private static Map<String, IconType> initIconNamevsType() {
        Map<String, IconType> iconNameVsIconType = new HashMap<>(values().length);
        for (IconType type : values()) {
            iconNameVsIconType .put(type.getValue(), type);
        }
        return Collections.unmodifiableMap(iconNameVsIconType);
    }

}

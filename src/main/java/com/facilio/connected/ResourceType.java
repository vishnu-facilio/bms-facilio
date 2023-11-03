package com.facilio.connected;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioIntEnum;

import java.util.Arrays;

public enum ResourceType implements FacilioIntEnum {
    ASSET_CATEGORY(FacilioConstants.ContextNames.ASSET),
    SPACE_CATEGORY(FacilioConstants.ContextNames.SPACE),
    METER_CATEGORY(FacilioConstants.Meter.METER),
    SITE(FacilioConstants.ContextNames.SITE);

    String moduleName ;
    ResourceType (String moduleName) {
        this.moduleName = moduleName;
    }
    ResourceType () {}
    public static ResourceType valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        throw new IllegalArgumentException("Invalid resource Type index");
    }
    public String getModuleName() {
        return moduleName;
    }

    public static ResourceType getResourceTypeFromModuleName(String moduleName){
        return Arrays.stream(ResourceType.values())
                .filter(r -> moduleName.equals(r.getModuleName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Module Name supplied to getResourceTypeFromModuleName " + moduleName));
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}

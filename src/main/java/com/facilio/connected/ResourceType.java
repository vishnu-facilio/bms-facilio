package com.facilio.connected;

import com.facilio.connected.scopeHandler.AssetCommissioningHandler;
import com.facilio.connected.scopeHandler.MeterCommissioningHandler;
import com.facilio.connected.scopeHandler.ScopeCommissioningHandler;
import com.facilio.connected.scopeHandler.SpaceCommissioningHandler;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ResourceType implements FacilioIntEnum {
    ASSET_CATEGORY(FacilioConstants.ContextNames.ASSET, new AssetCommissioningHandler(), "Asset"),
    SPACE_CATEGORY(FacilioConstants.ContextNames.SPACE, new SpaceCommissioningHandler(), "Space"),
    METER_CATEGORY(FacilioConstants.Meter.METER, new MeterCommissioningHandler(), "Meter"),
    SITE(FacilioConstants.ContextNames.SITE, null, "Site");

    String moduleName;
    ScopeCommissioningHandler scopeHandler;
    String name;

    ResourceType() {
    }

    ResourceType(String moduleName) {
        this.moduleName = moduleName;
    }

    ResourceType(String moduleName, ScopeCommissioningHandler scopeCommissioningHandler, String name) {
        this.moduleName = moduleName;
        this.scopeHandler = scopeCommissioningHandler;
        this.name = name;
    }

    public static ResourceType valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
    }

    public String getModuleName() {
        return moduleName;
    }

    public static ResourceType getResourceTypeFromModuleName(String moduleName) {
        return Arrays.stream(ResourceType.values())
                .filter(r -> moduleName.equals(r.getModuleName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Module Name supplied to getResourceTypeFromModuleName " + moduleName));
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public String getValue() {
        return this.name;
    }
}

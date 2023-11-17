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
    ASSET_CATEGORY(FacilioConstants.ContextNames.ASSET,new AssetCommissioningHandler()),
    SPACE_CATEGORY(FacilioConstants.ContextNames.SPACE,new SpaceCommissioningHandler()),
    METER_CATEGORY(FacilioConstants.Meter.METER,new MeterCommissioningHandler()),
    SITE(FacilioConstants.ContextNames.SITE);

    String moduleName ;
    ScopeCommissioningHandler scopeHandler;

    ResourceType () {}

    ResourceType(String moduleName){
        this.moduleName = moduleName;
    }

    ResourceType (String moduleName, ScopeCommissioningHandler scopeCommissioningHandler) {
        this.moduleName = moduleName;
        this.scopeHandler = scopeCommissioningHandler;
    }

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

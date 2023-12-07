package com.facilio.connected;

import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.MetersAPI;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.connected.scopeHandler.AssetCommissioningHandler;
import com.facilio.connected.scopeHandler.MeterCommissioningHandler;
import com.facilio.connected.scopeHandler.ScopeCommissioningHandler;
import com.facilio.connected.scopeHandler.SpaceCommissioningHandler;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum ResourceType implements FacilioIntEnum {
    ASSET_CATEGORY(FacilioConstants.ContextNames.ASSET, new AssetCommissioningHandler(), "Asset") {
        @Override
        public String getCategoryModuleName() {
            return FacilioConstants.ContextNames.ASSET_CATEGORY;
        }

        @Override
        public Long getModuleId(V3Context category) {
            return ((V3AssetCategoryContext) category).getAssetModuleID();
        }

        @Override
        public <T extends ModuleBaseWithCustomFields> List<T> getResources(Long categoryId) throws Exception {
            return (List<T>) AssetsAPI.getAssetListOfCategory(categoryId);
        }

    },
    SPACE_CATEGORY(FacilioConstants.ContextNames.SPACE, new SpaceCommissioningHandler(), "Space") {
        @Override
        public String getCategoryModuleName() {
            throw new IllegalArgumentException("Not Supported Yet");
        }

        @Override
        public Long getModuleId(V3Context category) {
            return -1L;
        }

        @Override
        public <T extends ModuleBaseWithCustomFields> List<T> getResources(Long categoryId) throws Exception {
            throw new IllegalArgumentException("Not Supported Yet");
        }
    },
    METER_CATEGORY(FacilioConstants.Meter.METER, new MeterCommissioningHandler(), "Meter") {
        @Override
        public String getCategoryModuleName() {
            return FacilioConstants.Meter.UTILITY_TYPE;
        }

        @Override
        public Long getModuleId(V3Context category) {
            return ((V3UtilityTypeContext) category).getMeterModuleID();
        }

        @Override
        public <T extends ModuleBaseWithCustomFields> List<T> getResources(Long categoryId) throws Exception {
            return (List<T>) MetersAPI.getMeterListOfUtilityType(categoryId);
        }
    },
    SITE(FacilioConstants.ContextNames.SITE, null, "Site") {
        @Override
        public String getCategoryModuleName() {
            throw new IllegalArgumentException("Not Supported Yet");
        }

        @Override
        public Long getModuleId(V3Context category) throws Exception {
            FacilioModule siteModule= Constants.getModBean().getModule(FacilioConstants.ContextNames.SITE);
            return siteModule.getModuleId();
        }

        @Override
        public <T extends ModuleBaseWithCustomFields> List<T> getResources(Long categoryId) throws Exception {
            return new ArrayList<>();
        }

    };

    @Getter
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

    public abstract String getCategoryModuleName() throws Exception;
    public abstract Long getModuleId(V3Context category) throws Exception;
    public abstract <T extends ModuleBaseWithCustomFields> List<T> getResources(Long categoryId) throws Exception;

    public static ResourceType valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
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

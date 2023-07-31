package com.facilio.bmsconsoleV3.context.asset;

import com.facilio.assetcatergoryfeature.AssetCategoryFeatureActivationContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class V3AssetCategoryContext extends V3Context {

    private static final long serialVersionUID = 1L;

    private String name;
    private AssetCategoryType type;
    private Long parentCategoryId;
    private Long assetModuleID;
    private String moduleName;
    private String displayName;
    private Boolean isDefault;
    AssetCategoryFeatureActivationContext assetCategoryFeatureContext;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        if (type != null) {
            return type.getIntVal();
        }
        return -1;
    }
    public String getTypeVal() {
        if(type != null) {
            return type.getStringVal();
        }
        return null;
    }

    public void setType(AssetCategoryType type) {
        this.type = type;
    }

    public void setType(int type) {
        this.type = AssetCategoryType.STATE_MAP.get(type);
    }

    public AssetCategoryType getTypeEnum() {
        return type;
    }


    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public Long getAssetModuleID() {
        return assetModuleID;
    }

    public void setAssetModuleID(Long assetModuleID) {
        this.assetModuleID = assetModuleID;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getDisplayName() {
        if (displayName != null && !displayName.isEmpty()) {
            return displayName;
        } else {
            return name;
        }
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }


    public enum AssetCategoryType {
        MISC(0, "Misc"),
        HVAC(1, "HVAC"),
        ENERGY(2, "Energy"),
        FIRE(3, "Fire"),
        CONTROLLER(4,"Controller"),
        DEVICE(5,"Device")
        ;

        private int intVal;
        private String strVal;

         AssetCategoryType(int intVal, String strVal) {
            this.intVal = intVal;
            this.strVal = strVal;
        }

        public Integer getIntVal() {
            return intVal;
        }
        public String getStringVal() {
            return strVal;
        }


        public static AssetCategoryType getType(Integer val) {
            return STATE_MAP.get(val);
        }

        private static final Map<Integer, AssetCategoryType> STATE_MAP = Collections.unmodifiableMap(initTypeMap());
        private static Map<Integer, AssetCategoryType> initTypeMap() {
            Map<Integer, AssetCategoryType> typeMap = new HashMap<>();

            for(AssetCategoryType type : values()) {
                typeMap.put(type.getIntVal(), type);
            }
            return typeMap;
        }
        public Map<Integer, AssetCategoryType> getAllTypes() {
            return STATE_MAP;
        }
    }
}

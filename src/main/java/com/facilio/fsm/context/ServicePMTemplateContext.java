package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3SpaceCategoryContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServicePMTemplateContext extends ServicePlannedMaintenanceContext{
    private String masterPMName;
    private V3AssetCategoryContext assetCategory;
    private V3SpaceCategoryContext spaceCategory;
    private MasterPMType masterPMType;
    private ServicePMTriggerContext servicePMTrigger;

    public Integer getMasterPMType() {
        if (masterPMType != null) {
            return masterPMType.getIndex();
        }
        return null;
    }
    public void setMasterPMType(Integer masterPMType) {
        if (masterPMType != null) {
            this.masterPMType = MasterPMType.valueOf(masterPMType);
        }
    }
    public MasterPMType getMasterPMTypeEnum() {
        return masterPMType;
    }

    public enum MasterPMType implements FacilioIntEnum {
        SITE("Site"),
        ASSET("Asset"),
        SPACE("Space"),
        BUILDING("Building"),
        FLOOR("Floor");

        private final String value;
        MasterPMType(String value) {
            this.value = value;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return value;
        }

        public static MasterPMType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
}

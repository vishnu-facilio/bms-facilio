package com.facilio.componentpackage.context;

import com.facilio.modules.FacilioIntEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PackageContext {

    private long id = -1;
    private long orgId = -1;
    private String displayName;
    private String uniqueName;
    private double version;
    private PackageType type;
    private long fileId;
    private long sysCreatedTime;
    private long sysModifiedTime;
    private long sysCreatedBy;
    private long sysModifiedBy;

    @AllArgsConstructor
    public enum PackageType implements FacilioIntEnum {
        SANDBOX,
        PRODUCTION_TO_SANDBOX;

        @Override
        public String getValue() {
            return name();
        }

        public static PackageType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    public void setType(PackageType type) {
        this.type = type;
    }
    public int getType() {
        if (type != null) {
            return type.getIndex();
        }
        return -1;
    }
    public PackageType getTypeEnum() {
        return type;
    }
    public void setType(int type) {
        this.type = PackageType.valueOf(type);
    }

}

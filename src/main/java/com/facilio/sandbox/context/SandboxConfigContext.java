package com.facilio.sandbox.context;

import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class SandboxConfigContext implements Serializable {

    private long id = -1;
    private long orgId = -1;
    private String name;
    private String description;
    private String subDomain;
    private String fullDomain;
    private Long sysCreatedTime;
    private Long sysCreatedBy;
    private Long sysModifiedTime;
    private Long sysModifiedBy;
    private Long sandboxOrgId;
    private SharingContext<SingleSharingContext> sandboxSharing;
    private SandboxType sandboxType;
    public SandboxType getSandboxTypeEnum() {
        return sandboxType;
    }
    public void setSandboxType(SandboxType sandboxType) {
        this.sandboxType = sandboxType;
    }
    public int getSandboxType() {
        if (sandboxType != null) {
            return sandboxType.getIndex();
        }
        return -1;
    }
    public void setSandboxType(int sandboxType) {
        this.sandboxType = SandboxType.valueOf(sandboxType);
    }
    public static enum SandboxType implements FacilioIntEnum {
        CUSTOMIZATION,
        CUSTOMIZATION_AND_DATA;

        @Override
        public String getValue() {
            return name();
        }

        public static SandboxType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
    public static enum SandboxStatus implements FacilioIntEnum {
        ACTIVE,
        INACTIVE,
        SANDBOX_ORG_CREATION_IN_PROGRESS,
        META_UPGRADE_IN_PROGRESS,
        SANDBOX_ORG_CREATION_FAILED,
        META_PACKAGE_FAILED,
        META_INSTALL_FAILED;

        @Override
        public String getValue() {
            return name();
        }

        public static SandboxStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    private SandboxStatus status;
    public SandboxStatus getStatusEnum() {
        return status;
    }
    public void setStatus(SandboxStatus status) {
        this.status = status;
    }
    public int getStatus() {
        if (status != null) {
            return status.getIndex();
        }
        return -1;
    }
    public void setStatus(int status) {
        this.status = SandboxStatus.valueOf(status);
    }

}

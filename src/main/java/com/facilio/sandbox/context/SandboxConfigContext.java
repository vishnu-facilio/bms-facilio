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

    public static enum SandboxStatus implements FacilioIntEnum {
        ACTIVE,
        INACTIVE,
        CREATION_IN_PROGRESS,
        UPGRADE_IN_PROGRESS,
        FAILED;

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

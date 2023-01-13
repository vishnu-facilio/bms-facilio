package com.facilio.connectedapp.context;

import com.facilio.accounts.dto.IAMUser;
import com.facilio.connectedapp.util.ConnectedAppHostingAPI;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ConnectedAppDeploymentContext implements Serializable {
    private long id;
    private long orgId;
    private long connectedAppId;
    private int versionNumber;
    private String uniqueId;
    private String comments;
    private boolean isPublished;
    private long sysCreatedTime;
    private long sysCreatedBy;
    private IAMUser sysCreatedByUser;
    private long sysModifiedTime;
    private long sysModifiedBy;
    private IAMUser sysModifiedByUser;

    private ConnectedAppDeploymentContext.DeployStatus deployStatus;
    public ConnectedAppDeploymentContext.DeployStatus getDeployStatusEnum() {
        return deployStatus;
    }
    public void setDeployStatus(ConnectedAppDeploymentContext.DeployStatus deployStatus) {
        this.deployStatus = deployStatus;
    }
    public int getDeployStatus() {
        if (deployStatus != null) {
            return deployStatus.getValue();
        }
        return -1;
    }
    public void setDeployStatus(int deployStatus) {
        this.deployStatus = ConnectedAppDeploymentContext.DeployStatus.valueOf(deployStatus);
    }

    public enum DeployStatus {
        STARTED, PROGRESS, DEPLOYED, FAILED;

        public int getValue() {
            return ordinal() + 1;
        }

        public static ConnectedAppDeploymentContext.DeployStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    public String getUrl() {
        if (getUniqueId() != null) {
            return "https://" + getUniqueId() + "." + ConnectedAppHostingAPI.getConnectedAppHostingDomain();
        }
        return null;
    }
}

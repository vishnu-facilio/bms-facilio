package com.facilio.componentpackage.context;

import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.modules.FacilioIntEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PackageChangeSetMappingContext {
    private long id = -1;
    private long orgId = -1;
    private long packageId;
    private ComponentType componentType;
    private long componentId;
    private long parentComponentId;
    private Double createdVersion;
    private Double modifiedVersion;
    private ComponentStatus status;
    private String uniqueIdentifier;
    private String componentDisplayName;
    private long componentLastEditedTime;

    @AllArgsConstructor
    public enum ComponentStatus implements FacilioIntEnum {
        ADDED,
        UPDATED,
        DELETED;

        @Override
        public String getValue() {
            return name();
        }

        public static ComponentStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    public void setStatus(ComponentStatus status) {
        this.status = status;
    }
    public int getStatus() {
        if (status != null) {
            return status.getIndex();
        }
        return -1;
    }

    public ComponentStatus getStatusEnum() {
        return status;
    }
    public void setStatus(int status) {
        this.status = ComponentStatus.valueOf(status);
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public int getComponentType() {
        if (componentType != null) {
            return componentType.getIndex();
        }
        return -1;
    }

    public ComponentType getComponentTypeEnum() {
        return componentType;
    }

    public void setComponentType(int componentType) {
        this.componentType = ComponentType.valueOf(componentType);
    }

}

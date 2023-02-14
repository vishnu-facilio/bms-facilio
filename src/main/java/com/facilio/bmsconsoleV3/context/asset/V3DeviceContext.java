package com.facilio.bmsconsoleV3.context.asset;

import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class V3DeviceContext extends V3AssetContext {

    private static final long serialVersionUID = 1L;

    private String deviceInfo;
    private long deviceType;
    private long site;
    private DeviceType deviceTypeEnum;
    private KioskType kioskType;
    private V3ResourceContext associatedResource;
    private Boolean isDeviceConnected;
    private Boolean onlineStatus;
    private Long lastSeenOnlineTime;
    private Long lastActivityTime;
    private Long connectedTime;


    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public KioskType getKioskType() {
        return kioskType;
    }

    public void setKioskType(KioskType kioskType) {
        this.kioskType = kioskType;
    }

    public V3ResourceContext getAssociatedResource() {
        return associatedResource;
    }

    public void setAssociatedResource(V3ResourceContext associatedResource) {
        this.associatedResource = associatedResource;
    }

    public Boolean getDeviceConnected() {
        return isDeviceConnected;
    }

    public void setDeviceConnected(Boolean deviceConnected) {
        isDeviceConnected = deviceConnected;
    }

    public Boolean getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public Long getLastSeenOnlineTime() {
        return lastSeenOnlineTime;
    }

    public void setLastSeenOnlineTime(Long lastSeenOnlineTime) {
        this.lastSeenOnlineTime = lastSeenOnlineTime;
    }

    public Long getLastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(Long lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public Long getConnectedTime() {
        return connectedTime;
    }

    public void setConnectedTime(Long connectedTime) {
        this.connectedTime = connectedTime;
    }


    public enum KioskType implements FacilioIntEnum {
        VISITOR("Visitor"),
        SERVICE_KIOSK("Service")
        ;

        private String name;
        KioskType (String name) {
            this.name = name;

        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static KioskType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    public enum DeviceType implements FacilioIntEnum {
        TV("Tv"),
        VISITOR_KIOSK("Visitor Kiosk"),
        DIGITAL_LOGBOOK("Digital Log Book"),
        FEEDBACK_KIOSK("Feedback Kiosk"),
        SMART_CONTROL_KIOSK("Smart Control Kiosk"),
        CUSTOM_KIOSK("Custom Kiosk")
        ;

        private String name;
        DeviceType (String name) {
            this.name = name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static DeviceType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }


}

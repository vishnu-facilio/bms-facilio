package com.facilio.bmsconsole.context;

import com.facilio.modules.FacilioIntEnum;

public class DeviceContext extends AssetContext {
	private static final long serialVersionUID = 1L;
	
//	private SiteContext site;
//	public SiteContext getSite() {
//		return site;
//	}
//	public void setSite(SiteContext site) {
//		this.site = site;
////		this.siteId = site != null ? site.getId() : -1;
//	}
	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public DeviceType getDeviceTypeEnum() {
		return deviceType;
	}
	
	public int getDeviceType() {
		if(deviceType!=null)
		{
			return deviceType.getIndex();
		}
		return -1;
	}
	

	public void setDeviceType(int deviceType) {
		this.deviceType = DeviceType.valueOf(deviceType);
	}
	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public int getKioskType() {
		if(kioskType!=null)
		{
			return kioskType.getIndex();	
		}
		return -1;
		
	}
	

	public KioskType getKioskTypeEnum() {
		return kioskType;
	}

	public void setKioskType(KioskType kioskType) {
		this.kioskType = kioskType;
	}

	public void setKioskType(int kioskType) {
		this.kioskType = KioskType.valueOf(kioskType);
	}
	
	public ResourceContext getAssociatedResource() {
		return associatedResource;
	}

	public void setAssociatedResource(ResourceContext associatedResource) {
		this.associatedResource = associatedResource;
	}



	public long getConnectedTime() {
		return connectedTime;
	}

	public void setConnectedTime(long connectedTime) {
		this.connectedTime = connectedTime;
	}

	private String deviceInfo;
	private DeviceType deviceType;
	private KioskType kioskType;
	private ResourceContext associatedResource;
	Boolean isDeviceConnected;
	private Boolean onlineStatus;
	private long lastSeenOnlineTime;
	private long lastActivityTime;

	public long getLastActivityTime() {
		return lastActivityTime;
	}

	public void setLastActivityTime(long lastActivityTime) {
		this.lastActivityTime = lastActivityTime;
	}

	public long getLastSeenOnlineTime() {
		return lastSeenOnlineTime;
	}

	public void setLastSeenOnlineTime(long lastSeenOnlineTime) {
		this.lastSeenOnlineTime = lastSeenOnlineTime;
	}

	public Boolean getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(Boolean onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	public Boolean getIsDeviceConnected() {
		return isDeviceConnected;
	}

	public void setIsDeviceConnected(Boolean isDeviceConnected) {
		this.isDeviceConnected = isDeviceConnected;
	}

	private long connectedTime;
    public static enum DeviceType implements FacilioIntEnum {
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
    
    public static enum KioskType implements FacilioIntEnum {
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
	
}

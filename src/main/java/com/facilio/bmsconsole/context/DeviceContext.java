package com.facilio.bmsconsole.context;

import com.facilio.modules.FacilioEnum;

public class DeviceContext extends AssetContext {
	private static final long serialVersionUID = 1L;
	
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


	public Boolean getIsDeviceConnected() {
		return isDeviceConnected;
	}

	public void setIsDeviceConnected(Boolean isDeviceConnected) {
		this.isDeviceConnected = isDeviceConnected;
	}

	private long connectedTime;
    public static enum DeviceType implements FacilioEnum {
        TV("Tv"),
        VISITOR_KIOSK("Visitor Kiosk"),
        DIGITAL_LOGBOOK("Digital Log Book")
        ;

        private String name;
        DeviceType (String name) {
            this.name = name;
        }

        @Override
        public int getIndex() {
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
    
    public static enum KioskType implements FacilioEnum {
        VISITOR("Visitor"),
        SERVICE_KIOSK("Service")
        ;

        private String name;
        KioskType (String name) {
            this.name = name;
            
        }

        @Override
        public int getIndex() {
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

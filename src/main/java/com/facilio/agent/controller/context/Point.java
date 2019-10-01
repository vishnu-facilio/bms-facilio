package com.facilio.agent.controller.context;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FieldType;


public class Point implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private long id = -1;

	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	private String displayName;
	public String getDisplayName() {
		
		if(StringUtils.isBlank(displayName)) {
			return name;
		}
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	private String description;
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description=description;
	}
	
	

	private FieldType dataType;
	public FieldType getDataTypeEnum() {
		return dataType;
	}
	public void setDataType(FieldType dataType) {
		this.dataType = dataType;
	}
	public int getDataType() {
		if(dataType != null) {
			return dataType.getTypeAsInt();
		}
		return -1;
	}
	public void setDataType(int dataType) {
		this.dataType = FieldType.getCFType(dataType);
	}
	
	private ControllerType pointType;
	public ControllerType getControllerTypeEnum() {
		return pointType;
	}
	public int getPointType() {
		if (pointType != null) {
			return pointType.getKey();
		}
		return -1;
	}
	
	public void setPointType(ControllerType pointType) {
		this.pointType = pointType;
	}
	public void setPointType(int pointTypeVal) {
		this.pointType = ControllerType.valueOf(pointTypeVal);
	}
	
	private String deviceName;
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	private long controllerId = -1;
	public long getControllerId() {
		return controllerId;
	}
	public void setControllerId(long controllerId) {
		this.controllerId = controllerId;
	}
	
	private long assetCategoryId = -1;
	public long getAssetCategoryId() {
		return assetCategoryId;
	}
	public void setAssetCategoryId(long assetCategoryId) {
		this.assetCategoryId = assetCategoryId;
	}
	
	private long resourceId = -1;
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	
	private long fieldId = -1;
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	
	private Boolean writable;
	
	public Boolean getWritable() {
		return writable;
	}
	public void setWritable(boolean writable) {
		this.writable = writable;
	}
	
	private Boolean inUse;
	
	public Boolean getInUse() {
		return inUse;
	}
	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}
	
	private Boolean subscribed;

	
	public Boolean getSubscribed() {
		return subscribed;
	}
	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
	}
	
	
	private String thresholdJson;
	
	public String getThresholdJson() {
		return thresholdJson;
	}
	
	public void setThresholdJson(String thresholdJson) {
		
		this.thresholdJson=thresholdJson;
	}
	
	private long createdTime = -1;
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	private long mappedTime = -1;
	public long getMappedTime() {
		return mappedTime;
	}
	public void setMappedTime(long mappedTime) {
		this.mappedTime = mappedTime;
	}
	
	private int unit=-1;
	
	public int getUnit() {
		return unit;
	}
	
	public void setUnit(int unit) {
		this.unit=unit;
	}
	
	public static enum ConfigureStatus implements FacilioEnum {
        UNCONFIGURED ("Un Configured"),
        IN_PROGRESS ("In Progress"),
        CONFIGURED ("Configured"),
        ;

        private String name;
        ConfigureStatus (String name) {
            this.name = name;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        public String getName() {
            return name;
        }

        public static ConfigureStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

		@Override
		public String getValue() {
			return getName();
		}
    }
	
	public static enum SubscribeStatus implements FacilioEnum {
		UNSUBSCRIBED ("Un Subscribed"),
        IN_PROGRESS ("In Progress"),
        SUBSCRIBED ("Subscribed"),
        ;

        private String name;
        SubscribeStatus (String name) {
            this.name = name;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        public String getName() {
            return name;
        }

        public static SubscribeStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

		@Override
		public String getValue() {
			return getName();
		}
    }
	
}

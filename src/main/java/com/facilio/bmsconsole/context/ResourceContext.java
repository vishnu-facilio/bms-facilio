package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ResourceContext extends ModuleBaseWithCustomFields {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private long photoId = -1;

	public long getPhotoId() {
		return photoId;
	}

	public void setPhotoId(long photoId) {
		this.photoId = photoId;
	}

	private long operatingHour = -1;

	public long getOperatingHour() {
		return operatingHour;
	}

	public void setOperatingHour(long operatingHour) {
		this.operatingHour = operatingHour;
	}
	BusinessHoursContext businessHour;
	public BusinessHoursContext getBusinessHour() {
		return businessHour;
	}
	public void setBusinessHour(BusinessHoursContext businessHour) {
		this.businessHour = businessHour;
	}

	private long spaceId = -1;

	public long getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
		if (spaceId > -1 && space == null) {
			BaseSpaceContext space = new BaseSpaceContext();
			space.setId(spaceId);
			setSpace(space);
		}
	}

	private BaseSpaceContext space;

	public void setSpace(BaseSpaceContext space) {
		this.space = space;
		if (this.space != null && getSpaceId() == -1) {
			setSpaceId(this.space.getId());
		}
	}

	public BaseSpaceContext getSpace() {
		return this.space;
	}
	/*
	 * public BaseSpaceContext getSpace() { if ((space == null || space.getId()
	 * == -1) && getSpaceId()!= -1) { space = new BaseSpaceContext();
	 * space.setId(getSpaceId()); } return space; }
	 */

	private ResourceType resourceType;

	public ResourceType getResourceTypeEnum() {
		return resourceType;
	}

	public int getResourceType() {
		if (resourceType != null) {
			return resourceType.getValue();
		}
		return -1;
	}

	public void setResourceType(int resourceType) {
		this.resourceType = ResourceType.valueOf(resourceType);
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	private String avatarUrl;

	@JsonIgnore
	public String getAvatarUrl() throws Exception {
		if (avatarUrl == null && this.photoId > 0) {
			FileStore fs = FacilioFactory.getFileStore();
			avatarUrl = fs.getPrivateUrl(this.photoId);
		}
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	private long controllerId = -1;

	public long getControllerId() {
		return controllerId;
	}

	public void setControllerId(long controllerId) {
		this.controllerId = controllerId;
	}

	private long siteId = -1;

	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}

	public long getSiteId() {
		return this.siteId;
	}

	private String qrVal;

	public String getQrVal() {
		return qrVal;
	}

	public void setQrVal(String qrVal) {
		this.qrVal = qrVal;
	}
	

	public static enum ResourceType {
		SPACE, ASSET, PM, USER;

		public int getValue() {
			return ordinal() + 1;
		}

		public static ResourceType valueOf(int val) {
			if (val > 0 && val <= values().length) {
				return values()[val - 1];
			}
			return null;
		}
	}

	private SourceType sourceType;

	public SourceType getSourceTypeEnum() {
		return sourceType;
	}

	public int getSourceType() {
		if (sourceType != null) {
			return sourceType.getIndex();
		}
		return -1;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = SourceType.valueOf(sourceType);
	}

	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
	}

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}

	private long sourceId = -1;

}

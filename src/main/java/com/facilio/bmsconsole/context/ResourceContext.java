package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ResourceContext extends ModuleBaseWithCustomFields {
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
	
	private long spaceId = -1;
	public long getSpaceId() {
		return spaceId;
	}
	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
	}
	
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
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
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

	public static enum ResourceType {
		SPACE,
		ASSET,
		PM,
		USER
		;
		
		public int getValue() {
			return ordinal()+1;
		}
		
		public static ResourceType valueOf(int val) {
			if (val > 0 && val <= values().length) {
				return values()[val - 1];
			}
			return null;
		}
	}
}

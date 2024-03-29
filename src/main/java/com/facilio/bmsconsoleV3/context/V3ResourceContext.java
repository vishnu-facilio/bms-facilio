package com.facilio.bmsconsoleV3.context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Setter@Getter
public class V3ResourceContext extends V3Context {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;

	private Long photoId;
	private Long operatingHour;
	BusinessHoursContext businessHour;

	private long spaceId = -1;

	public long getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
		if (spaceId > -1 && space == null) {
			V3BaseSpaceContext space = new V3BaseSpaceContext();
			space.setId(spaceId);
			setSpace(space);
		}
	}

	private V3BaseSpaceContext space;

	public void setSpace(V3BaseSpaceContext space) {
		this.space = space;
		if (this.space != null && getSpaceId() == -1) {
			setSpaceId(this.space.getId());
		}
	}

	public V3BaseSpaceContext getSpace() {
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

	public String getAvatarUrl() throws Exception {
		if (avatarUrl == null && (this.photoId != null && this.photoId > 0)) {
			FileStore fs = FacilioFactory.getFileStore();
			avatarUrl = fs.getPrivateUrl(this.photoId);
		}
		if(AccountUtil.getCurrentOrg() != null && V3PermissionUtil.isAllowedEnvironment()) {
			FileStore fs = FacilioFactory.getFileStore();
			if(this.photoId != null) {
				avatarUrl = fs.getPrivateUrl(-1, -1, this.photoId, false);
			}
		}
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	private Long controllerId;
	private String qrVal;

	public static enum ResourceType {
		SPACE (FacilioConstants.ContextNames.BASE_SPACE),
		ASSET (FacilioConstants.ContextNames.ASSET),
		PM (FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE),
		USER (FacilioConstants.ContextNames.USERS),
		CONTROLLER (FacilioConstants.ContextNames.CONTROLLER_MODULE_NAME),
		METER (FacilioConstants.Meter.METER);

		private String subModuleName;
		private ResourceType (String module) {
			this.subModuleName = module;
		}

		public String getSubModuleName() {
			return subModuleName;
		}

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

	private Long sourceId;

	public Boolean getDecommission() {
		return decommission != null && decommission.booleanValue();
	}
	private Boolean decommission;
	private  long commissionedTime;
	private User decommissionedBy;
	public Boolean getViewRecommissionedBtn() {
		return viewRecommissionedBtn != null && viewRecommissionedBtn.booleanValue();
	}
    private Boolean viewRecommissionedBtn;

}

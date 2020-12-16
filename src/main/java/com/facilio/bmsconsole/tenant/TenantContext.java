package com.facilio.bmsconsole.tenant;

import com.facilio.bmsconsole.context.*;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TenantContext extends ModuleBaseWithCustomFields {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private long orgId = -1;

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	private long inTime;

	public long getInTime() {
		return inTime;
	}

	public void setInTime(long inTime) {
		this.inTime = inTime;
	}

	private long outTime;

	public long getOutTime() {
		return outTime;
	}

	public void setOutTime(long outTime) {
		this.outTime = outTime;
	}

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

	private long logoId = -1;

	public long getLogoId() {
		return logoId;
	}

	public void setLogoId(long logoId) {
		this.logoId = logoId;
	}

	private String logoUrl;

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	private ZoneContext zone = null;

	public ZoneContext getZone() {
		return zone;
	}

	public void setZone(ZoneContext zone) {
		this.zone = zone;
	}

	private BaseSpaceContext space;

	public BaseSpaceContext getSpace() {
		return space;
	}

	public void setSpace(BaseSpaceContext space) {
		this.space = space;
	}

	private List<UtilityAsset> utilityAssets;

	public List<UtilityAsset> getUtilityAssets() {
		return utilityAssets;
	}

	public void setUtilityAssets(List<UtilityAsset> utilityAssets) {
		this.utilityAssets = utilityAssets;
	}

	public List<UtilityAsset> getUtilityAssetsOfUtility(int utilityId) {

		List<UtilityAsset> utilityAssets1 = null;

		if (utilityAssets != null && !utilityAssets.isEmpty()) {

			for (UtilityAsset utilityAsset : utilityAssets) {

				if (utilityId == utilityAsset.getUtility()) {
					if (utilityAssets1 == null) {
						utilityAssets1 = new ArrayList<>();
					}
					utilityAssets1.add(utilityAsset);
				}
			}
		}
		return utilityAssets1;
	}

	private File tenantLogo;

	public File getTenantLogo() {
		return tenantLogo;
	}

	public void setTenantLogo(File tenantLogo) {
		this.tenantLogo = tenantLogo;
	}

	private String tenantLogoFileName;

	public String getTenantLogoFileName() {
		return tenantLogoFileName;
	}

	public void setTenantLogoFileName(String tenantLogoFileName) {
		this.tenantLogoFileName = tenantLogoFileName;
	}

	private String tenantLogoContentType;

	public String getTenantLogoContentType() {
		return tenantLogoContentType;
	}

	public void setTenantLogoContentType(String tenantLogoContentType) {
		this.tenantLogoContentType = tenantLogoContentType;
	}

	private long siteId = -1;

	public long getSiteId() {
		return this.siteId;
	}

	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}

	private Status status;

	public Status getStatusEnum() {
		return status;
	}

	public int getStatus() {
		if (status != null) {
			return status.getValue();
		}
		return -1;
	}

	public void setStatus(int status) {
		this.status = Status.valueOf(status);
	}

//	public void setStatus(Status status) {
//		this.status = status;
//	}


	public static enum Status {
		ACTIVE(),
		EXPIRED();

		public int getValue() {
			return ordinal() + 1;
		}

		public static Status valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}

	private List<ContactsContext> tenantContacts;

	public List<ContactsContext> getTenantContacts() {
		return tenantContacts;
	}

	public void setTenantContacts(List<ContactsContext> tenantContacts) {
		this.tenantContacts = tenantContacts;
	}

	private TenantType tenantType;

	public int getTenantType() {
		if (tenantType != null) {
			return tenantType.getIndex();
		}
		return -1;
	}

	public void setTenantType(int tenantType) {
		this.tenantType = TenantType.valueOf(tenantType);
	}

	public TenantType getTenantTypeEnum() {
		return tenantType;
	}
//	public void setTenantType(TenantType tenantType) {
//		this.tenantType = tenantType;
//	}

	public static enum TenantType implements FacilioEnum {
		COMMERCIAL, RESIDENTIAL;

		@Override
		public int getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name();
		}

		public static TenantType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}

	private String primaryContactName;

	public String getPrimaryContactName() {
		return primaryContactName;
	}

	public void setPrimaryContactName(String primaryContactName) {
		this.primaryContactName = primaryContactName;
	}

	private String primaryContactEmail;
	private String primaryContactPhone;

	public String getPrimaryContactEmail() {
		return primaryContactEmail;
	}

	public void setPrimaryContactEmail(String primaryContactEmail) {
		this.primaryContactEmail = primaryContactEmail;
	}

	public String getPrimaryContactPhone() {
		return primaryContactPhone;
	}

	public void setPrimaryContactPhone(String primaryContactPhone) {
		this.primaryContactPhone = primaryContactPhone;
	}

	private List<BaseSpaceContext> spaces;

	public List<BaseSpaceContext> getSpaces() {
		return spaces;
	}

	public void setSpaces(List<BaseSpaceContext> spaces) {
		this.spaces = spaces;
	}

	private List<OccupantsContext> occupantList;

	public List<OccupantsContext> getOccupantList() {
		return occupantList;
	}

	public void setOccupantList(List<OccupantsContext> occupantList) {
		this.occupantList = occupantList;
	}

	private File avatar;

	public File getAvatar() {
		return avatar;
	}

	public void setAvatar(File avatar) {
		this.avatar = avatar;
	}

	private String avatarFileName;

	public String getAvatarFileName() {
		return avatarFileName;
	}

	public void setAvatarFileName(String avatarFileName) {
		this.avatarFileName = avatarFileName;
	}

	private long avatarId;

	public long getAvatarId() {
		return avatarId;
	}

	public void setAvatarId(long avatarId) {
		this.avatarId = avatarId;
	}

	private String avatarUrl;

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	private String avatarContentType;

	public String getAvatarContentType() {
		return avatarContentType;
	}

	public void setAvatarContentType(String avatarContentType) {
		this.avatarContentType = avatarContentType;
	}


	private List<TenantContactContext> peopleTenantContacts;

	public List<TenantContactContext> getPeopleTenantContacts() {
		return peopleTenantContacts;
	}

	public void setPeopleTenantContacts(List<TenantContactContext> peopleTenantContacts) {
		this.peopleTenantContacts = peopleTenantContacts;
	}

	public LocationContext getAddress() {
		return address;
	}

	public void setAddress(LocationContext address) {
		this.address = address;
	}

	private LocationContext address;

	private BaseSpaceContext site;

	public BaseSpaceContext getSite() {
		return site;
	}

	public void setSite(BaseSpaceContext site) {
		this.site = site;
	}
}

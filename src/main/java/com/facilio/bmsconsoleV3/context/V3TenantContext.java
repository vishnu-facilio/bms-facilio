package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.tenant.UtilityAsset;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.V3Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class V3TenantContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private Long inTime;
    public Long getInTime() {
        return inTime;
    }
    public void setInTime(Long inTime) {
        this.inTime = inTime;
    }

    private Long outTime;
    public Long getOutTime() {
        return outTime;
    }
    public void setOutTime(Long outTime) {
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

    private Long logoId;
    public Long getLogoId() {
        return logoId;
    }
    public void setLogoId(Long logoId) {
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

        if(utilityAssets != null && !utilityAssets.isEmpty()) {

            for(UtilityAsset utilityAsset :utilityAssets) {

                if(utilityId == utilityAsset.getUtility()) {
                    if(utilityAssets1 == null) {
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

    private  String tenantLogoContentType;
    public String getTenantLogoContentType() {
        return tenantLogoContentType;
    }
    public void setTenantLogoContentType(String tenantLogoContentType) {
        this.tenantLogoContentType = tenantLogoContentType;
    }

    private TenantContext.Status status;
    public TenantContext.Status getStatusEnum() {
        return status;
    }
    public Integer getStatus() {
        if (status != null) {
            return status.getValue();
        }
        return null;
    }

    public void setStatus(int status) {
        this.status = TenantContext.Status.valueOf(status);
    }

//	public void setStatus(Status status) {
//		this.status = status;
//	}


    public static enum Status {
        ACTIVE(),
        EXPIRED();

        public int getValue() {
            return ordinal()+1;
        }

        public static V3TenantContext.Status valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    private List<V3ContactsContext> tenantContacts;
    public List<V3ContactsContext> getTenantContacts() {
        return tenantContacts;
    }
    public void setTenantContacts(List<V3ContactsContext> tenantContacts) {
        this.tenantContacts = tenantContacts;
    }

    private V3TenantContext.TenantType tenantType;
    public Integer getTenantType() {
        if (tenantType != null) {
            return tenantType.getIndex();
        }
        return null;
    }
    public void setTenantType(Integer tenantType) {

        if(tenantType != null) {
            this.tenantType = V3TenantContext.TenantType.valueOf(tenantType);
        }
    }
    public V3TenantContext.TenantType getTenantTypeEnum() {
        return tenantType;
    }
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

        public static V3TenantContext.TenantType valueOf(int value) {
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

    private Long avatarId;
    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    private String avatarUrl;
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    private  String avatarContentType;
    public String getAvatarContentType() {
        return avatarContentType;
    }

    public void setAvatarContentType(String avatarContentType) {
        this.avatarContentType = avatarContentType;
    }


    private List<V3TenantContactContext> peopleTenantContacts;
    public List<V3TenantContactContext> getPeopleTenantContacts() {
        return peopleTenantContacts;
    }
    public void setPeopleTenantContacts(List<V3TenantContactContext> peopleTenantContacts) {
        this.peopleTenantContacts = peopleTenantContacts;
    }

    public LocationContext getAddress() {
        return address;
    }

    public void setAddress(LocationContext address) {
        this.address = address;
    }

    private LocationContext address;


}

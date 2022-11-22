package com.facilio.accounts.dto;

import java.io.Serializable;
import com.facilio.modules.FacilioIntEnum;

public class AppDomainLink implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private long appDomainId;
    private String name;
    private LinkType linkType;
    private Boolean isExternalURL;
    private String content;
    private String externalURL;
    private long createdTime;
    private long createdBy;
    private long modifiedTime;
    private long modifiedBy;
    private Boolean showInMenu = false;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAppDomainId() {
        return appDomainId;
    }

    public void setAppDomainId(long appDomainId) {
        this.appDomainId = appDomainId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExternalURL() {
        return externalURL;
    }

    public void setExternalURL(String externalURL) {
        this.externalURL = externalURL;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Boolean getIsExternalURL() {
        return isExternalURL;
    }

    public void setIsExternalURL(Boolean isExternalURL) {
        this.isExternalURL = isExternalURL;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLinkType() {
        if (linkType != null) {
            return linkType.getIndex();
        }
        return -1;
    }

    public void setLinkType(int linkType) {
        this.linkType = LinkType.valueOf(linkType);
    }
    public void setLinkType(LinkType linkType) {
        this.linkType = linkType;
    }
    public LinkType getLinkTypeEnum() {
        return linkType;
    }

    public Boolean getShowInMenu() {
        return showInMenu;
    }

    public void setShowInMenu(Boolean showInMenu) {
        this.showInMenu = showInMenu;
    }

    public static enum LinkType implements FacilioIntEnum {
        PRIVACY_POLICY,
        TERMS_OF_USE,
        IOS_APP,
        ANDROID_APP,
        HELP,
        FAQ,
        OTHER;

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name();
        }

        public static LinkType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
}
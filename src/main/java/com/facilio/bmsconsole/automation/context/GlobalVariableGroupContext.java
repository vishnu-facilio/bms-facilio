package com.facilio.bmsconsole.automation.context;

import com.facilio.accounts.dto.User;

import java.io.Serializable;

public class GlobalVariableGroupContext implements Serializable {
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

    private String linkName;
    public String getLinkName() {
        return linkName;
    }
    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    private String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    private long createdBy = -1;
    public long getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    private User createdByUser;
    public User getCreatedByUser() {
        return createdByUser;
    }
    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }

    private long modifiedBy = -1;
    public long getModifiedBy() {
        return modifiedBy;
    }
    public void setModifiedBy(long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    private User modifiedByUser;
    public User getModifiedByUser() {
        return modifiedByUser;
    }
    public void setModifiedByUser(User modifiedByUser) {
        this.modifiedByUser = modifiedByUser;
    }

    private long createdTime = -1l;
    public long getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    private long modifiedTime = -1l;
    public long getModifiedTime() {
        return modifiedTime;
    }
    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}

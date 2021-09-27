package com.facilio.delegate.context;

import com.facilio.accounts.dto.User;

import java.io.Serializable;

public class DelegationContext implements Serializable {

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

    private String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    private long appId = -1;
    public long getAppId() {
        return appId;
    }
    public void setAppId(long appId) {
        this.appId = appId;
    }

    private long fromTime = -1;
    public long getFromTime() {
        return fromTime;
    }
    public void setFromTime(long fromTime) {
        this.fromTime = fromTime;
    }

    private long toTime = -1;
    public long getToTime() {
        return toTime;
    }
    public void setToTime(long toTime) {
        this.toTime = toTime;
    }

    private long userId = -1;
    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }

    private User user;
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    private long delegateUserId = -1;
    public long getDelegateUserId() {
        return delegateUserId;
    }
    public void setDelegateUserId(long delegateUserId) {
        this.delegateUserId = delegateUserId;
    }

    private User delegateUser;
    public User getDelegateUser() {
        return delegateUser;
    }
    public void setDelegateUser(User delegateUser) {
        this.delegateUser = delegateUser;
    }
}

package com.facilio.accounts.dto;

import java.io.Serializable;

public class RoleApp implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private long id;
    private long applicationId;
    private long roleId;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getApplicationId() {
        return applicationId;
    }
    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

}

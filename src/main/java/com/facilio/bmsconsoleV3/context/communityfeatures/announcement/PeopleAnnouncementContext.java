package com.facilio.bmsconsoleV3.context.communityfeatures.announcement;

import com.facilio.accounts.dto.IAMUser;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;

public class PeopleAnnouncementContext extends AnnouncementContext {

    private V3PeopleContext people;
    private Boolean isRead;
    private Long parentId;

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Boolean isRead() {
        if (isRead != null) {
            return isRead.booleanValue();
        }
        return false;
    }

    public V3PeopleContext getPeople() {
        return people;
    }

    public void setPeople(V3PeopleContext people) {
        this.people = people;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    private IAMUser createdBy;
    public IAMUser getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(IAMUser createdBy) {
        this.createdBy = createdBy;
    }

    private Long createdTime;
    public Long getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

}

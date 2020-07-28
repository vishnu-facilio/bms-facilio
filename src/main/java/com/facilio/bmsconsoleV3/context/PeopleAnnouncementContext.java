package com.facilio.bmsconsoleV3.context;

public class PeopleAnnouncementContext extends AnnouncementContext {

    private V3PeopleContext people;
    private Boolean isRead;
    private Long parentId;
    private Boolean isCancelled;

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

    public Boolean getCancelled() {
        return isCancelled;
    }

    public void setCancelled(Boolean cancelled) {
        isCancelled = cancelled;
    }

    public Boolean isCancelled() {
        if (isCancelled != null) {
            return isCancelled.booleanValue();
        }
        return false;
    }
}

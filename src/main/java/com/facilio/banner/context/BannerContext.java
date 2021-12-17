package com.facilio.banner.context;

import com.facilio.modules.FacilioIntEnum;

import java.io.Serializable;

public class BannerContext implements Serializable {

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

    private long userId = -1;
    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }

    private String subject;
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    private long startDate = -1;
    public long getStartDate() {
        return startDate;
    }
    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    private long endDate = -1;
    public long getEndDate() {
        return endDate;
    }
    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    private int icon = -1;
    public int getIcon() {
        return icon;
    }
    public void setIcon(int icon) {
        this.icon = icon;
    }

    private Type type;
    public Type getTypeEnum() {
        return type;
    }
    public int getType() {
        if (type != null) {
            return type.getIndex();
        }
        return -1;
    }
    public void setType(Type type) {
        this.type = type;
    }
    public void setType(int type) {
        this.type = Type.valueOf(type);
    }

    private boolean cancelled;
    public boolean isCancelled() {
        return cancelled;
    }
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String uniqueId;
    public String getUniqueId() {
        return uniqueId;
    }
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Priority priority;
    public int getPriority() {
        if (priority != null) {
            return priority.getIndex();
        }
        return -1;
    }
    public Priority getPriorityEnum() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = Priority.valueOf(priority);
    }
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    private String linkConfig;
    public String getLinkConfig() {
        return linkConfig;
    }
    public void setLinkConfig(String linkConfig) {
        this.linkConfig = linkConfig;
    }

    public enum Type implements FacilioIntEnum {
        CANCEL("Cancel"),
        COLLAPSE("Collapse");

        private String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String getValue() {
            return name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        public static Type valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }
    }

    public enum Priority implements FacilioIntEnum {
        HIGH("High"),
        MEDIUM("Medium"),
        LOW("Low");

        private String name;

        Priority(String name) {
            this.name = name;
        }

        @Override
        public String getValue() {
            return FacilioIntEnum.super.getValue();
        }

        public static Priority valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }
    }
}

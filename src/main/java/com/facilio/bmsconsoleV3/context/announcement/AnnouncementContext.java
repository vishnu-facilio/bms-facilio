package com.facilio.bmsconsoleV3.context.announcement;

import com.facilio.v3.context.V3Context;

import java.util.List;

public class AnnouncementContext extends V3Context {

    private String title;
    private String description;
    private String longDescription;
    private Long expiryDate;
    private Integer category;
    private Boolean isPublished;
    private Boolean isCancelled;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    private List<AnnouncementSharingInfoContext> announcementsharing;

    public List<AnnouncementSharingInfoContext> getAnnouncementsharing() {
        return announcementsharing;
    }

    public void setAnnouncementsharing(List<AnnouncementSharingInfoContext> announcementsharing) {
        this.announcementsharing = announcementsharing;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public Boolean isPublished() {
        if (isPublished != null) {
            return isPublished.booleanValue();
        }
        return false;
    }

    public Boolean getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(Boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public Boolean isCancelled() {
        if (isCancelled != null) {
            return isCancelled.booleanValue();
        }
        return false;
    }

    private Long readCount;

    public Long getReadCount() {
        return readCount;
    }

    public void setReadCount(Long readCount) {
        this.readCount = readCount;
    }
}

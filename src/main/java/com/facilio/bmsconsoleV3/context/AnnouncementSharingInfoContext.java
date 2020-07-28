package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;

import java.io.Serializable;

public class AnnouncementSharingInfoContext extends V3Context {

    private AnnouncementContext announcement;
    private SharingType sharingType;


    public enum SharingType implements FacilioEnum {
        BUILDING("Building");

        private String name;

        SharingType(String name) {
            this.name = name;
        }

        public static SharingType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    private Long sharedTo;

    public Long getSharedTo() {
        return sharedTo;
    }

    public void setSharedTo(Long sharedTo) {
        this.sharedTo = sharedTo;
    }

    public void setSharingType(Integer sharingType) {
        if (sharingType != null) {
            this.sharingType = SharingType.valueOf(sharingType);
        }
    }

    public SharingType getSharingTypeEnum() {
        return sharingType;
    }
    public Integer getSharingType() {
        if (sharingType != null) {
            return sharingType.getIndex();
        }
        return null;
    }

    public AnnouncementContext getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(AnnouncementContext announcement) {
        this.announcement = announcement;
    }
}

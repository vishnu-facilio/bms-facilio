package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.context.announcement.AnnouncementSharingInfoContext;
import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;

public class CommunitySharingInfoContext extends V3Context {

    private SharingType sharingType;


    public enum SharingType implements FacilioEnum {
        BUILDING("Building"),
        SITE("Site"),
        ALL_SITES("All Sites")
        ;

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

    private BaseSpaceContext sharedToSpace;

    public BaseSpaceContext getSharedToSpace() {
        return sharedToSpace;
    }

    public void setSharedToSpace(BaseSpaceContext sharedToSpace) {
        this.sharedToSpace = sharedToSpace;
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

}

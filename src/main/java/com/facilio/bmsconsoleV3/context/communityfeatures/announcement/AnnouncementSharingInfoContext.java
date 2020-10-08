package com.facilio.bmsconsoleV3.context.communityfeatures.announcement;

import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementContext;

public class AnnouncementSharingInfoContext extends CommunitySharingInfoContext {

    private AnnouncementContext announcement;
    public AnnouncementContext getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(AnnouncementContext announcement) {
        this.announcement = announcement;
    }
}

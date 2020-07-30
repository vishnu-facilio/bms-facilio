package com.facilio.bmsconsoleV3.context.announcement;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;
import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;

public class AnnouncementSharingInfoContext extends CommunitySharingInfoContext {

    private AnnouncementContext announcement;
    public AnnouncementContext getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(AnnouncementContext announcement) {
        this.announcement = announcement;
    }
}

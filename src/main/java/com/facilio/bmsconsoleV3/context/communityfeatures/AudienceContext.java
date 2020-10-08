package com.facilio.bmsconsoleV3.context.communityfeatures;

import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class AudienceContext extends V3Context {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private List<CommunitySharingInfoContext> audienceSharing;

    public List<CommunitySharingInfoContext> getAudienceSharing() {
        return audienceSharing;
    }

    public void setAudienceSharing(List<CommunitySharingInfoContext> audienceSharing) {
        this.audienceSharing = audienceSharing;
    }
}

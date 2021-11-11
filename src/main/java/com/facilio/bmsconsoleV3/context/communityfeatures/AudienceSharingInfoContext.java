package com.facilio.bmsconsoleV3.context.communityfeatures;

import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;

public class AudienceSharingInfoContext extends CommunitySharingInfoContext {

    private AudienceContext audienceId;

    public AudienceContext getAudienceId() {
        return audienceId;
    }

    public void setAudienceId(AudienceContext audienceId) {
        this.audienceId = audienceId;
    }
}

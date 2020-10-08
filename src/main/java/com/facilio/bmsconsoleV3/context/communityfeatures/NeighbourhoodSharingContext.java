package com.facilio.bmsconsoleV3.context.communityfeatures;

import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;

public class NeighbourhoodSharingContext extends CommunitySharingInfoContext {

    private NeighbourhoodContext neighbourhood;

    public NeighbourhoodContext getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(NeighbourhoodContext neighbourhood) {
        this.neighbourhood = neighbourhood;
    }
}

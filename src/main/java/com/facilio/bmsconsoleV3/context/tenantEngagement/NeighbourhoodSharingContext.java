package com.facilio.bmsconsoleV3.context.tenantEngagement;

import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;

public class NeighbourhoodSharingContext extends CommunitySharingInfoContext {

    public NeighbourhoodContext getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(NeighbourhoodContext neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    private NeighbourhoodContext neighbourhood;
}

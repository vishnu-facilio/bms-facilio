package com.facilio.bmsconsoleV3.context.communityfeatures;

import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;

public class DealsAndOffersSharingContext extends CommunitySharingInfoContext {

    private DealsAndOffersContext deals;

    public DealsAndOffersContext getDeals() {
        return deals;
    }

    public void setDeals(DealsAndOffersContext deals) {
        this.deals = deals;
    }
}

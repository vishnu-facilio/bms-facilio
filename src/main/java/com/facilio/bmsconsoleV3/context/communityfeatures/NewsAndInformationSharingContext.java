package com.facilio.bmsconsoleV3.context.communityfeatures;

import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;

public class NewsAndInformationSharingContext extends CommunitySharingInfoContext {

    private NewsAndInformationContext newsAndInformation;

    public NewsAndInformationContext getNewsAndInformation() {
        return newsAndInformation;
    }

    public void setNewsAndInformation(NewsAndInformationContext newsAndInformation) {
        this.newsAndInformation = newsAndInformation;
    }
}

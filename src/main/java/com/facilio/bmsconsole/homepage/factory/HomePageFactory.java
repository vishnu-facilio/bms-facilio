package com.facilio.bmsconsole.homepage.factory;

import com.facilio.bmsconsole.homepage.HomePage;
import com.facilio.constants.FacilioConstants;


public class HomePageFactory {

    public static HomePage getPage(String applicationLinkName) throws Exception {

        switch(applicationLinkName) {
            case FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP:
                return EmplpoyeePortalHome.getDefaultPage(applicationLinkName);
            default:
                return null;
        }
    }
}

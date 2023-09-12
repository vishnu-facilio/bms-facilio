package com.facilio.bmsconsole.homepage.factory;

import com.facilio.bmsconsole.homepage.HomePage;
import com.facilio.constants.FacilioConstants;


public class HomePageFactory {

    public static HomePage getPage(String applicationLinkName) throws Exception {

        switch(applicationLinkName) {
            case FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP:
            case FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP:
                return EmplpoyeePortalHome.getDefaultPage(applicationLinkName);
            case FacilioConstants.ApplicationLinkNames.FSM_APP:
                return FieldServiceHome.getDefaultPage();
            default:
                return null;
        }
    }
}

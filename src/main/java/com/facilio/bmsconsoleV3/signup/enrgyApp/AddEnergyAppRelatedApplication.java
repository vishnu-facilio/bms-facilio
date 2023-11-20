package com.facilio.bmsconsoleV3.signup.enrgyApp;

import com.facilio.bmsconsole.context.ApplicationRelatedAppsContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddEnergyAppRelatedApplication extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<String> energyAppRelatedApplications = Arrays.asList(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);

        long energyAppId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        List<ApplicationRelatedAppsContext> relatedApps = new ArrayList<>();
        for(String appLinkName : energyAppRelatedApplications) {
            long appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);
            ApplicationRelatedAppsContext relatedAppsContext = new ApplicationRelatedAppsContext();
            relatedAppsContext.setApplicationId(energyAppId);
            relatedAppsContext.setRelatedApplicationId(appId);
            relatedApps.add(relatedAppsContext);
        }
        ApplicationApi.addRelatedApplications(relatedApps);
        return false;
    }
}

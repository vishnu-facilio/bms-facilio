package com.facilio.bmsconsoleV3.signup.fsmApp;

import com.facilio.bmsconsole.context.ApplicationRelatedAppsContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddFsmAppRelatedApplicationsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String appLinkNames[] = new String[]{FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP,FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP};
        List<String> fsmAppRelatedApplications = Arrays.asList(appLinkNames);
        long fsmAppId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FSM_APP);
        List<ApplicationRelatedAppsContext> relatedApps = new ArrayList<>();
        for(String appLinkName : fsmAppRelatedApplications) {
            long appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);
            ApplicationRelatedAppsContext relatedAppsContext = new ApplicationRelatedAppsContext();
            relatedAppsContext.setApplicationId(fsmAppId);
            relatedAppsContext.setRelatedApplicationId(appId);
            relatedApps.add(relatedAppsContext);
        }
        ApplicationApi.addRelatedApplications(relatedApps);
            return false;
    }
}

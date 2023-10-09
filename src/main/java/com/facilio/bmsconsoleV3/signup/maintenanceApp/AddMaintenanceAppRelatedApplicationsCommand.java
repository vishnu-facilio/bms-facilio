package com.facilio.bmsconsoleV3.signup.maintenanceApp;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ApplicationRelatedAppsContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddMaintenanceAppRelatedApplicationsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<String> maintenanceAppRelatedApplications = Arrays.asList(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP,FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.DATA_LOADER_APP,FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP,FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);

        long maintenanceAppId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        List<ApplicationRelatedAppsContext> relatedApps = new ArrayList<>();
        for(String appLinkName : maintenanceAppRelatedApplications) {
            long appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);
            ApplicationRelatedAppsContext relatedAppsContext = new ApplicationRelatedAppsContext();
            relatedAppsContext.setApplicationId(maintenanceAppId);
            relatedAppsContext.setRelatedApplicationId(appId);
            relatedApps.add(relatedAppsContext);
        }
        ApplicationApi.addRelatedApplications(relatedApps);
        return false;
    }
}

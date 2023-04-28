package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.context.ApplicationRelatedAppsContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddRelatedApplicationsForMigratingAppCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long mainAppId = (long) context.get(FacilioConstants.ContextNames.APP_ID);
        List<String> migratedAppRelatedApplications = Arrays.asList(
                FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP,
                FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP,
                FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP,
                FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP,
                FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP,
                FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,
                FacilioConstants.ApplicationLinkNames.IWMS_APP,
                FacilioConstants.ApplicationLinkNames.DATA_LOADER_APP);
        List<ApplicationRelatedAppsContext> relatedApps = new ArrayList<>();
        for(String appLinkName : migratedAppRelatedApplications) {
            long appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);
            ApplicationRelatedAppsContext relatedAppsContext = new ApplicationRelatedAppsContext();
            relatedAppsContext.setApplicationId(mainAppId);
            relatedAppsContext.setRelatedApplicationId(appId);
            relatedApps.add(relatedAppsContext);
        }
        ApplicationApi.addRelatedApplications(relatedApps);
        return false;
    }
}

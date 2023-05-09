package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.context.ApplicationRelatedAppsContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RelateMainAppWithMaintenanceCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long mainAppId = (long) context.get(FacilioConstants.ContextNames.APP_ID);
        long maintenanceAppId =  ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        List<ApplicationRelatedAppsContext> relatedApps = new ArrayList<>();
        ApplicationRelatedAppsContext relatedAppsContext = new ApplicationRelatedAppsContext();
        relatedAppsContext.setApplicationId(maintenanceAppId);
        relatedAppsContext.setRelatedApplicationId(mainAppId);
        relatedApps.add(relatedAppsContext);
        ApplicationApi.addRelatedApplications(relatedApps);
        return false;
    }
}

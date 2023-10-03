package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;

public class GetAllApplicationBasedOnDashboardCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<ApplicationContext> applications = null;
        List<Integer> tabTypeIds = (List<Integer>) context.get(FacilioConstants.ContextNames.WEB_TAB_ID);
        applications = ApplicationApi.getApplicationsContainsTabTypes(tabTypeIds);
        context.put(FacilioConstants.ContextNames.APPLICATION, applications);

        return false;
    }
}

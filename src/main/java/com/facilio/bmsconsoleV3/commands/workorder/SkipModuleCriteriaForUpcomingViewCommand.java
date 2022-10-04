package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SkipModuleCriteriaForUpcomingViewCommand extends FacilioCommand {

    private static final Logger LOGGER =
            LogManager.getLogger(SkipModuleCriteriaForUpcomingViewCommand.class.getName());

    private boolean upcomingWorkorders(FacilioView view) {
        return view != null && view.getName().startsWith("upcoming");
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {

        LOGGER.trace("Starting SkipModuleCriteriaForUpcomingViewCommand");

        FacilioView view = (FacilioView) context.get("customView");
        if (upcomingWorkorders(view)) {
            context.put(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, true);
        }
        return false;
    }
}

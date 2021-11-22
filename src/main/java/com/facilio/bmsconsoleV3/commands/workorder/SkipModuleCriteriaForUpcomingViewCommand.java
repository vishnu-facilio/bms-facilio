package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class SkipModuleCriteriaForUpcomingViewCommand extends FacilioCommand {

    private boolean upcomingWorkorders(FacilioView view) {
        return view != null && view.getName().startsWith("upcoming");
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioView view = (FacilioView) context.get("customView");
        if (upcomingWorkorders(view)) {
            context.put(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, true);
        }
        return false;
    }
}

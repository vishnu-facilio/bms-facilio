package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class SetTimelineViewContextCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (moduleName == null) {
            throw new Exception("Module name cannot be null.");
        }

        Map<String, Object> recordMap = ((Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP));
        List<PMPlanner> pmPlannerList = (List<PMPlanner>) recordMap.get(moduleName);

        for (PMPlanner pmPlanner : pmPlannerList) {
            Long resourceTimelineViewId = pmPlanner.getResourceTimelineViewId();
            if (resourceTimelineViewId != null) {
                pmPlanner.setResourceTimelineView(ViewAPI.getView(resourceTimelineViewId));
            }
            Long staffTimelineViewId = pmPlanner.getStaffTimelineViewId();
            if(staffTimelineViewId != null) {
                pmPlanner.setStaffTimelineView(ViewAPI.getView(staffTimelineViewId));
            }
        }

        return false;
    }
}

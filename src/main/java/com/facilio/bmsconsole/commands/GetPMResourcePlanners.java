package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;

import java.util.Collections;
import java.util.Map;

public class GetPMResourcePlanners extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long recordId = Constants.getRecordId(context);
        Map<Long, PMResourcePlannerContext> pmResourcesPlanner = PreventiveMaintenanceAPI.getPMResourcesPlanner(recordId);
        if (MapUtils.isNotEmpty(pmResourcesPlanner)) {
            Constants.setResult(context, pmResourcesPlanner.values());
        } else {
            Constants.setResult(context, Collections.EMPTY_LIST);
        }


        return false;
    }
}

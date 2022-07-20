package com.facilio.bmsconsoleV3.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Log4j
public class PublishPMCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long pmId = (long) context.get("pmId");

        List<Long> plannerIds = PlannedMaintenanceAPI.getPlanners(pmId);
        if (CollectionUtils.isEmpty(plannerIds)) {
            LOGGER.error("Planner is missing");
            return false;
        }

        for (Long plannerId : plannerIds) {
            PlannedMaintenanceAPI.schedulePlanner(plannerId);
        }

        return false;
    }
}

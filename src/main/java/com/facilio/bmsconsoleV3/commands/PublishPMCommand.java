package com.facilio.bmsconsoleV3.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Log4j
public class PublishPMCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long pmId = (Long) context.get("pmId");
        List<Long> pmIds = new ArrayList<>();
        if (pmId != null) {
            pmIds.add(pmId);
        } else {
            pmIds = (List<Long>) context.get("pmIds");
        }

        if (CollectionUtils.isEmpty(pmIds)) {
            throw new IllegalArgumentException("Pm ids cannot be empty");
        }

        List<Long> plannerIds = PlannedMaintenanceAPI.getPlannerIds(pmIds);
        if (CollectionUtils.isEmpty(plannerIds)) {
            LOGGER.error("Planner is missing");
            return false;
        }

        for (Long plannerId : plannerIds) {
            PlannedMaintenanceAPI.schedulePlanner(plannerId);
            LOGGER.info("Wo got created from Planner "+plannerId);
        }

        return false;
    }
}

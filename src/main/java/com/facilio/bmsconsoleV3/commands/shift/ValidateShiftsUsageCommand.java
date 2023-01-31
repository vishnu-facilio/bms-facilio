package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import java.util.List;

public class ValidateShiftsUsageCommand extends FacilioCommand {

    private boolean shiftUsageBreach(long shiftID) throws Exception {
        return ShiftAPI.getAssociatedEmployeesCount(shiftID, ShiftAPI.getTodayEpochDate()) > 0;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<Long> shiftIDs = (List<Long>) context.get("recordIds");
        for (long id : shiftIDs) {
            if (shiftUsageBreach(id)) {
                throw new IllegalArgumentException("Shift is associated with employees. Remove employees before delete");
            }
        }

        return false;
    }

}

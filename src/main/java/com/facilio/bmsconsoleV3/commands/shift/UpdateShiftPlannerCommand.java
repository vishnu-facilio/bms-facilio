package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

public class UpdateShiftPlannerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long peopleID = (Long) context.get("peopleID");
        long shiftID = (Long) context.get("shiftID");
        long shiftStart = (Long) context.get("shiftStart");
        long shiftEnd = (Long) context.get("shiftEnd");

        ShiftAPI.updatePeopleShift(peopleID, shiftID, shiftStart, shiftEnd);
        return false;
    }
}
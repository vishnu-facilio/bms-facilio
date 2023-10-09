package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class UpdateShiftPlannerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        validateProps(context);

        List<Long> people = (List<Long>) context.get(FacilioConstants.ContextNames.PEOPLE);
        long shiftID = (Long) context.get(FacilioConstants.Shift.SHIFT_ID);
        long shiftStart = (Long) context.get(FacilioConstants.Shift.SHIFT_START);
        long shiftEnd = (Long) context.get(FacilioConstants.Shift.SHIFT_END);

        ShiftAPI.updateEmployeeShift(people, shiftID, shiftStart, shiftEnd);
        return false;
    }

    private void validateProps(Context context) {

        List<Long> people = (List<Long>) context.get(FacilioConstants.ContextNames.PEOPLE);
        if (CollectionUtils.isEmpty(people)){
            throw new IllegalArgumentException("people is a mandatory prop");
        }

        Long shiftID = (Long) context.get(FacilioConstants.ContextNames.SHIFT_ID);
        if (shiftID == null || shiftID <= 0){
            throw new IllegalArgumentException("shiftID is a mandatory prop");
        }

        Long shiftStart = (Long) context.get(FacilioConstants.Shift.SHIFT_START);
        if (shiftStart == null || shiftStart <= 0){
            throw new IllegalArgumentException("shiftStart is a mandatory prop");
        }

        Long shiftEnd = (Long) context.get(FacilioConstants.Shift.SHIFT_END);
        if (shiftEnd == null || shiftEnd <= 0){
            throw new IllegalArgumentException("shiftEnd is a mandatory prop");
        }
    }
}
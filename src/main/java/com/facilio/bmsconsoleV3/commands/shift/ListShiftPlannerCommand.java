package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.*;

public class ListShiftPlannerCommand extends FacilioCommand {
    
    @Override
    public boolean executeCommand(Context context) throws Exception {

        validateProps(context);

        Long rangeFrom = (Long) context.get(FacilioConstants.Shift.RANGE_FROM);
        Long rangeTo = (Long) context.get(FacilioConstants.Shift.RANGE_TO);
        Long employeeID = (Long) context.get(FacilioConstants.Shift.EMPLOYEE_ID);

        List<Map<String, Object>> shifts = ShiftAPI.getShiftListDecoratedWithWeeklyOff(employeeID, rangeFrom, rangeTo);

        context.put(FacilioConstants.Shift.SHIFTS, shifts);
        return false;
    }

    private void validateProps(Context context) {
        Long rangeFrom = (Long) context.get(FacilioConstants.Shift.RANGE_FROM);
        if (rangeFrom == null || rangeFrom <= 0){
            throw new IllegalArgumentException("rangeFrom is a mandatory prop");
        }

        Long rangeTo = (Long) context.get(FacilioConstants.Shift.RANGE_TO);
        if (rangeTo == null || rangeTo <= 0){
            throw new IllegalArgumentException("rangeTo is a mandatory prop");
        }

        Long employeeID = (Long) context.get(FacilioConstants.Shift.EMPLOYEE_ID);
        if (employeeID == null || employeeID <= 0){
            throw new IllegalArgumentException("employeeID is a mandatory prop");
        }
    }

}
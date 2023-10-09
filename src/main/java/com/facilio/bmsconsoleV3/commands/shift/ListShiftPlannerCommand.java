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
        Long peopleId = (Long) context.get(FacilioConstants.ContextNames.PEOPLE_ID);

        List<Map<String, Object>> shifts = ShiftAPI.getShiftListDecoratedWithWeeklyOff(peopleId, rangeFrom, rangeTo);

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

        Long peopleId = (Long) context.get(FacilioConstants.ContextNames.PEOPLE_ID);
        if (peopleId == null || peopleId <= 0){
            throw new IllegalArgumentException("peopleId is a mandatory prop");
        }
    }

}
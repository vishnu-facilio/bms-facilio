package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.*;

public class ListShiftPlannerCommand extends FacilioCommand {
    
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long rangeFrom = (Long) context.get(FacilioConstants.Shift.RANGE_FROM);
        Long rangeTo = (Long) context.get(FacilioConstants.Shift.RANGE_TO);
        Long peopleID = (Long) context.get(FacilioConstants.Shift.PEOPLE_ID);

        List<Map<String, Object>> shifts = ShiftAPI.getShiftList(peopleID, rangeFrom, rangeTo);

        context.put("shifts", shifts);
        return false;
    }

}
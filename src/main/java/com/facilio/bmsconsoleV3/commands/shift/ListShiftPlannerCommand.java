package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.*;

public class ListShiftPlannerCommand extends FacilioCommand {
    
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long timelineValue = (Long) context.get(FacilioConstants.Shift.TIMELINE_VALUE);
        String timelineMetric = (String) context.get(FacilioConstants.Shift.TIMELINE_METRIC);
        Long peopleID = (Long) context.get(FacilioConstants.Shift.PEOPLE_ID);

        List<Map<String, Object>> shifts = timelineMetric.equals("month") ?
                ShiftAPI.getMonthlyShiftList(peopleID, timelineValue) :
                ShiftAPI.getWeeklyShiftList(peopleID, timelineValue);

        context.put("shifts", shifts);
        return false;
    }

}
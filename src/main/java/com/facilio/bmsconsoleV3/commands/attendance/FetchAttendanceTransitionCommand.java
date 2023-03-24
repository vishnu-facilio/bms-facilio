package com.facilio.bmsconsoleV3.commands.attendance;

import com.facilio.bmsconsoleV3.context.attendance.AttendanceTransaction;
import com.facilio.bmsconsoleV3.util.AttendanceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;

public class FetchAttendanceTransitionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        validateProps(context);

        Long peopleID = (Long) context.get(FacilioConstants.Attendance.PEOPLE_ID);
        List<AttendanceTransaction.Type> transitions = AttendanceAPI.getAttendanceTransitions(peopleID);

        context.put(FacilioConstants.Attendance.ATTENDANCE_TRANSITIONS, transitions);

        return false;
    }

    private void validateProps(Context context) {
        Long peopleID = (Long) context.get(FacilioConstants.Attendance.PEOPLE_ID);
        if (peopleID == null || peopleID <= 0){
            throw new IllegalArgumentException("peopleID is a mandatory prop");
        }
    }
}

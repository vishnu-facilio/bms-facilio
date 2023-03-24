package com.facilio.bmsconsoleV3.commands.attendance;

import com.facilio.bmsconsoleV3.context.attendance.AttendanceTransaction;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import com.facilio.bmsconsoleV3.util.AttendanceAPI;

import java.util.List;

public class FetchAttendanceTransactionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        validateProps(context);

        Long peopleID = (Long) context.get(FacilioConstants.Attendance.PEOPLE_ID);

        Long date = (Long) context.get(FacilioConstants.Attendance.DATE);

        List<AttendanceTransaction> transactions = AttendanceAPI.getAttendanceTxnsForGivenDay(date, peopleID);

        context.put(FacilioConstants.Attendance.ATTENDANCE_TRANSACTION, transactions);
        return false;
    }

    private void validateProps(Context context) {
        Long date = (Long) context.get(FacilioConstants.Attendance.DATE);
        if (date == null || date <= 0){
            throw new IllegalArgumentException("date is a mandatory prop");
        }
        Long peopleID = (Long) context.get(FacilioConstants.Attendance.PEOPLE_ID);
        if (peopleID == null || peopleID <= 0){
            throw new IllegalArgumentException("peopleID is a mandatory prop");
        }
    }
}

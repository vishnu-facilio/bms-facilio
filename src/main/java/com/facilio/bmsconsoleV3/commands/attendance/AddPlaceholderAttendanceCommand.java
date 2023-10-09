package com.facilio.bmsconsoleV3.commands.attendance;

import com.facilio.bmsconsoleV3.context.attendance.Attendance;
import com.facilio.bmsconsoleV3.context.attendance.AttendanceTransaction;
import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.bmsconsoleV3.util.AttendanceAPI;
import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class AddPlaceholderAttendanceCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AttendanceTransaction> txns = recordMap.get(moduleName);

        if (txns.size() != 1) {
            throw new IllegalArgumentException("expected a single attendance transaction");
        }
        AttendanceTransaction tx = txns.get(0);

        if (notCheckInTransaction(tx)){
            return false;
        }

        if (notFirstTransactionOfTheDay(tx)){
            return false;
        }

        addPlaceholderAttendance(tx);
        return false;
    }

    private void addPlaceholderAttendance(AttendanceTransaction tx) throws Exception {
        Long peopleID = tx.getPeople().getId();
        Long date = AttendanceAPI.stripTime(tx.getTransactionTime());
        Shift shift = ShiftAPI.getPeopleShiftForDay(peopleID, date);

        Attendance attendance = new Attendance();
        attendance.setShift(shift);
        attendance.setDay(date);
        attendance.setPeople(tx.getPeople());
        attendance.setCheckInTime(tx.getTransactionTime());

        FacilioModule attendanceMod = Constants.getModBean().getModule(FacilioConstants.Attendance.ATTENDANCE);

        InsertRecordBuilder<Attendance> insert = new InsertRecordBuilder<Attendance>()
                .module(attendanceMod)
                .fields(Constants.getModBean().getModuleFields(attendanceMod.getName()))
                .addRecord(attendance);
        insert.save();
    }

    private boolean notFirstTransactionOfTheDay(AttendanceTransaction tx) throws Exception {
        Long date = AttendanceAPI.stripTime(tx.getTransactionTime());
        Long peopleID = tx.getPeople().getId();
        List<AttendanceTransaction> txns = AttendanceAPI.getAttendanceTxnsForGivenDay(date, peopleID);
        return !(txns.size() == 1);
    }

    private static boolean notCheckInTransaction(AttendanceTransaction tx) {
        return !tx.getTransactionType().equals(AttendanceTransaction.Type.CHECK_IN);
    }
}

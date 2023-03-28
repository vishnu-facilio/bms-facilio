package com.facilio.bmsconsoleV3.commands.attendance;

import com.facilio.bmsconsoleV3.context.attendance.Attendance;
import com.facilio.bmsconsoleV3.context.attendance.AttendanceSettings;
import com.facilio.bmsconsoleV3.context.attendance.AttendanceTransaction;
import com.facilio.bmsconsoleV3.context.shift.Break;
import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.bmsconsoleV3.util.AttendanceAPI;
import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.stream.Collectors;

public class UpdateAttendance extends FacilioCommand {


    @Override
    public boolean executeCommand(Context context) throws Exception {

        String ModName = FacilioConstants.Attendance.ATTENDANCE_TRANSACTION;
        Map<String, List> recordMap = (Map<String, List>) context.get("recordMap");
        List<AttendanceTransaction> transactionsCreated = recordMap.get(ModName);
        AttendanceTransaction transaction = transactionsCreated.get(0);

        // attendance creation/update to be done only after checkout
        if (!isCheckoutTransaction(transaction)) {
            return false;
        }

        FacilioModule attendanceMod = Constants.getModBean().getModule(FacilioConstants.Attendance.ATTENDANCE);
        Long peopleID = transaction.getPeople().getId();
        Long date = stripTime(transaction.getTransactionTime());
        Shift shift = ShiftAPI.getPeopleShiftForDay(peopleID, date);

        Attendance existingAttendance = AttendanceAPI.getAttendanceForGivenDay(date, peopleID);

        AttendanceSettings settings = AttendanceAPI.getAttendanceSettings();

        List<AttendanceTransaction> transactions = AttendanceAPI.getAttendanceTxnsForGivenDay(date, peopleID);
        fillBreaks(transactions);
        Attendance reducedAttendance = AttendanceAPI.reduceAttendanceTransactions(settings, transactions);
        reducedAttendance.setPeople(transaction.getPeople());
        reducedAttendance.setShift(shift);

        if (existingAttendance == null) {
            createAttendance(attendanceMod, reducedAttendance);
            return false;
        }
        updateAttendance(attendanceMod, existingAttendance, reducedAttendance);
        return false;
    }

    private void fillBreaks(List<AttendanceTransaction> transactions) throws Exception {
        Map<Long, Break> cache = new HashMap<>();
        for (AttendanceTransaction tr : transactions) {
            if (tr.getShiftBreak() == null) {
                continue;
            }
            Long breakID = tr.getShiftBreak().getId();
            if (cache.containsKey(breakID)) {
                tr.setShiftBreak(cache.get(breakID));
            } else {
                Break fetchedBreak = ShiftAPI.getBreak(breakID);
                cache.put(breakID, fetchedBreak);
                tr.setShiftBreak(fetchedBreak);
            }
        }
    }

    private static void updateAttendance(FacilioModule attendanceMod, Attendance existingAttendance, Attendance reducedAttendance) throws Exception {
        UpdateRecordBuilder<Attendance> updateBuilder = new UpdateRecordBuilder<Attendance>()
                .moduleName(attendanceMod.getName())
                .fields(Constants.getModBean().getModuleFields(attendanceMod.getName()))
                .andCondition(CriteriaAPI.getIdCondition(existingAttendance.getId(), attendanceMod));
        updateBuilder.update(reducedAttendance);
    }

    private static void createAttendance(FacilioModule attendanceMod, Attendance reducedAttendance) throws Exception {
        InsertRecordBuilder<Attendance> insert = new InsertRecordBuilder<Attendance>()
                .module(attendanceMod)
                .fields(Constants.getModBean().getModuleFields(attendanceMod.getName()))
                .addRecord(reducedAttendance);
        insert.save();
    }

    private static boolean isCheckoutTransaction(AttendanceTransaction transaction) {
        return transaction.getTransactionType().equals(AttendanceTransaction.Type.CHECK_OUT);
    }

    private Long stripTime(Long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}
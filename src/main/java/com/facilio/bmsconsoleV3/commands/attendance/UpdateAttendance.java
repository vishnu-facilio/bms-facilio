package com.facilio.bmsconsoleV3.commands.attendance;

import com.facilio.bmsconsole.workflow.rule.EventType;
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
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class UpdateAttendance extends FacilioCommand {


    @Override
    public boolean executeCommand(Context context) throws Exception {

        String ModName = FacilioConstants.Attendance.ATTENDANCE_TRANSACTION;
        Map<String, List> recordMap = (Map<String, List>) context.get("recordMap");
        List<AttendanceTransaction> transactionsCreated = recordMap.get(ModName);
        AttendanceTransaction transaction = transactionsCreated.get(0);

        if (isCreateTransaction(context) &&
                isNotCheckoutTransaction(transaction)){
            return false;
        }

        FacilioModule attendanceMod = Constants.getModBean().getModule(FacilioConstants.Attendance.ATTENDANCE);
        Long peopleID = transaction.getPeople().getId();
        Long date = AttendanceAPI.stripTime(transaction.getTransactionTime());
        Shift shift = ShiftAPI.getPeopleShiftForDay(peopleID, date);

        AttendanceSettings settings = AttendanceAPI.getAttendanceSettings();
        Attendance existingAttendance = AttendanceAPI.getAttendanceForGivenDay(date, peopleID);

        List<AttendanceTransaction> transactions = AttendanceAPI.getAttendanceTxnsForGivenDay(date, peopleID);
        long previousDay = date - ShiftAPI.DAY_IN_MILLIS;
        List<AttendanceTransaction> previousDayTransactions = AttendanceAPI.getAttendanceTxnsForGivenDay(previousDay, peopleID);

        if(CollectionUtils.isNotEmpty(previousDayTransactions)) {
            if (existingAttendance == null && isPersonSignedInSincePreviousDay(previousDayTransactions)) {
                List<AttendanceTransaction> cumulativeTransactions = new ArrayList<>();
                cumulativeTransactions.addAll(previousDayTransactions);
                cumulativeTransactions.addAll(transactions);
                transactions = cumulativeTransactions;

                existingAttendance = AttendanceAPI.getAttendanceForGivenDay(previousDay, peopleID);
            }
        }

        if (transactions.size() < 2){
            return false;
        }
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

    private boolean isPersonSignedInSincePreviousDay(List<AttendanceTransaction> previousDayTransactions) {
        AttendanceTransaction lastTransaction = previousDayTransactions.get(previousDayTransactions.size() - 1);
        if (lastTransaction.getTransactionType().equals(AttendanceTransaction.Type.CHECK_IN)){
            return true;
        }
        return false;
    }


    private boolean isCreateTransaction(Context context) {
        return context.get("eventType").equals(EventType.CREATE);
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

    private static boolean isNotCheckoutTransaction(AttendanceTransaction transaction) {
        return !transaction.getTransactionType().equals(AttendanceTransaction.Type.CHECK_OUT);
    }


}

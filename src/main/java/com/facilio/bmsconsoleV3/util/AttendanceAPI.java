package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.attendance.Attendance;
import com.facilio.bmsconsoleV3.context.attendance.AttendanceSettings;
import com.facilio.bmsconsoleV3.context.attendance.AttendanceTransaction;
import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.facilio.bmsconsoleV3.context.shift.Break;

import java.util.*;

public class AttendanceAPI {
    private static final Logger LOGGER = LogManager.getLogger(AttendanceAPI.class.getName());


    public static Attendance getAttendanceForToday(long peopleID) throws Exception {
        return getAttendance(ShiftAPI.getTodayEpochDate(), peopleID);
    }

    public static Attendance getAttendanceForGivenDay(long date, long peopleID) throws Exception {
        return getAttendance(date, peopleID);
    }

    private static Attendance getAttendance(long date, long peopleID) throws Exception {

        FacilioModule module =
                Constants.getModBean().getModule(FacilioConstants.Attendance.ATTENDANCE);

        List<FacilioField> attendanceFields =
                Constants.getModBean().getAllFields(FacilioConstants.Attendance.ATTENDANCE);

        Map<String, FacilioField> fieldMap =
                FieldFactory.getAsMap(attendanceFields);

        Condition peopleCondition = CriteriaAPI.getCondition(fieldMap.get("people"),
                Collections.singletonList(peopleID),
                NumberOperators.EQUALS);

        Condition dateCondition = CriteriaAPI.getCondition(fieldMap.get("day"),
                Collections.singletonList(date),
                NumberOperators.EQUALS);

        SelectRecordsBuilder<Attendance> builder = new SelectRecordsBuilder<Attendance>()
                .beanClass(Attendance.class)
                .module(module)
                .select(attendanceFields)
                .andCondition(peopleCondition)
                .andCondition(dateCondition);

        List<Attendance> attendances = builder.get();
        LOGGER.debug("attendance fetched " + attendances.size() + " txns for people ID " + peopleID + " on " + date);
        if (CollectionUtils.isEmpty(attendances)) {
            return null;
        }
        return attendances.get(0);
    }

    public static AttendanceSettings getAttendanceSettings() throws Exception {
        FacilioModule mod = ModuleFactory.getAttendanceSettingsPseudoModule();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getAttendanceSettingsPseudoModuleFields())
                .table(mod.getTableName());
        List<Map<String, Object>> resultSet = selectBuilder.get();

        if (resultSet != null && resultSet.size() != 1) {
            throw new Exception("attendance settings mismatch");
        }

        return FieldUtil.getAsBeanFromMap(resultSet.get(0), AttendanceSettings.class);
    }

    public static List<AttendanceTransaction> getAttendanceTxnsForToday(long peopleID) throws Exception {
        return getAttendanceTxnsForGivenDay(ShiftAPI.getTodayEpochDate(), peopleID);
    }

    public static List<AttendanceTransaction> getAttendanceTxnsForGivenDay(long date, long peopleID) throws Exception {

        FacilioModule module =
                Constants.getModBean().getModule(FacilioConstants.Attendance.ATTENDANCE_TRANSACTION);

        List<FacilioField> attendanceTxnFields =
                Constants.getModBean().getAllFields(FacilioConstants.Attendance.ATTENDANCE_TRANSACTION);

        Map<String, FacilioField> fieldMap =
                FieldFactory.getAsMap(attendanceTxnFields);

        Condition peopleCondition = CriteriaAPI.getCondition(fieldMap.get("people"),
                Collections.singletonList(peopleID),
                NumberOperators.EQUALS);

        Condition dateConditionGTE = CriteriaAPI.getCondition(fieldMap.get("transactionTime"),
                Collections.singletonList(date),
                NumberOperators.GREATER_THAN_EQUAL);

        Condition dateConditionLT = CriteriaAPI.getCondition(fieldMap.get("transactionTime"),
                Collections.singletonList(date + ShiftAPI.DAY_IN_MILLIS),
                NumberOperators.LESS_THAN);

        Criteria timeCriteria = new Criteria();
        timeCriteria.addOrCondition(dateConditionGTE);
        timeCriteria.addOrCondition(dateConditionLT);

        SelectRecordsBuilder<AttendanceTransaction> builder = new SelectRecordsBuilder<AttendanceTransaction>()
                .beanClass(AttendanceTransaction.class)
                .module(module)
                .select(attendanceTxnFields)
                .andCondition(peopleCondition)
                .andCriteria(timeCriteria)
                .orderBy("ID ASC");

        List<AttendanceTransaction> txns = builder.get();
        LOGGER.debug("attendance txns fetched " + txns.size() + " txns for people ID " + peopleID + " on " + date);
        return txns;
    }

    public static Attendance reduceAttendanceTransactions(AttendanceSettings settings, List<AttendanceTransaction> txns) throws Exception {

        if (txns.size() < 2) {
            throw new Exception("insufficient attendance transactions to reduce");
        }

        V3PeopleContext people = txns.get(0).getPeople();
        Shift peopleShift = ShiftAPI.getPeopleShiftForDay(people.getId(), ShiftAPI.getTodayEpochDate());

        Attendance attendance = new Attendance();
        attendance.setShift(peopleShift);
        computeBreaksConsumed(attendance, txns);
        computeWorkingHoursAndDay(attendance, txns);
        computeAttendanceStatus(settings, attendance);
        return attendance;
    }

    private static void computeAttendanceStatus(AttendanceSettings settings, Attendance attendance) throws Exception {
        boolean workedMoreThanRequiredMinHours = false;
        switch (settings.getWorkingHoursMode()) {
            case SHIFT:
                final Long TWENTY_FOUR_HOURS_IN_SECS = 24 * 60 * 60L;
                Shift peopleShift = ShiftAPI.getShift(attendance.getShift().getId());

                Long startTime = peopleShift.getStartTime();
                Long endTime = peopleShift.getEndTime();
                Long shiftWorkingHoursInSecs = 0L;
                if (startTime.equals(endTime)){
                    shiftWorkingHoursInSecs = TWENTY_FOUR_HOURS_IN_SECS;
                } else if (startTime > endTime) {
                    shiftWorkingHoursInSecs = TWENTY_FOUR_HOURS_IN_SECS - (startTime - endTime);
                }else {
                    shiftWorkingHoursInSecs = startTime - endTime;
                }
                Long expectedMinWorkHoursInMillis = shiftWorkingHoursInSecs * 1000;
                workedMoreThanRequiredMinHours =
                        attendance.getWorkingHours() >= expectedMinWorkHoursInMillis;
                break;
            case CUSTOM:
                workedMoreThanRequiredMinHours =
                        attendance.getWorkingHours() >= settings.getMinWorkHours();
                break;
        }
        attendance.setStatus(workedMoreThanRequiredMinHours ?
                Attendance.Status.PRESENT : Attendance.Status.ABSENT);
    }

    private static void computeWorkingHoursAndDay(Attendance attendance, List<AttendanceTransaction> txns) {
        AttendanceTransaction firstCheckInTransaction = txns.get(0);
        attendance.setDay(firstCheckInTransaction.getTransactionTime());
        attendance.setCheckInTime(firstCheckInTransaction.getTransactionTime());

        AttendanceTransaction lastCheckOutTransaction = getLastCheckoutTxn(txns);
        attendance.setCheckOutTime(lastCheckOutTransaction.getTransactionTime());

        Long clockedHours = lastCheckOutTransaction.getTransactionTime() - firstCheckInTransaction.getTransactionTime();
        attendance.setWorkingHours(clockedHours - attendance.getTotalUnpaidBreakHrs());
    }

    private static void computeBreaksConsumed(Attendance attendance, List<AttendanceTransaction> txns) {
        for (int ix = 0; ix < txns.size(); ix++) {
            AttendanceTransaction txn = txns.get(ix);
            if (!isBreakTransaction(txn)) {
                continue;
            }
            AttendanceTransaction resumeWorkTxn = txns.get(ix + 1);

            long breakTimeElapsed = resumeWorkTxn.getTransactionTime() - txn.getTransactionTime();
            Break associatedBreak = txn.getShiftBreak();

            if (isPaidBreak(associatedBreak)){
                long allowedTime = associatedBreak.getBreakTime();
                if (breakTimeElapsed > allowedTime){
                    long extraBreakTimeConsumed = breakTimeElapsed - allowedTime;
                    attendance.setWorkingHours(attendance.getWorkingHours() - extraBreakTimeConsumed);
                }
                attendance.setTotalPaidBreakHrs(attendance.getTotalPaidBreakHrs() + allowedTime);
                continue;
            }
            // handling for unpaid break
            attendance.setWorkingHours(attendance.getWorkingHours() - breakTimeElapsed);
            attendance.setTotalUnpaidBreakHrs(attendance.getTotalUnpaidBreakHrs() + breakTimeElapsed);
        }
    }

    private static boolean isPaidBreak(Break associatedBreak) {
        return associatedBreak.getBreakType().equals(Break.Type.PAID);
    }

    private static boolean isBreakTransaction(AttendanceTransaction txn) {
        return txn.getTransactionType().equals(AttendanceTransaction.Type.BREAK);
    }

    private static AttendanceTransaction getLastCheckoutTxn(List<AttendanceTransaction> txns) {

        for (int ix = txns.size() - 1; ix >= 0; ix--) {
            AttendanceTransaction tx = txns.get(ix);
            if (tx.getTransactionType() == AttendanceTransaction.Type.CHECK_OUT) {
                return tx;
            }
        }
        return null;
    }

    public static List<Attendance> fetchAttendanceInRange(Long peopleID, Long rangeFrom, Long rangeTo) throws Exception {
        FacilioModule module =
                Constants.getModBean().getModule(FacilioConstants.Attendance.ATTENDANCE);

        List<FacilioField> attendanceFields =
                Constants.getModBean().getAllFields(FacilioConstants.Attendance.ATTENDANCE);

        Map<String, FacilioField> fieldMap =
                FieldFactory.getAsMap(attendanceFields);

        Condition peopleCondition = CriteriaAPI.getCondition(fieldMap.get("people"),
                Collections.singletonList(peopleID),
                NumberOperators.EQUALS);

        Condition dateConditionGTE = CriteriaAPI.getCondition(fieldMap.get("day"),
                Collections.singletonList(rangeFrom),
                NumberOperators.GREATER_THAN_EQUAL);

        Condition dateConditionLTE = CriteriaAPI.getCondition(fieldMap.get("day"),
                Collections.singletonList(rangeTo),
                NumberOperators.LESS_THAN_EQUAL);

        Criteria timeCriteria = new Criteria();
        timeCriteria.addAndCondition(dateConditionGTE);
        timeCriteria.addAndCondition(dateConditionLTE);

        SelectRecordsBuilder<Attendance> builder = new SelectRecordsBuilder<Attendance>()
                .beanClass(Attendance.class)
                .module(module)
                .select(attendanceFields)
                .andCondition(peopleCondition)
                .andCriteria(timeCriteria);

        List<Attendance> attendances = builder.get();
        LOGGER.debug("attendances fetched " + attendances.size() + " txns for people ID " + peopleID + " from " + rangeFrom + " to " + rangeTo);
        return attendances;
    }

    public static List<AttendanceTransaction.Type> getAttendanceTransitions(Long peopleID) throws Exception {
        List<AttendanceTransaction> transactions = getAttendanceTxnsForToday(peopleID);
        if (CollectionUtils.isEmpty(transactions)) {
            return Collections.singletonList(AttendanceTransaction.Type.CHECK_IN);
        }
        AttendanceTransaction lastTransaction = transactions.get(transactions.size() - 1);
        switch (lastTransaction.getTransactionType()) {
            case CHECK_IN:
            case RESUME_WORK:
                return Arrays.asList(AttendanceTransaction.Type.CHECK_OUT, AttendanceTransaction.Type.BREAK);
            case BREAK:
                return Collections.singletonList(AttendanceTransaction.Type.RESUME_WORK);
            case CHECK_OUT:
                return Collections.singletonList(AttendanceTransaction.Type.CHECK_IN);
        }
        return null;
    }

    public static List<V3PeopleContext> getPeopleWithoutAttendance(Long date) throws Exception {

//        SELECT
//        Employee.ID
//                FROM
//        Employee
//        INNER JOIN
//        Attendance
//        ON Employee.ID = Attendance.PEOPLE
//        WHERE
//        Attendance.DAY >= 1 AND
//        Attendance.DAY < 1;

        FacilioModule employeeMod =
                Constants.getModBean().getModule(FacilioConstants.ContextNames.EMPLOYEE);

        List<FacilioField> employeeFields =
                Constants.getModBean().getAllFields(FacilioConstants.ContextNames.EMPLOYEE);

        FacilioModule attendanceMod =
                Constants.getModBean().getModule(FacilioConstants.ContextNames.ATTENDANCE);

        List<FacilioField> attendanceFields =
                Constants.getModBean().getAllFields(FacilioConstants.ContextNames.ATTENDANCE);

        Map<String, FacilioField> attendanceFieldMap =
                FieldFactory.getAsMap(attendanceFields);

        Condition dateConditionGTE = CriteriaAPI.getCondition(attendanceFieldMap.get("day"),
                Collections.singletonList(date),
                NumberOperators.GREATER_THAN_EQUAL);

        Condition dateConditionLT = CriteriaAPI.getCondition(attendanceFieldMap.get("day"),
                Collections.singletonList(date + ShiftAPI.DAY_IN_MILLIS),
                NumberOperators.LESS_THAN);

        Criteria timeCriteria = new Criteria();
        timeCriteria.addOrCondition(dateConditionGTE);
        timeCriteria.addOrCondition(dateConditionLT);

        SelectRecordsBuilder<V3PeopleContext> builder = new SelectRecordsBuilder<V3PeopleContext>()
                .beanClass(V3PeopleContext.class)
                .module(employeeMod)
                .select(employeeFields)
                .innerJoin(attendanceMod.getTableName()).on("Employee.ID = Attendance.PEOPLE")
                .andCriteria(timeCriteria);

        List<V3PeopleContext> txns = builder.get();
        LOGGER.debug("people without attendance fetched " + txns.size() + " on " + date);
        return txns;
    }

    public static void setAttendanceSettings(AttendanceSettings settings)  throws Exception{
        FacilioModule mod = ModuleFactory.getAttendanceSettingsPseudoModule();
        List<FacilioField> fields = FieldFactory.getAttendanceSettingsPseudoModuleFields();

        long orgId = AccountUtil.getCurrentOrg().getOrgId();

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(mod.getTableName())
                .fields(fields)
                .andCustomWhere("ORGID = ?", orgId);
        updateBuilder.update(FieldUtil.getAsProperties(settings));

    }
}

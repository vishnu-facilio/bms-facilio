package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.attendance.Attendance;
import com.facilio.bmsconsoleV3.context.attendance.AttendanceSettings;
import com.facilio.bmsconsoleV3.context.attendance.AttendanceTransaction;
import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LargeTextField;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.facilio.bmsconsoleV3.context.shift.Break;

import java.util.*;
import java.util.stream.Collectors;

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
        timeCriteria.addAndCondition(dateConditionGTE);
        timeCriteria.addAndCondition(dateConditionLT);

        SelectRecordsBuilder<AttendanceTransaction> builder = new SelectRecordsBuilder<AttendanceTransaction>()
                .beanClass(AttendanceTransaction.class)
                .module(module)
                .select(attendanceTxnFields)
                .andCondition(peopleCondition)
                .andCriteria(timeCriteria)
                .fetchSupplements(Arrays.asList((LargeTextField) fieldMap.get("notes")))
                .orderBy("TRANSACTION_TIME ASC");

        List<AttendanceTransaction> txns = builder.get();
        LOGGER.debug("attendance txns fetched " + txns.size() + " txns for people ID " + peopleID + " on " + date);
        return txns;
    }

    public static Attendance reduceAttendanceTransactions(AttendanceSettings settings, List<AttendanceTransaction> txns) throws Exception {

        if (txns.size() < 2) {
            throw new Exception("insufficient attendance transactions to reduce");
        }
        Attendance attendance = new Attendance();
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
                if (startTime.equals(endTime)) {
                    shiftWorkingHoursInSecs = TWENTY_FOUR_HOURS_IN_SECS;
                } else if (startTime > endTime) {
                    shiftWorkingHoursInSecs = TWENTY_FOUR_HOURS_IN_SECS - (startTime - endTime);
                } else {
                    shiftWorkingHoursInSecs = endTime - startTime;
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

    public static Long stripTime(Long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private static void computeWorkingHoursAndDay(Attendance attendance, List<AttendanceTransaction> txns) {

        AttendanceTransaction firstCheckInTransaction = getFirstCheckInTxn(txns);
        attendance.setDay(stripTime(firstCheckInTransaction.getTransactionTime()));
        attendance.setCheckInTime(firstCheckInTransaction.getTransactionTime());

        AttendanceTransaction lastCheckOutTransaction = getLastCheckoutTxn(txns);
        attendance.setCheckOutTime(lastCheckOutTransaction.getTransactionTime());

        long clockedHours = 0L;
        long startTime = 0;
        boolean checkedInForTheDay = false;
        for (AttendanceTransaction tx: txns) {
            if (checkInOrBreakTransaction(tx)){
                startTime = tx.getTransactionTime();
                checkedInForTheDay = true;
                continue;
            }

            if (!checkedInForTheDay) {
                continue;
            }

            if (checkOutOrBreakTransaction(tx)){
                Long txTime = tx.getTransactionTime();
                clockedHours +=  (txTime - startTime);
            }

        }
        attendance.setWorkingHours(clockedHours);
    }

    private static AttendanceTransaction getFirstCheckInTxn(List<AttendanceTransaction> txns) {
        for (int ix = 0; ix < txns.size(); ix++) {
            AttendanceTransaction tx = txns.get(ix);
            if (tx.getTransactionType() == AttendanceTransaction.Type.CHECK_IN) {
                return tx;
            }
        }
        return txns.get(0);
    }

    private static boolean checkOutOrBreakTransaction(AttendanceTransaction tx) {
        return tx.getTransactionType().equals(AttendanceTransaction.Type.CHECK_OUT) ||
                tx.getTransactionType().equals(AttendanceTransaction.Type.BREAK);
    }

    private static boolean checkInOrBreakTransaction(AttendanceTransaction tx) {
        return tx.getTransactionType().equals(AttendanceTransaction.Type.CHECK_IN) ||
                tx.getTransactionType().equals(AttendanceTransaction.Type.RESUME_WORK);
    }

    private static void computeBreaksConsumed(Attendance attendance, List<AttendanceTransaction> txns) {

        Map<Long, Long> breakTimeCounter = new HashMap<>();

        for (int ix = 0; ix < txns.size(); ix++) {
            AttendanceTransaction txn = txns.get(ix);
            if (!isBreakTransaction(txn)) {
                continue;
            }
            AttendanceTransaction resumeWorkTxn = txns.get(ix + 1);
            long breakTimeElapsedInMillis = resumeWorkTxn.getTransactionTime() - txn.getTransactionTime();
            Break associatedBreak = txn.getShiftBreak();

            if (!breakTimeCounter.containsKey(associatedBreak.getId())){
                long allowedTime = associatedBreak.getBreakTime();
                long allowedTimeInMillis = allowedTime * 1000;
                breakTimeCounter.put(associatedBreak.getId(), allowedTimeInMillis);
            }

            long availableBreakTimeMillis = breakTimeCounter.get(associatedBreak.getId());

            if (isPaidBreak(associatedBreak)) {

                if (breakTimeElapsedInMillis > availableBreakTimeMillis) {

                    breakTimeCounter.put(associatedBreak.getId(), 0L);

                    long allowedTime = associatedBreak.getBreakTime();
                    long allowedTimeInMillis = allowedTime * 1000;
                    attendance.setTotalPaidBreakHrs(attendance.getTotalPaidBreakHrs() + allowedTimeInMillis);
                } else {
                    breakTimeCounter.put(associatedBreak.getId(), breakTimeCounter.get(associatedBreak.getId()) - breakTimeElapsedInMillis);
                    attendance.setTotalPaidBreakHrs(attendance.getTotalPaidBreakHrs() + breakTimeElapsedInMillis);
                }

            } else {
                if (breakTimeElapsedInMillis > availableBreakTimeMillis) {

                    breakTimeCounter.put(associatedBreak.getId(), 0L);

                    long allowedTime = associatedBreak.getBreakTime();
                    long allowedTimeInMillis = allowedTime * 1000;
                    attendance.setTotalUnpaidBreakHrs(attendance.getTotalUnpaidBreakHrs() + allowedTimeInMillis);

                } else {
                    breakTimeCounter.put(associatedBreak.getId(), breakTimeCounter.get(associatedBreak.getId()) - breakTimeElapsedInMillis);
                    attendance.setTotalUnpaidBreakHrs(attendance.getTotalUnpaidBreakHrs() + breakTimeElapsedInMillis);
                }
            }
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
                .orderBy(fieldMap.get("day").getCompleteColumnName() + " DESC")
                .andCriteria(timeCriteria);

        List<Attendance> attendances = builder.get();
        LOGGER.debug("attendances fetched " + attendances.size() + " txns for people ID " + peopleID + " from " + rangeFrom + " to " + rangeTo);
        return attendances;
    }

    public static List<AttendanceTransaction.Type> getAttendanceTransitions(Long peopleID) throws Exception {

        Long now = new Date().getTime();
        AttendanceTransaction lastTransaction = getPriorAttendanceTxn(peopleID, now);
        if (lastTransaction == null) {
            return Collections.singletonList(AttendanceTransaction.Type.CHECK_IN);
        }

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

    public static List<Long> getPeopleWithoutAttendance(Long date) throws Exception {

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


        SelectRecordsBuilder<V3PeopleContext> employeeBuilder = new SelectRecordsBuilder<V3PeopleContext>()
                .beanClass(V3PeopleContext.class)
                .module(employeeMod)
                .select(employeeFields);

        List<V3PeopleContext> people = employeeBuilder.get();

        List<Long> allEmployeeIDs = people
                .stream().map(ModuleBaseWithCustomFields::getId)
                .collect(Collectors.toList());

        FacilioModule attendanceMod =
                Constants.getModBean().getModule(FacilioConstants.ContextNames.ATTENDANCE);

        List<FacilioField> attendanceFields =
                Constants.getModBean().getAllFields(FacilioConstants.ContextNames.ATTENDANCE);

        Map<String, FacilioField> attendanceFieldMap =
                FieldFactory.getAsMap(attendanceFields);

        Condition dateCondition = CriteriaAPI.getCondition(attendanceFieldMap.get("day"),
                Collections.singletonList(date),
                NumberOperators.EQUALS);

        SelectRecordsBuilder<Attendance> attendanceBuilder = new SelectRecordsBuilder<Attendance>()
                .beanClass(Attendance.class)
                .module(attendanceMod)
                .select(attendanceFields)
                .andCondition(dateCondition);

        List<Attendance> attendance = attendanceBuilder.get();

        List<Long> employeesWithAttendance = attendance
                .stream()
                .filter(a -> a.getPeople() != null)
                .map(a -> a.getPeople().getId())
                .collect(Collectors.toList());

        allEmployeeIDs.removeAll(employeesWithAttendance);

        LOGGER.debug("people without attendance fetched " + allEmployeeIDs.size() + " on " + date);
        return allEmployeeIDs;
    }

    public static void setAttendanceSettings(AttendanceSettings settings) throws Exception {
        FacilioModule mod = ModuleFactory.getAttendanceSettingsPseudoModule();
        List<FacilioField> fields = FieldFactory.getAttendanceSettingsPseudoModuleFields();

        long orgId = AccountUtil.getCurrentOrg().getOrgId();

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(mod.getTableName())
                .fields(fields)
                .andCustomWhere("ORGID = ?", orgId);
        updateBuilder.update(FieldUtil.getAsProperties(settings));
    }

    public static AttendanceTransaction getPriorAttendanceTxn(Long peopleID, Long time) throws Exception {

        FacilioModule module =
                Constants.getModBean().getModule(FacilioConstants.Attendance.ATTENDANCE_TRANSACTION);

        List<FacilioField> attendanceTxnFields =
                Constants.getModBean().getAllFields(FacilioConstants.Attendance.ATTENDANCE_TRANSACTION);

        Map<String, FacilioField> fieldMap =
                FieldFactory.getAsMap(attendanceTxnFields);

        Condition peopleCondition = CriteriaAPI.getCondition(fieldMap.get("people"),
                Collections.singletonList(peopleID),
                NumberOperators.EQUALS);

        Condition timeCriteria = CriteriaAPI.getCondition(fieldMap.get("transactionTime"),
                Collections.singletonList(time),
                NumberOperators.LESS_THAN_EQUAL);

        SelectRecordsBuilder<AttendanceTransaction> builder = new SelectRecordsBuilder<AttendanceTransaction>()
                .beanClass(AttendanceTransaction.class)
                .module(module)
                .select(attendanceTxnFields)
                .andCondition(peopleCondition)
                .andCondition(timeCriteria)
                .orderBy("TRANSACTION_TIME DESC")
                .limit(1);

        List<AttendanceTransaction> txns = builder.get();
        return txns.size() == 1 ? txns.get(0) : null;
    }

    public static boolean transactionIsNotCausallyValid(AttendanceTransaction tx) throws Exception {

        Long peopleID = tx.getPeople().getId();
        Long time = tx.getTransactionTime();
        AttendanceTransaction.Type newTxType = tx.getTransactionType();

        AttendanceTransaction previousTxn = getPriorAttendanceTxn(peopleID, time);
        if (previousTxn == null) {
            return newTxType == AttendanceTransaction.Type.CHECK_IN ?  false : true;
        }
        AttendanceTransaction.Type oldTxType = previousTxn.getTransactionType();

        switch (newTxType) {
            case CHECK_IN:
                if (oldTxType == AttendanceTransaction.Type.CHECK_IN) {
                    if (updatingFirstTransactionOfTheDay(tx)){
                        return false;
                    }
                    return true;
                }
                if (oldTxType != AttendanceTransaction.Type.CHECK_OUT) {
                    return true;
                }
                break;
            case CHECK_OUT:
                if (oldTxType == AttendanceTransaction.Type.CHECK_OUT) {
                    if (updatingLastTransactionOfTheDay(tx)){
                        return false;
                    }
                    return true;
                }
                if (oldTxType == AttendanceTransaction.Type.BREAK) {
                    return true;
                }
                break;
            case BREAK:
                if (oldTxType == AttendanceTransaction.Type.CHECK_OUT) {
                    return true;
                }
                if (oldTxType == AttendanceTransaction.Type.BREAK) {
                    return true;
                }
                break;
            case RESUME_WORK:
                if (oldTxType != AttendanceTransaction.Type.BREAK) {
                    return true;
                }
                break;
        }
        return false;
    }

    private static boolean updatingLastTransactionOfTheDay(AttendanceTransaction tx) throws Exception {

        Long peopleID = tx.getPeople().getId();
        Long time = tx.getTransactionTime();

        List<AttendanceTransaction> txns = getAttendanceTxnsForGivenDay(stripTime(time), peopleID);
        AttendanceTransaction lastTxn = txns.get(txns.size() - 1);

        return lastTxn.getId() == tx.getId();
    }

    private static boolean updatingFirstTransactionOfTheDay(AttendanceTransaction tx) throws Exception {

        Long peopleID = tx.getPeople().getId();
        Long time = tx.getTransactionTime();

        List<AttendanceTransaction> txns = getAttendanceTxnsForGivenDay(stripTime(time), peopleID);
        AttendanceTransaction firstTxn = txns.get(0);

        return firstTxn.getId() == tx.getId();
    }

    public static void markAttendanceForPreviousDay() throws Exception {
        Long previousDay = ShiftAPI.getTodayEpochDate() - ShiftAPI.DAY_IN_MILLIS;
        List<Long> people = AttendanceAPI.getPeopleWithoutAttendance(previousDay);

        List<Attendance> attendanceToBeAdded = new ArrayList<>();

        for (Long p : people) {
            Shift shift = ShiftAPI.getPeopleShiftForDay(p, previousDay);
            Attendance.Status prevDayStatus = shift.isWeeklyOff(previousDay) ?
                    Attendance.Status.WEEKLY_OFF :
                    Attendance.Status.ABSENT;
            Attendance attendance = new Attendance(previousDay, prevDayStatus);
            attendance.setShift(shift);
            V3PeopleContext emp = new V3PeopleContext();
            emp.setId(p);
            attendance.setPeople(emp);

            attendanceToBeAdded.add(attendance);
        }

        FacilioModule attendanceMod =
                Constants.getModBean().getModule(FacilioConstants.ContextNames.ATTENDANCE);

        List<FacilioField> attendanceFields =
                Constants.getModBean().getAllFields(FacilioConstants.ContextNames.ATTENDANCE);

        InsertRecordBuilder<Attendance> insert = new InsertRecordBuilder<Attendance>()
                .module(attendanceMod)
                .fields(attendanceFields)
                .addRecords(attendanceToBeAdded);
        insert.save();

        LOGGER.debug("marked " + attendanceToBeAdded.size() + " attendances for " + previousDay);
    }

}

package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.bmsconsoleV3.context.shift.ShiftSlot;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.security.MalwareInterceptor;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.modules.ModuleFactory.getShiftModule;
import static com.facilio.modules.ModuleFactory.getShiftPeopleRelPseudoModule;

public class ShiftAPI {

    private static final Logger LOGGER = LogManager.getLogger(MalwareInterceptor.class.getName());

    public static final long UNLIMITED_PERIOD = -2;
    public final static long DAY_IN_MILLIS = 24 * 60 * 60 * 1000;

    final static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy");

    /**
     * Returns the Shift object of given ID
     *
     * @param id ID of the shift to look for
     * @return retrieved Shift object
     */
    public static Shift getShift(long id) throws Exception {
        List<Shift> shifts = getShifts(Collections.singletonList(id));
        return shifts == null || shifts.isEmpty() ? null : shifts.get(0);
    }

    /**
     * Returns a list of Shifts with respect to the given ids
     *
     * @param ids list of shift IDs to be retrieved
     * @return retrieved list of Shifts
     */
    public static List<Shift> getShifts(List<Long> ids) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SHIFT);
        SelectRecordsBuilder<Shift> builder = new SelectRecordsBuilder<Shift>()
                .beanClass(Shift.class)
                .module(module)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getIdCondition(ids, module));
        return builder.get();
    }

    /**
     * Returns the default shift of the org
     *
     * @return default Shift of the org
     */
    public static Shift getDefaultShift() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SHIFT);

        FacilioField isDefaultShift =
                FieldFactory.getField("isDefault", "IS_DEFAULT", FieldType.BOOLEAN);

        Condition defaultShiftCondition =
                CriteriaAPI.getCondition(isDefaultShift, "true", BooleanOperators.IS);

        SelectRecordsBuilder<Shift> builder = new SelectRecordsBuilder<Shift>()
                .beanClass(Shift.class)
                .module(module)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(defaultShiftCondition);

        List<Shift> shifts = builder.get();
        if (shifts.size() != 1) {
            throw new Exception("more than one or no default shift found");
        }
        return shifts.get(0);
    }

    /**
     * Assigns the default Shift to the given employee perpetually
     *
     * @param employeeID ID of the employee
     */
    public static void assignDefaultShiftToEmployee(long employeeID) throws Exception {
        Shift shift = getDefaultShift();

        V3EmployeeContext emp = new V3EmployeeContext();
        emp.setId(employeeID);

        ShiftSlot defaultShiftSlot = new ShiftSlot(emp, shift, UNLIMITED_PERIOD, UNLIMITED_PERIOD);
        addSlots(Collections.singletonList(defaultShiftSlot));
    }

    /**
     * Assigns the given Shift to the given employee for the given time span.
     *
     * @param employees IDs of the Employee
     * @param shiftID   ID of the Shift to be associated
     * @param from      time span start
     * @param to        time span end
     */
    public static void updateEmployeeShift(List<Long> employees, long shiftID, long from, long to) throws Exception {

        for (Long emp : employees) {
            V3EmployeeContext employee = new V3EmployeeContext();
            employee.setId(emp);
            Shift shift = new Shift();
            shift.setId(shiftID);
            updateShift(employee, shift, from, to);
        }
    }

    /**
     * Will decorate the given Employee's shift roster with weekly off information.
     *
     * @param employeeID ID of the Employee
     * @param rangeFrom  time span start
     * @param rangeTo    time span end
     * @return shift roster for the given time span
     **/
    public static List<Map<String, Object>> getShiftListDecoratedWithWeeklyOff(Long employeeID, Long rangeFrom, Long rangeTo) throws Exception {
        List<Map<String, Object>> shifts = ShiftAPI.getShiftList(employeeID, rangeFrom, rangeTo);
        return shifts.stream()
                .map(s -> {
                    if ((Boolean) s.get("isWeeklyOff")) {
                        s.put("name", s.get("name") + " - Weekly Off");
                        s.put("startTime", "");
                        s.put("endTime", "");
                    }
                    return s;
                })
                .collect(Collectors.toList());
    }

    public static List<Map<String, Object>> getShiftList(Long employeeID, Long rangeFrom, Long rangeTo) throws Exception {

        List<ShiftSlot> slots =  fetchShiftSlots(Collections.singletonList(employeeID), rangeFrom, rangeTo);

        ShiftSlot defaultShiftSlot = slots.get(0);
        // applying default shift as base
        List<Map<String, Object>> shiftSchedule = new ArrayList<>();
        // day schedule is added to list and also a reference is stored in map
        // for applying update spreads
        Map<String, Map<String, Object>> shiftScheduleMap = new HashMap<>();
        for (long time = rangeFrom; time <= rangeTo; time += DAY_IN_MILLIS) {
            Map<String, Object> shift = new HashMap<>();
            String date = epochToReadableDate(time);
            shift.put("date", date);
            shift.put("epoch", time);
            shift.put("name", defaultShiftSlot.getShift().getName());
            shift.put("shiftId", defaultShiftSlot.getShift().getId());
            shift.put("colorCode", defaultShiftSlot.getShift().getColorCode());
            shift.put("startTime", dayEpochToReadableTime(defaultShiftSlot.getShift().getStartTime()));
            shift.put("endTime", dayEpochToReadableTime(defaultShiftSlot.getShift().getEndTime()));
            shift.put("isWeeklyOff", defaultShiftSlot.getShift().isWeeklyOff(time));

            shiftSchedule.add(shift);
            shiftScheduleMap.put(date, shift);
        }

        for (ShiftSlot slot : slots) {

            long modificationStart = slot.getFrom();
            long modificationEnd = slot.getTo();
            if (modificationStart == UNLIMITED_PERIOD || modificationEnd == UNLIMITED_PERIOD){
                continue;
            }
            Shift shift = slot.getShift();

            for (long time = modificationStart; time <= modificationEnd; time += DAY_IN_MILLIS) {
                String date = epochToReadableDate(time);
                Map<String, Object> shiftObj = shiftScheduleMap.get(date);
                if (shiftObj == null){
                    continue;
                }
                shiftObj.put("name", shift.getName());
                shiftObj.put("shiftId", shift.getId());
                shiftObj.put("colorCode", shift.getColorCode());
                shiftObj.put("startTime", dayEpochToReadableTime(shift.getStartTime()));
                shiftObj.put("endTime", dayEpochToReadableTime(shift.getEndTime()));
                shiftObj.put("isWeeklyOff", shift.isWeeklyOff(time));
            }

        }

        return shiftSchedule;
    }

    private static List<ShiftSlot> fetchShiftSlots(List<Long> employeeIDs, Long rangeFrom, Long rangeTo) throws Exception {

        //        SELECT <fields>
        //                FROM Shift_People_Rel
        //        WHERE PEOPLE_ID in (5)
        //        AND ( START_TIME = -2
        //                OR END_TIME = -2
        //                OR START_TIME >= 1672597800000
        //                OR END_TIME <= 1672684200000 )
        //        ORDER BY START_TIME;

        FacilioModule shiftMod = getShiftModule();
        List<FacilioField> shiftFields = FieldFactory.getShiftFields();

        FacilioModule shiftPeopleRelMod = getShiftPeopleRelPseudoModule();

        List<FacilioField> selectFields = new ArrayList<>();
        List<String> shiftFieldsWhitelist = Arrays.asList("name", "colorCode", "startTime", "endTime", "weekend");
        selectFields.addAll(filterFields(shiftFields, shiftFieldsWhitelist));
        selectFields.add(FieldFactory.getField("shiftID", "ID", shiftMod, FieldType.ID));

        selectFields.add(FieldFactory.getField("slotStart", "START_TIME", shiftPeopleRelMod, FieldType.NUMBER));
        selectFields.add(FieldFactory.getField("slotEnd", "END_TIME", shiftPeopleRelMod, FieldType.NUMBER));
        selectFields.add(FieldFactory.getField("peopleID", "PEOPLE_ID", shiftPeopleRelMod, FieldType.NUMBER));

        Map<String, FacilioField> fieldMap =
                FieldFactory.getAsMap(FieldFactory.getShiftPeopleRelPseudoModuleFields());

        // A.1 People Condition | PEOPLE_ID = peopleID
        Condition peopleCondition = CriteriaAPI.getCondition(fieldMap.get("peopleId"),
                employeeIDs ,
                NumberOperators.EQUALS);

        // B.1 Bound Condition | START_TIME = -2
        Condition perpetualStartTimeCond = CriteriaAPI.getCondition(fieldMap.get("startTime"),
                Collections.singletonList(UNLIMITED_PERIOD),
                NumberOperators.EQUALS);

        // B.2 Bound Condition | END_TIME = -2
        Condition perpetualEndTimeCond = CriteriaAPI.getCondition(fieldMap.get("endTime"),
                Collections.singletonList(UNLIMITED_PERIOD),
                NumberOperators.EQUALS);

        // B.3 START_TIME >= 1672597800000
        Condition startRangeCond = CriteriaAPI.getCondition(fieldMap.get("startTime"),
                Collections.singletonList(rangeFrom),
                NumberOperators.GREATER_THAN_EQUAL);

        // B.4 END_TIME <= 1672684200000
        Condition endRangeCond = CriteriaAPI.getCondition(fieldMap.get("endTime"),
                Collections.singletonList(rangeTo),
                NumberOperators.LESS_THAN_EQUAL);

        // Time Bound Criteria
        Criteria boundCriteria = new Criteria();
        boundCriteria.addOrCondition(perpetualStartTimeCond);
        boundCriteria.addOrCondition(perpetualEndTimeCond);
        boundCriteria.addOrCondition(startRangeCond);
        boundCriteria.addOrCondition(endRangeCond);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(shiftPeopleRelMod.getTableName())
                .select(selectFields)
                .innerJoin(shiftMod.getTableName())
                .on("Shift_People_Rel.SHIFT_ID = Shift.ID")
                .orderBy("Shift_People_Rel.START_TIME")
                .andCondition(peopleCondition)
                .andCriteria(boundCriteria);

        List<Map<String, Object>> resultSet = builder.get();

        List<ShiftSlot>  slots = new ArrayList<>();

        for (Map<String, Object> row: resultSet) {
            Long slotStart = (Long) row.get("slotStart");
            Long slotEnd =  (Long) row.get("slotEnd");
            Long peopleID =  (Long) row.get("peopleID");

            Shift shift = FieldUtil.cloneBean(row, Shift.class);
            Long shiftID =  (Long) row.get("shiftID");
            shift.setId(shiftID);

            V3EmployeeContext emp = new V3EmployeeContext();
            emp.setId(peopleID);

            ShiftSlot slot = new ShiftSlot(emp, shift, slotStart, slotEnd);
            slots.add(slot);
        }
        return slots;
    }


    private static List<FacilioField> filterFields(List<FacilioField> fields, List<String> whitelist) {
        return fields.stream()
                .filter(f -> whitelist.contains(f.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Converts second of day to human-readable time.
     *
     * @param time second of the day
     * @return human-readable time representation of then given time.
     **/
    private static String dayEpochToReadableTime(Object time) {
        return LocalTime.ofSecondOfDay((Long) time).toString();
    }

    /**
     * Converts epoch to human-readable date.
     *
     * @param date epoch
     * @return human-readable date representation of then given epoch.
     **/
    public static String epochToReadableDate(Long date) {
        return dateFormatter.format(new Date(date));
    }

    /**
     * Generates epoch dates between the given range.
     *
     * @param rangeFrom range start
     * @param rangeTo   range end
     * @return list of generated dates in epoch format
     **/
    public static List<Long> computeRange(Long rangeFrom, Long rangeTo) {
        List<Long> range = new ArrayList<>();
        for (long time = rangeFrom; time <= rangeTo; time += DAY_IN_MILLIS) {
            range.add(time);
        }
        return range;
    }

    /**
     * Compaction process merges contiguous identical shifts into a single block and adds 'span' attribute
     * representing the number of blocks merged.
     *
     * @param employeeID ID of the Employee
     * @param rangeFrom  range start
     * @param rangeTo    range end
     * @return list of compacted Shift sequence between the given range.
     */
    public static List<Map<String, Object>> getCompactedShiftList(long employeeID, Long rangeFrom, Long rangeTo) throws Exception {
        List<Map<String, Object>> compactedShifts = new ArrayList<>();
        List<Map<String, Object>> shifts = getShiftList(employeeID, rangeFrom, rangeTo);

        Map<String, Object> prevShift = null;
        int counter = 1;
        for (Map<String, Object> s : shifts) {
            if (prevShift == null) {
                prevShift = s;
                continue;
            }
            if (s.get("shiftId").equals(prevShift.get("shiftId")) &&
                    s.get("isWeeklyOff").equals(prevShift.get("isWeeklyOff"))) {
                counter++;
            } else {
                prevShift.put("span", counter);
                // trimming unwanted fields;
                prevShift.remove("date");

                prevShift.put("span", counter);
                counter = 1;

                compactedShifts.add(prevShift);
                prevShift = s;
            }
        }
        prevShift.put("span", counter);
        compactedShifts.add(prevShift);
        return compactedShifts;
    }

    /**
     * Returns the number of employees associated to the given Shift
     *
     * @param shiftID ID of the Shift
     * @return Number of Employee associated to the Shift
     **/
    public static Integer getAssociatedEmployeesCount(long shiftID, long date) throws Exception {

    //        SELECT count(distinct(PEOPLE_ID)) as 'count'
    //        FROM Shift_People_Rel
    //        WHERE
    //        SHIFT_ID = 5
    //        AND orgid = 7
    //        AND (
    //                (START_TIME <= 1672770600000 AND END_TIME >= 1672770600000) OR
    //                (START_TIME = -2 AND END_TIME = -2) OR
    //                (START_TIME = -2 AND END_TIME >= 1672770600000) OR
    //                (START_TIME <= 1672770600000 AND END_TIME = -2)
    //        );

        FacilioModule shiftPeopleRelMod = ModuleFactory.getShiftPeopleRelPseudoModule();
        FacilioField countField =
                FieldFactory.getField("count", "COUNT(DISTINCT PEOPLE_ID)", FieldType.NUMBER);

        Map<String, FacilioField> fieldMap =
                FieldFactory.getAsMap(FieldFactory.getShiftPeopleRelPseudoModuleFields());

        // Shift Condition | SHIFT_ID = shiftID
        Condition shiftCond = CriteriaAPI.getCondition(fieldMap.get("shiftId"),
                Collections.singleton(shiftID),
                NumberOperators.EQUALS);

        Condition startTimeLessThanEqDayCond = CriteriaAPI.getCondition(fieldMap.get("startTime"),
                Collections.singleton(date),
                NumberOperators.LESS_THAN_EQUAL);

        Condition endTimeGreaterThanEqDayCond = CriteriaAPI.getCondition(fieldMap.get("endTime"),
                Collections.singleton(date),
                NumberOperators.GREATER_THAN_EQUAL);

        // A. (START_TIME <= day AND END_TIME >= day)
        Criteria  nonPerpetualSlotInterceptingTheDateCriteria = new Criteria();
        nonPerpetualSlotInterceptingTheDateCriteria.addAndCondition(startTimeLessThanEqDayCond);
        nonPerpetualSlotInterceptingTheDateCriteria.addAndCondition(endTimeGreaterThanEqDayCond);

        Condition perpetualStartTimeCond = CriteriaAPI.getCondition(fieldMap.get("startTime"),
                Collections.singleton(UNLIMITED_PERIOD),
                NumberOperators.EQUALS);

        Condition perpetualEndTimeCond = CriteriaAPI.getCondition(fieldMap.get("endTime"),
                Collections.singleton(UNLIMITED_PERIOD),
                NumberOperators.EQUALS);

        // B. (START_TIME = -2 AND END_TIME = -2)
        Criteria  perpetualSlotCriteria = new Criteria();
        perpetualSlotCriteria.addAndCondition(perpetualStartTimeCond);
        perpetualSlotCriteria.addAndCondition(perpetualEndTimeCond);

        // C. (START_TIME = -2 AND END_TIME >= day)
        Criteria  semiPerpetualWithEndTimeSlotCriteria = new Criteria();
        semiPerpetualWithEndTimeSlotCriteria.addAndCondition(perpetualStartTimeCond);
        semiPerpetualWithEndTimeSlotCriteria.addAndCondition(endTimeGreaterThanEqDayCond);

        // D. (START_TIME <= day AND END_TIME = -2)
        Criteria  semiPerpetualWithStartTimeSlotCriteria = new Criteria();
        semiPerpetualWithStartTimeSlotCriteria.addAndCondition(startTimeLessThanEqDayCond);
        semiPerpetualWithStartTimeSlotCriteria.addAndCondition(perpetualEndTimeCond);

        // A + B + C + D
        Criteria  slotBoundsCriteria = new Criteria();
        slotBoundsCriteria.orCriteria(nonPerpetualSlotInterceptingTheDateCriteria);
        slotBoundsCriteria.orCriteria(perpetualSlotCriteria);
        slotBoundsCriteria.orCriteria(semiPerpetualWithEndTimeSlotCriteria);
        slotBoundsCriteria.orCriteria(semiPerpetualWithStartTimeSlotCriteria);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(shiftPeopleRelMod.getTableName())
                .select(Collections.singletonList(countField))
                .andCondition(shiftCond)
                .andCriteria(slotBoundsCriteria);

        List<Map<String, Object>> resultSet = builder.get();
        return FacilioUtil.parseInt(resultSet.get(0).get("count"));
    }

    /**
     * returns associated employees count for the day
     *
     * @param shiftID ID of the shift
     * @return number of employees associated
     **/
    public static Integer getAssociatedEmployeesCountForToday(long shiftID) throws Exception {
        return getAssociatedEmployeesCount(shiftID, getTodayEpochDate());
    }

    /**
     * Returns today's date in epoch
     *
     * @return date as epoch
     **/
    public static long getTodayEpochDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }


    public static void updateShift(V3PeopleContext emp, Shift shift, long from, long to) throws Exception {
        List<ShiftSlot> incidentSlots = getIncidentSlots(emp.getId(), from, to);

        List<ShiftSlot> slotsToBeDeleted = new ArrayList<>();
        List<ShiftSlot> slotsToBeAdded = new ArrayList<>();

        ShiftSlot newSlot = new ShiftSlot(emp, shift, from, to);
        slotsToBeAdded.add(newSlot);

        slotsToBeDeleted.addAll(incidentSlots);

        for (ShiftSlot incidentSlot : incidentSlots) {
            if (fullyContainedSlot(incidentSlot, newSlot)){
                continue;
            }
            if (isDefaultShiftSlot(incidentSlot)) {
                List<ShiftSlot> trimmedSlots = breakDefaultSlot(incidentSlot, newSlot);
                slotsToBeAdded.addAll(trimmedSlots);
            } else {
                ShiftSlot trimmedSlot = trimHalfContainedSlot(incidentSlot, newSlot);
                if (trimmedSlot == null){
                    throw new Exception("trimmed slot cannot be null");
                }
                slotsToBeAdded.add(trimmedSlot);
            }
        }
        deleteSlots(slotsToBeDeleted);
        addSlots(slotsToBeAdded);
    }

    private static boolean fullyContainedSlot(ShiftSlot incidentSlot, ShiftSlot newSlot) {
        // perpetual slots can never be fully contained
        if (incidentSlot.getFrom() == UNLIMITED_PERIOD ||
                incidentSlot.getTo() == UNLIMITED_PERIOD) {
            return false;
        }
        return incidentSlot.getFrom()>= newSlot.getFrom() &&
                incidentSlot.getTo() <= newSlot.getTo();
    }

    private static void addSlots(List<ShiftSlot> slotsToBeAdded) throws SQLException {

        LOGGER.info("adding slots " + slotsToBeAdded);

        for (ShiftSlot slot : slotsToBeAdded) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("peopleId", slot.getPeople().getId());
            payload.put("shiftId", slot.getShift().getId());
            payload.put("startTime", slot.getFrom());
            payload.put("endTime", slot.getTo());
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(getShiftPeopleRelPseudoModule().getTableName())
                    .fields(FieldFactory.getShiftPeopleRelPseudoModuleFields())
                    .addRecords(Collections.singletonList(payload));
            insertBuilder.save();
        }
    }

    private static void deleteSlots(List<ShiftSlot> slotsToBeDeleted) throws Exception {

        LOGGER.info("deleting slots " + slotsToBeDeleted);

        FacilioModule shiftRelMod = getShiftPeopleRelPseudoModule();
        List<Long> slotID = slotsToBeDeleted.stream()
                .map(s -> s.getId())
                .collect(Collectors.toList());
        String idCollectionString = StringUtils.join(slotID, ",");

        Condition idCondition = CriteriaAPI.getCondition("ID", "id", idCollectionString, NumberOperators.EQUALS);
        GenericDeleteRecordBuilder del = new GenericDeleteRecordBuilder()
                .table(shiftRelMod.getTableName())
                .andCondition(idCondition);
        del.delete();
    }

    private static ShiftSlot trimHalfContainedSlot(ShiftSlot incidentSlot, ShiftSlot newSlot) {

        if (incidentSlot.getTo() >= newSlot.getFrom()) {
            long updatesStartForIncidentSlot = incidentSlot.getFrom();
            long updatedEndForIncidentSlot = newSlot.getFrom() - DAY_IN_MILLIS;
            return new ShiftSlot(incidentSlot.getPeople(), incidentSlot.getShift(), updatesStartForIncidentSlot, updatedEndForIncidentSlot);
        }

        if (incidentSlot.getFrom() <= newSlot.getTo()) {
            long updatesStartForIncidentSlot = newSlot.getTo() + DAY_IN_MILLIS;
            long updatedEndForIncidentSlot = incidentSlot.getTo();
            return new ShiftSlot(incidentSlot.getPeople(), incidentSlot.getShift(), updatesStartForIncidentSlot, updatedEndForIncidentSlot);
        }

        return null;
    }

    private static List<ShiftSlot> getIncidentSlots(long peopleID, long from, long to) throws Exception {

        Collection<FacilioField> shiftPeopleFields = FieldFactory.getShiftPeopleRelPseudoModuleFields();
        Map<String, FacilioField> fieldMap =
                FieldFactory.getAsMap(shiftPeopleFields);
        // A. (PEOPLE_ID == $1)
        Condition peopleCondition = CriteriaAPI.getCondition(fieldMap.get("peopleId"),
                Collections.singletonList(peopleID),
                NumberOperators.EQUALS);

        // B. time span
        // (SHIFT_START >= $2)
        Condition shiftStartCondition = CriteriaAPI.getCondition(fieldMap.get("startTime"),
                Collections.singletonList(to),
                NumberOperators.LESS_THAN_EQUAL);
        // (SHIFT_START == -2)
        Condition shiftStartBoundlessCondition = CriteriaAPI.getCondition(fieldMap.get("startTime"),
                Collections.singletonList(UNLIMITED_PERIOD),
                NumberOperators.EQUALS);

        Criteria shiftStartCriteria = new Criteria();
        shiftStartCriteria.addOrCondition(shiftStartCondition);
        shiftStartCriteria.addOrCondition(shiftStartBoundlessCondition);

        // (SHIFT_END <= $3)
        Condition shiftEndCondition = CriteriaAPI.getCondition(fieldMap.get("endTime"),
                Collections.singletonList(from),
                NumberOperators.GREATER_THAN_EQUAL);

        // (SHIFT_END == -2)
        Condition shiftEndBoundlessCondition = CriteriaAPI.getCondition(fieldMap.get("endTime"),
                Collections.singletonList(UNLIMITED_PERIOD),
                NumberOperators.EQUALS);

        Criteria shiftEndCriteria = new Criteria();
        shiftEndCriteria.addOrCondition(shiftEndCondition);
        shiftEndCriteria.addOrCondition(shiftEndBoundlessCondition);

        // Complete Time Criteria
        Criteria completeTimeCriteria = new Criteria();
        completeTimeCriteria.andCriteria(shiftStartCriteria);
        completeTimeCriteria.andCriteria(shiftEndCriteria);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getShiftPeopleRelPseudoModule().getTableName())
                .select(shiftPeopleFields)
                .andCriteria(completeTimeCriteria)
                .andCondition(peopleCondition);

        List<Map<String, Object>> resultSet = builder.get();
        if (resultSet == null || resultSet.isEmpty()) {
            throw new Exception("result set cannot be empty");
        }
        return composeShiftSlotsFromResult(resultSet);
    }

    private static List<ShiftSlot> composeShiftSlotsFromResult(List<Map<String, Object>> resultSet) {
        List<ShiftSlot> slots = new ArrayList<>();
        for (Map<String, Object> rec: resultSet) {

            Long id = (Long) rec.get("id");

            V3EmployeeContext emp = new V3EmployeeContext();
            emp.setId((Long) rec.get("peopleId"));

            Shift shift = new Shift();
            shift.setId((Long) rec.get("shiftId"));

            Long from = (Long) rec.get("startTime");
            Long to = (Long) rec.get("endTime");

            slots.add(new ShiftSlot(id, emp, shift, from, to));
        }
        return slots;
    }

    private static List<ShiftSlot> breakDefaultSlot(ShiftSlot incidentSlot, ShiftSlot newSlot) {
        long start = newSlot.getFrom();
        long end = newSlot.getTo();
        List<ShiftSlot> slots = new ArrayList<>();

        // Compose left slot
        long leftStart = UNLIMITED_PERIOD;
        long leftEnd = start - DAY_IN_MILLIS;
        slots.add(new ShiftSlot(incidentSlot.getPeople(), incidentSlot.getShift(), leftStart, leftEnd));

        // Compose right slot
        long rightStart = end + DAY_IN_MILLIS;
        long rightEnd = UNLIMITED_PERIOD;
        slots.add(new ShiftSlot(incidentSlot.getPeople(), incidentSlot.getShift(), rightStart, rightEnd));
        return slots;
    }

    private static boolean isDefaultShiftSlot(ShiftSlot slot) {
        return slot.getFrom() == UNLIMITED_PERIOD && slot.getTo() == UNLIMITED_PERIOD;
    }


    public static Integer getAssociatedBreakCount(long shiftID) throws Exception {

        FacilioModule shiftBreakRelMod =
                Constants.getModBean().getModule(FacilioConstants.ContextNames.SHIFT_BREAK_REL);

        FacilioField countField =
                FieldFactory.getField("count", "COUNT(DISTINCT BREAK_ID)", FieldType.NUMBER);

        FacilioField shiftField =
                FieldFactory.getField("shiftId", "SHIFT_ID", FieldType.NUMBER);
        Condition shiftCond =
                CriteriaAPI.getCondition(shiftField, String.valueOf(shiftID), NumberOperators.EQUALS);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(shiftBreakRelMod.getTableName())
                .select(Collections.singletonList(countField))
                .andCondition(shiftCond);

        List<Map<String, Object>> resultSet = builder.get();
        if (resultSet == null) {
            throw new Exception("resultSet cannot be null");
        }
        return FacilioUtil.parseInt(resultSet.get(0).get("count"));
    }
}

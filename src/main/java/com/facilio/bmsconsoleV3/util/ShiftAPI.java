package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ShiftUserRelContext;
import com.facilio.bmsconsoleV3.context.Shift;
import com.facilio.constants.FacilioConstants;
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
import com.facilio.time.DateTimeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ShiftAPI {

    public static final long UNLIMITED_PERIOD = -2;
    final static long DAY_IN_MILLIS = 24 * 60 * 60 * 1000;


    public static Shift getShift(long id) throws Exception {
        List<Shift> shifts = getShifts(Collections.singletonList(id));
        return shifts == null || shifts.isEmpty() ? null : shifts.get(0);
    }

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

    public static Shift getPeopleShift(long peopleID) throws Exception {

        /*
            SELECT SHIFT_ID
            FROM Shift_People_Rel
            WHERE (PEOPLE_ID = {peopleID})
            AND ((SHIFT_START = -2 AND SHIFT_END = -2) OR (SHIFT_START <= {now} AND SHIFT_END >= {now}))
            ORDER BY ID LIMIT 1
        */

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getShiftPeopleRelModuleFields());

        // A. People Condition
        // PEOPLE_ID = $1
        Condition peopleCondition = CriteriaAPI.getCondition(fieldMap.get("peopleId"),
                Collections.singletonList(peopleID),
                NumberOperators.EQUALS);

        // B. Default Shift Condition
        // (SHIFT_START = -2)
        Condition defaultShiftStartCondition = CriteriaAPI.getCondition(fieldMap.get("startTime"),
                Collections.singletonList(UNLIMITED_PERIOD),
                NumberOperators.EQUALS);

        // (SHIFT_END = -2)
        Condition defaultShiftEndCondition = CriteriaAPI.getCondition(fieldMap.get("endTime"),
                Collections.singletonList(UNLIMITED_PERIOD),
                NumberOperators.EQUALS);

        // (SHIFT_START = -2) AND (SHIFT_END = -2)
        Criteria defaultShiftCondition = new Criteria();
        defaultShiftCondition.addAndCondition(defaultShiftStartCondition);
        defaultShiftCondition.addAndCondition(defaultShiftEndCondition);

        // C. Current Shift Condition
        // (SHIFT_START <= {now})
        long now = DateTime.now().getMillis();
        Condition shiftGtNow = CriteriaAPI.getCondition(fieldMap.get("startTime"),
                Collections.singletonList(now),
                NumberOperators.LESS_THAN_EQUAL);

        // (SHIFT_END >= {now})
        Condition shiftLtNow = CriteriaAPI.getCondition(fieldMap.get("endTime"),
                Collections.singletonList(now),
                NumberOperators.GREATER_THAN_EQUAL);

        // (SHIFT_START = -2) AND (SHIFT_END = -2)
        Criteria currShiftCriteria = new Criteria();
        currShiftCriteria.addAndCondition(shiftGtNow);
        currShiftCriteria.addAndCondition(shiftLtNow);

        // D. Composite Shift Criteria
        Criteria shiftTimeCriteria = new Criteria();
        defaultShiftCondition.orCriteria(currShiftCriteria);
        defaultShiftCondition.orCriteria(defaultShiftCondition);

        List<FacilioField> fields = FieldFactory.getShiftPeopleRelModuleFields();
        fields.add(FieldFactory.getField("shiftStart", "Shift.START_TIME", FieldType.NUMBER));
        fields.add(FieldFactory.getField("shiftEnd", "Shift.END_TIME", FieldType.NUMBER));
        fields.add(FieldFactory.getField("shiftName", "Shift.NAME", FieldType.STRING));

        FacilioModule shiftPeopleModule = ModuleFactory.getShiftPeopleRelModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(shiftPeopleModule.getTableName())
                .select(fields)
                .innerJoin("Shift")
                .on("Shift_People_Rel.SHIFT_ID = Shift.ID")
                .orderBy("Shift_People_Rel.ID")
                .andCondition(peopleCondition)
                .andCriteria(shiftTimeCriteria);

        List<Map<String, Object>> shifts = builder.get();
        if (shifts.isEmpty()) {
            throw new Exception("person should always have a shift, none found");
        }

        return FieldUtil.getAsBeanFromMap(shifts.get(0), Shift.class);
    }

    public static void assignDefaultShiftToPeople(long peopleID) throws Exception {
        Shift shift = getDefaultShift();
        updatePeopleShift(peopleID, shift.getId(), UNLIMITED_PERIOD, UNLIMITED_PERIOD);
    }

    public static void updatePeopleShift(long peopleID, long shiftID, long from, long to) throws Exception {

        Map<String, Object> rel = new HashMap<>();
        rel.put("peopleId", peopleID);
        rel.put("shiftId", shiftID);
        rel.put("startTime", from);
        rel.put("endTime", to);

        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getShiftPeopleRelModule().getTableName())
                .fields(FieldFactory.getShiftPeopleRelModuleFields())
                .addRecords(Collections.singletonList(rel));
        insertBuilder.save();
    }

    public static List<Map<String, Object>> getWeeklyShiftList(Long peopleID, Long timelineValue) {
        List<Map<String, Object>> shifts = new ArrayList<>();
        return shifts;
    }

    public static List<Map<String, Object>> getMonthlyShiftList(Long peopleID, Long timelineValue) throws Exception {

        Map<Integer, Integer> monthDaysCountEncoding = getMonthDaysCount();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(timelineValue));
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        DateTime rangeStart = DateTime.now().minusDays(dayOfMonth - 1);
        DateTime rangeEnd = rangeStart.plusDays(monthDaysCountEncoding.get(month) - 1);

        // People's shift updates along with shift information
        Pair<Map<String, Object>, List<Map<String, Object>>> userShifts =
                getPeopleShifts(peopleID, rangeStart, rangeEnd);
        Map<String, Object> defaultShiftMap = userShifts.getLeft();
        List<Map<String, Object>> updatedShiftsMap = userShifts.getRight();

        // applying default shift as base
        List<Map<String, Object>> shiftSchedule = new ArrayList<>();
        // day schedule is added to list and also a reference is stored in map
        // for applying update spreads
        Map<String, Map<String, Object>> shiftScheduleMap = new HashMap();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        for (long time = rangeStart.getMillis(); time <= rangeEnd.getMillis(); time += DAY_IN_MILLIS) {
            Map<String, Object> shift = new HashMap<>();
            String date = formatter.format(new Date(time));
            shift.put("date", date);
            shift.put("name", defaultShiftMap.get("shiftName"));
            shift.put("startTime", dayEpochToString(defaultShiftMap.get("shiftStart")));
            shift.put("endTime", dayEpochToString(defaultShiftMap.get("shiftEnd")));

            shiftSchedule.add(shift);
            shiftScheduleMap.put(date, shift);
        }

        // spreading updated shifts over default shift base
        for (Map<String, Object> updatedShift : updatedShiftsMap) {
            long modificationStart = (long) updatedShift.get("startTime");
            long modificationEnd = (long) updatedShift.get("endTime");

            for (long time = modificationStart; time <= modificationEnd; time += DAY_IN_MILLIS) {
                String date = formatter.format(new Date(time));
                Map<String, Object> shift = shiftScheduleMap.get(date);
                shift.put("name", updatedShift.get("shiftName"));
                shift.put("startTime", dayEpochToString(updatedShift.get("shiftStart")));
                shift.put("endTime", dayEpochToString(updatedShift.get("shiftEnd")));
            }
        }

        return shiftSchedule;
    }

    private static Map<Integer, Integer> getMonthDaysCount() {
        Map<Integer, Integer> monthDaysCountEncoding = new HashMap<>();
        monthDaysCountEncoding.put(1, 31);
        monthDaysCountEncoding.put(2, LocalDate.now().isLeapYear() ? 29 : 28);
        monthDaysCountEncoding.put(3, 31);
        monthDaysCountEncoding.put(4, 30);
        monthDaysCountEncoding.put(5, 31);
        monthDaysCountEncoding.put(6, 30);
        monthDaysCountEncoding.put(7, 31);
        monthDaysCountEncoding.put(8, 31);
        monthDaysCountEncoding.put(9, 30);
        monthDaysCountEncoding.put(10, 31);
        monthDaysCountEncoding.put(11, 30);
        monthDaysCountEncoding.put(12, 31);
        return monthDaysCountEncoding;
    }

    private static String dayEpochToString(Object time) {
        return LocalTime.ofSecondOfDay((Long) time).toString();
    }

    // Pair's left: default shift - can never be null
    // Pair's right: list of updated shifts - can be empty
    private static Pair<Map<String, Object>, List<Map<String, Object>>>
    getPeopleShifts(Long peopleID, DateTime rangeStart, DateTime rangeEnd) throws Exception {

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getShiftPeopleRelModuleFields());

        // A. People Condition
        // PEOPLE_ID = $1
        Condition peopleCondition = CriteriaAPI.getCondition(fieldMap.get("peopleId"),
                Collections.singletonList(peopleID),
                NumberOperators.EQUALS);

        // B. Updated Shifts Condition
        // (SHIFT_START >= $1)
        Condition shiftStartCondition = CriteriaAPI.getCondition(fieldMap.get("startTime"),
                Collections.singletonList(rangeStart.getMillis()),
                NumberOperators.GREATER_THAN_EQUAL);

        // (SHIFT_END <= $1)
        Condition shiftEndCondition = CriteriaAPI.getCondition(fieldMap.get("endTime"),
                Collections.singletonList(rangeEnd.getMillis()),
                NumberOperators.LESS_THAN_EQUAL);

        // (SHIFT_START >= $1) AND (SHIFT_END <= $1)
        Criteria updatedShiftsCondition = new Criteria();
        updatedShiftsCondition.addAndCondition(shiftStartCondition);
        updatedShiftsCondition.addAndCondition(shiftEndCondition);

        // C. Default Shift Condition
        // (SHIFT_START = -2)
        Condition defaultShiftStartCondition = CriteriaAPI.getCondition(fieldMap.get("startTime"),
                Collections.singletonList(UNLIMITED_PERIOD),
                NumberOperators.EQUALS);

        // (SHIFT_END = -2)
        Condition defaultShiftEndCondition = CriteriaAPI.getCondition(fieldMap.get("endTime"),
                Collections.singletonList(UNLIMITED_PERIOD),
                NumberOperators.EQUALS);

        // (SHIFT_START >= -2) AND (SHIFT_END >= -2)
        Criteria defaultShiftCondition = new Criteria();
        defaultShiftCondition.addAndCondition(defaultShiftStartCondition);
        defaultShiftCondition.addAndCondition(defaultShiftEndCondition);

        // D. Shifts Condition
        // ((SHIFT_START >= -2) AND (SHIFT_END >= -2)) OR ((SHIFT_START >= $1) AND (SHIFT_END <= $1))
        Criteria shiftsCondition = new Criteria();
        shiftsCondition.orCriteria(defaultShiftCondition);
        shiftsCondition.orCriteria(updatedShiftsCondition);

        List<FacilioField> fields = FieldFactory.getShiftPeopleRelModuleFields();
        fields.add(FieldFactory.getField("shiftStart", "Shift.START_TIME", FieldType.NUMBER));
        fields.add(FieldFactory.getField("shiftEnd", "Shift.END_TIME", FieldType.NUMBER));
        fields.add(FieldFactory.getField("shiftName", "Shift.NAME", FieldType.STRING));

        FacilioModule shiftPeopleModule = ModuleFactory.getShiftPeopleRelModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(shiftPeopleModule.getTableName())
                .select(fields)
                .innerJoin("Shift")
                .on("Shift_People_Rel.SHIFT_ID = Shift.ID")
                .orderBy("Shift_People_Rel.ID")
                .andCondition(peopleCondition)
                .andCriteria(shiftsCondition);

        List<Map<String, Object>> shifts = builder.get();
        if (shifts.isEmpty()) {
            throw new Exception("associated shifts can never be empty");
        }
        Map<String, Object> defaultShift = shifts.get(0);
        List<Map<String, Object>> updatedShifts = shifts.size() == 1 ?
                new ArrayList<>() : shifts.subList(1, shifts.size());
        return Pair.of(defaultShift, updatedShifts);
    }

    public static List<ShiftUserRelContext> getShiftUserMapping(long startTime, long endTime, long orgUserId, long shiftId, boolean alignDate) throws Exception {
        startTime = DateTimeUtil.getDayStartTimeOf(startTime);
        endTime = DateTimeUtil.getDayEndTimeOf(endTime);
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getShiftUserRelModule().getTableName())
                .select(FieldFactory.getShiftUserRelModuleFields())
                .orderBy("START_TIME");

        Criteria c = new Criteria();
        c.addAndCondition(CriteriaAPI.getCondition("END_TIME", "endTime", String.valueOf(startTime), NumberOperators.GREATER_THAN_EQUAL));
        c.addOrCondition(CriteriaAPI.getCondition("END_TIME", "endTime", String.valueOf(UNLIMITED_PERIOD), NumberOperators.EQUALS));
        builder.andCriteria(c);

        c = new Criteria();
        c.addAndCondition(CriteriaAPI.getCondition("START_TIME", "startTime", String.valueOf(endTime), NumberOperators.LESS_THAN_EQUAL));
        c.addOrCondition(CriteriaAPI.getCondition("START_TIME", "startTime", String.valueOf(UNLIMITED_PERIOD), NumberOperators.EQUALS));
        builder.andCriteria(c);

        if (orgUserId > 0) {
            builder.andCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserid", String.valueOf(orgUserId), NumberOperators.EQUALS));
        }
        if (shiftId > 0) {
            builder.andCondition(CriteriaAPI.getCondition("SHIFTID", "shiftId", String.valueOf(shiftId), NumberOperators.EQUALS));
        }

        List<Map<String, Object>> list = builder.get();
        List<ShiftUserRelContext> shiftUserMapping = FieldUtil.getAsBeanListFromMapList(list, ShiftUserRelContext.class);

        if (alignDate) {
            if (CollectionUtils.isNotEmpty(shiftUserMapping)) {
                for (ShiftUserRelContext rel : shiftUserMapping) {
                    if (rel.getStartTime() == com.facilio.bmsconsole.util.ShiftAPI.UNLIMITED_PERIOD || rel.getStartTime() < startTime) {
                        rel.setStartTime(startTime);
                    }

                    if (rel.getEndTime() == com.facilio.bmsconsole.util.ShiftAPI.UNLIMITED_PERIOD || rel.getEndTime() > endTime) {
                        rel.setEndTime(endTime);
                    }
                }
            }
        }
        return shiftUserMapping;
    }

}

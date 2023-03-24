package com.facilio.bmsconsoleV3.commands.pmImport;

//
//import com.facilio.bmsconsole.context.PMTriggerV2;
//import com.facilio.modules.FieldUtil;
//import com.facilio.taskengine.ScheduleInfo;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.junit.jupiter.api.Test;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class HandleResourcePlannerImportCommandTest {
//
//    @Test
//    void triggerWithNullTimes() {
//        Map<String, Object> pm = new HashMap<>();
//        Exception ex = assertThrows(
//                Exception.class,
//                () -> Util.createTrigger(-1L, PMTriggerV2.PMTriggerFrequency.DAILY, pm),
//                "Expected exception not thrown"
//        );
//        assertEquals("times cannot be null", ex.getMessage());
//    }
//
//    @Test
//    void dailyTrigger() throws Exception {
//        Map<String, Object> pm = new HashMap<>();
//        pm.put("trTimes", "09:00, 12:00");
//
//        PMTriggerV2 trigger = Util.createTrigger(-1L, PMTriggerV2.PMTriggerFrequency.DAILY, pm);
//
//        JSONParser parser = new JSONParser();
//        JSONObject json = (JSONObject) parser.parse(trigger.getSchedule());
//        ScheduleInfo schedule = FieldUtil.getAsBeanFromJson(json, ScheduleInfo.class);
//
//        assertEquals(schedule.getTimes(), Arrays.asList("09:00", "12:00"));
//        assertEquals(ScheduleInfo.FrequencyType.DAILY, schedule.getFrequencyTypeEnum());
//    }
//
//    @Test
//    void weeklyTrigger() throws Exception {
//        Map<String, Object> pm = new HashMap<>();
//        pm.put("trTimes", "09:00, 12:00");
//        pm.put("trDays", "MON, WED");
//
//        PMTriggerV2 trigger = Util.createTrigger(-1L, PMTriggerV2.PMTriggerFrequency.WEEKLY, pm);
//
//        JSONParser parser = new JSONParser();
//        JSONObject json = (JSONObject) parser.parse(trigger.getSchedule());
//        ScheduleInfo schedule = FieldUtil.getAsBeanFromJson(json, ScheduleInfo.class);
//
//        assertEquals(schedule.getTimes(), Arrays.asList("09:00", "12:00"));
//        assertEquals(schedule.getValues(), Arrays.asList(1, 3));
//        assertEquals(ScheduleInfo.FrequencyType.WEEKLY, schedule.getFrequencyTypeEnum());
//    }
//
//    @Test
//    void monthlyDays() throws Exception {
//        Map<String, Object> pm = new HashMap<>();
//        pm.put("trTimes", "09:00, 12:00");
//        pm.put("trDates", "1, 5");
//
//        PMTriggerV2 trigger = Util.createTrigger(-1L, PMTriggerV2.PMTriggerFrequency.MONTHLY, pm);
//
//        JSONParser parser = new JSONParser();
//        JSONObject json = (JSONObject) parser.parse(trigger.getSchedule());
//        ScheduleInfo schedule = FieldUtil.getAsBeanFromJson(json, ScheduleInfo.class);
//
//        assertEquals(schedule.getTimes(), Arrays.asList("09:00", "12:00"));
//        assertEquals(schedule.getValues(), Arrays.asList(1, 5));
//        assertEquals(ScheduleInfo.FrequencyType.MONTHLY_DAY, schedule.getFrequencyTypeEnum());
//    }
//
//    @Test
//    void monthlyWeek() throws Exception {
//        Map<String, Object> pm = new HashMap<>();
//        pm.put("trTimes", "09:00, 12:00");
//        pm.put("trWeeks", "2");
//        pm.put("trDays", "MON, WED");
//
//
//        PMTriggerV2 trigger = Util.createTrigger(-1L, PMTriggerV2.PMTriggerFrequency.MONTHLY, pm);
//
//        JSONParser parser = new JSONParser();
//        JSONObject json = (JSONObject) parser.parse(trigger.getSchedule());
//        ScheduleInfo schedule = FieldUtil.getAsBeanFromJson(json, ScheduleInfo.class);
//
//        assertEquals(schedule.getTimes(), Arrays.asList("09:00", "12:00"));
//        assertEquals(schedule.getValues(), Arrays.asList(1, 3));
//        assertEquals(schedule.getWeekFrequency(), 2);
//        assertEquals(ScheduleInfo.FrequencyType.MONTHLY_WEEK, schedule.getFrequencyTypeEnum());
//    }
//
//    @Test
//    void weekAsCollection() throws Exception {
//        Map<String, Object> pm = new HashMap<>();
//        pm.put("trTimes", "09:00, 12:00");
//        pm.put("trWeeks", "2, 3");
//        pm.put("trDays", "MON, WED");
//
//        Exception ex = assertThrows(
//                Exception.class,
//                () -> Util.createTrigger(-1L, PMTriggerV2.PMTriggerFrequency.MONTHLY, pm),
//                "Expected exception not thrown"
//        );
//        assertEquals("week has to be a number", ex.getMessage());
//    }
//
//    @Test
//    void triggerBothDaysAndDates() throws Exception {
//        Map<String, Object> pm = new HashMap<>();
//        pm.put("trTimes", "09:00, 12:00");
//        pm.put("trDates", "1, 5");
//        pm.put("trDays", "MON, WED");
//
//        Exception ex = assertThrows(
//                Exception.class,
//                () -> Util.createTrigger(-1L, PMTriggerV2.PMTriggerFrequency.MONTHLY, pm),
//                "Expected exception not thrown"
//        );
//        assertEquals("trigger cannot have both days and dates", ex.getMessage());
//    }
//
//    @Test
//    void quarterlyDays() throws Exception {
//        Map<String, Object> pm = new HashMap<>();
//        pm.put("trTimes", "09:00, 12:00");
//        pm.put("trDates", "1, 5");
//
//        PMTriggerV2 trigger = Util.createTrigger(-1L, PMTriggerV2.PMTriggerFrequency.QUARTERLY, pm);
//
//        JSONParser parser = new JSONParser();
//        JSONObject json = (JSONObject) parser.parse(trigger.getSchedule());
//        ScheduleInfo schedule = FieldUtil.getAsBeanFromJson(json, ScheduleInfo.class);
//
//        assertEquals(schedule.getTimes(), Arrays.asList("09:00", "12:00"));
//        assertEquals(schedule.getValues(), Arrays.asList(1, 5));
//        assertEquals(schedule.getMonthValue(), 1);
//        assertEquals(ScheduleInfo.FrequencyType.QUARTERLY_DAY, schedule.getFrequencyTypeEnum());
//
//    }
//
//    @Test
//    void quarterlyWeek() throws Exception {
//        Map<String, Object> pm = new HashMap<>();
//        pm.put("trTimes", "09:00, 12:00");
//        pm.put("trWeeks", "2");
//        pm.put("trDays", "MON, WED");
//
//
//        PMTriggerV2 trigger = Util.createTrigger(-1L, PMTriggerV2.PMTriggerFrequency.QUARTERLY, pm);
//
//        JSONParser parser = new JSONParser();
//        JSONObject json = (JSONObject) parser.parse(trigger.getSchedule());
//        ScheduleInfo schedule = FieldUtil.getAsBeanFromJson(json, ScheduleInfo.class);
//
//        assertEquals(schedule.getTimes(), Arrays.asList("09:00", "12:00"));
//        assertEquals(schedule.getValues(), Arrays.asList(1, 3));
//        assertEquals(schedule.getWeekFrequency(), 2);
//        assertEquals(schedule.getMonthValue(), 1);
//        assertEquals(ScheduleInfo.FrequencyType.QUARTERLY_WEEK, schedule.getFrequencyTypeEnum());
//    }
//
//    @Test
//    void halfYearlyDays() throws Exception {
//        Map<String, Object> pm = new HashMap<>();
//        pm.put("trTimes", "09:00, 12:00");
//        pm.put("trDates", "1, 5");
//
//        PMTriggerV2 trigger = Util.createTrigger(-1L, PMTriggerV2.PMTriggerFrequency.HALF_YEARLY, pm);
//
//        JSONParser parser = new JSONParser();
//        JSONObject json = (JSONObject) parser.parse(trigger.getSchedule());
//        ScheduleInfo schedule = FieldUtil.getAsBeanFromJson(json, ScheduleInfo.class);
//
//        assertEquals(schedule.getTimes(), Arrays.asList("09:00", "12:00"));
//        assertEquals(schedule.getValues(), Arrays.asList(1, 5));
//        assertEquals(schedule.getMonthValue(), 1);
//        assertEquals(ScheduleInfo.FrequencyType.HALF_YEARLY_DAY, schedule.getFrequencyTypeEnum());
//
//    }
//
//    @Test
//    void halfYearlyWeek() throws Exception {
//        Map<String, Object> pm = new HashMap<>();
//        pm.put("trTimes", "09:00, 12:00");
//        pm.put("trWeeks", "2");
//        pm.put("trDays", "MON, WED");
//
//        PMTriggerV2 trigger = Util.createTrigger(-1L, PMTriggerV2.PMTriggerFrequency.HALF_YEARLY, pm);
//
//        JSONParser parser = new JSONParser();
//        JSONObject json = (JSONObject) parser.parse(trigger.getSchedule());
//        ScheduleInfo schedule = FieldUtil.getAsBeanFromJson(json, ScheduleInfo.class);
//
//        assertEquals(schedule.getTimes(), Arrays.asList("09:00", "12:00"));
//        assertEquals(schedule.getValues(), Arrays.asList(1, 3));
//        assertEquals(schedule.getWeekFrequency(), 2);
//        assertEquals(schedule.getMonthValue(), 1);
//        assertEquals(ScheduleInfo.FrequencyType.HALF_YEARLY_WEEK, schedule.getFrequencyTypeEnum());
//    }
//
//    @Test
//    void annuallyDays() throws Exception {
//        Map<String, Object> pm = new HashMap<>();
//        pm.put("trTimes", "09:00, 12:00");
//        pm.put("trDates", "5");
//        pm.put("trMonths", "JAN, JUN");
//
//        PMTriggerV2 trigger = Util.createTrigger(-1L, PMTriggerV2.PMTriggerFrequency.ANNUALLY, pm);
//
//        JSONParser parser = new JSONParser();
//        JSONObject json = (JSONObject) parser.parse(trigger.getSchedule());
//        ScheduleInfo schedule = FieldUtil.getAsBeanFromJson(json, ScheduleInfo.class);
//
//        assertEquals(schedule.getTimes(), Arrays.asList("09:00", "12:00"));
//        assertEquals(schedule.getValues(), Arrays.asList(1, 6));
//        assertEquals(schedule.getYearlyDayValue(), 5);
//        assertEquals(ScheduleInfo.FrequencyType.YEARLY, schedule.getFrequencyTypeEnum());
//    }
//
//    @Test
//    void annuallyWithoutMonths() throws Exception {
//        Map<String, Object> pm = new HashMap<>();
//        pm.put("trTimes", "09:00, 12:00");
//        pm.put("trDates", "5");
//
//        Exception ex = assertThrows(
//                Exception.class,
//                () -> Util.createTrigger(-1L, PMTriggerV2.PMTriggerFrequency.ANNUALLY, pm),
//                "Expected exception not thrown"
//        );
//        assertEquals("months cannot be empty for annual triggers", ex.getMessage());
//    }
//
//    @Test
//    void annuallyWithMultipleDates() throws Exception {
//        Map<String, Object> pm = new HashMap<>();
//        pm.put("trTimes", "09:00, 12:00");
//        pm.put("trDates", "5, 10");
//        pm.put("trMonths", "JAN, JUN");
//
//        Exception ex = assertThrows(
//                Exception.class,
//                () -> Util.createTrigger(-1L, PMTriggerV2.PMTriggerFrequency.ANNUALLY, pm),
//                "Expected exception not thrown"
//        );
//        assertEquals("Yearly day trigger accepts a single day", ex.getMessage());
//    }
//
//    @Test
//    void annuallyWeek() throws Exception {
//        Map<String, Object> pm = new HashMap<>();
//        pm.put("trTimes", "09:00, 12:00");
//        pm.put("trWeeks", "4");
//        pm.put("trDays", "MON, WED");
//        pm.put("trMonths", "JAN, JUN");
//
//        PMTriggerV2 trigger = Util.createTrigger(-1L, PMTriggerV2.PMTriggerFrequency.ANNUALLY, pm);
//
//        JSONParser parser = new JSONParser();
//        JSONObject json = (JSONObject) parser.parse(trigger.getSchedule());
//        ScheduleInfo schedule = FieldUtil.getAsBeanFromJson(json, ScheduleInfo.class);
//
//        assertEquals(schedule.getTimes(), Arrays.asList("09:00", "12:00"));
//        assertEquals(schedule.getValues(), Arrays.asList(1, 6));
//        assertEquals(schedule.getYearlyDayOfWeekValues(), Arrays.asList(1, 3));
//        assertEquals(schedule.getWeekFrequency(), 4);
//        assertEquals(ScheduleInfo.FrequencyType.YEARLY_WEEK, schedule.getFrequencyTypeEnum());
//    }
//}
package com.facilio.bmsconsoleV3.commands.pmImport;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMJobPlan;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PMTriggerV2;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.Strings;

import java.util.*;
import java.util.stream.Collectors;

public class Util {

    public static Map<Long, List<Map<String, Object>>> classifyByPM(List<Map<String, Object>> records) throws Exception {
        Map<Long, List<Map<String, Object>>> classification = new HashMap<>();
        for (Map<String, Object> rec : records) {

            Map<String, Object> pmObj = (Map<String, Object>) rec.get("pmId");
            if (pmObj == null) {
                throw new Exception("pm cannot be null");
            }
            Long pmID = FacilioUtil.parseLong(pmObj.get("id"));

            if (classification.containsKey(pmID)) {
                classification.get(pmID).add(rec);
            } else {
                List<Map<String, Object>> newBin = new ArrayList<>();
                newBin.add(rec);
                classification.put(pmID, newBin);
            }
        }
        return classification;
    }

    public static Object createRecord(String moduleName, Object data) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule plannerModule = modBean.getModule(moduleName);

        FacilioContext ctx = V3Util.createRecord(plannerModule, FieldUtil.getAsProperties(data));

        Map<String, Object> recordMap = (Map<String, Object>) ctx.get("recordMap");
        List<PMPlanner> triggerList = (List<PMPlanner>) recordMap.get(moduleName);
        return triggerList.get(0);
    }

    private static String[] explode(String value) {
        String[] explodedValues = StringUtils.split(value, ",");
        return StringUtils.stripAll(explodedValues);
    }

    private static String trimAndUppercase(String value) {
        return Strings.toUpperCase(StringUtils.strip(value));
    }

    private static List<String> explodeAndCapitalizeByComma(String value) {
        String[] explodedValues = explode(value);
        return Arrays.stream(explodedValues)
                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }

    private static List<Integer> explodeAndConvToIntByComma(String value) {
        String[] explodedValues = explode(value);
        return Arrays.stream(explodedValues)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }


    private static List<Integer> encodeDays(List<String> days) {
        Map<String, Integer> table = new HashMap<>();
        int counter = 1;
        table.put("MON", counter);
        table.put("TUE", ++counter);
        table.put("WED", ++counter);
        table.put("THU", ++counter);
        table.put("FRI", ++counter);
        table.put("SAT", ++counter);
        table.put("SUN", ++counter);
        return encode(table, days);
    }

    private static List<Integer> encodeMonths(List<String> months) {
        Map<String, Integer> table = new HashMap<>();
        int counter = 1;
        table.put("JAN", counter);
        table.put("FEB", ++counter);
        table.put("MAR", ++counter);
        table.put("APR", ++counter);
        table.put("MAY", ++counter);
        table.put("JUN", ++counter);
        table.put("AUG", ++counter);
        table.put("SEP", ++counter);
        table.put("OCT", ++counter);
        table.put("NOV", ++counter);
        table.put("DEC", ++counter);
        return encode(table, months);
    }

    private static List<Integer> encode(Map<String, Integer> table, List<String> data) {
        return data.stream()
                .map(table::get)
                .collect(Collectors.toList());
    }

    // not to be confused with ScheduleInfo.FrequencyType.WEEKLY
    // triggersWeekly represents MONTHLY_WEEK and its *_WEEK counterparts
    private static Boolean triggersWeekly(Map<String, Object> pm) {
        return pm.get("trWeeks") != null;
    }

    public static PMTriggerV2 createTrigger(Long pmID, PMTriggerV2.PMTriggerFrequency triggerFreq, Map<String, Object> pm) throws Exception {
        PMTriggerV2 trigger = new PMTriggerV2();
        trigger.setPmId(pmID);
        trigger.setFrequencyEnum(triggerFreq);

        ScheduleInfo schedule = new ScheduleInfo();

        Object timesObj = pm.get("trTimes");
        if (timesObj == null) {
            throw new Exception("times cannot be null");
        }

        Object daysObj = pm.get("trDays"); // MON - FRI
        Object datesObj = pm.get("trDates"); // 1 - 31
        Object weekObj = pm.get("trWeeks"); // 1 - 5
        Object monthsObj = pm.get("trMonths"); // JAN - DEC

        // validation for week count
        if (weekObj != null && !FacilioUtil.isNumeric((String) weekObj)) {
            throw new Exception("week has to be a number");
        }
        int weekIndex = weekObj != null ? FacilioUtil.parseInt(weekObj) : -1;

        // validation for days and dates
        if (!FacilioUtil.isEmptyOrNull(datesObj) && !FacilioUtil.isEmptyOrNull(daysObj)) {
            throw new Exception("trigger cannot have both days and dates");
        }

        boolean triggersWeekly = triggersWeekly(pm);

        schedule.setTimes(explodeAndCapitalizeByComma((String) timesObj));

        switch (triggerFreq) {
            case DAILY:
                schedule.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);
                break;
            case WEEKLY:
                schedule.setFrequencyType(ScheduleInfo.FrequencyType.WEEKLY);
                schedule.setValues(encodeDays(explodeAndCapitalizeByComma((String) daysObj)));
                break;
            case MONTHLY:
                if (triggersWeekly) {
                    schedule.setFrequencyType(ScheduleInfo.FrequencyType.MONTHLY_WEEK);
                    schedule.setValues(encodeDays(explodeAndCapitalizeByComma((String) daysObj)));
                    schedule.setWeekFrequency(weekIndex);
                } else {
                    schedule.setFrequencyType(ScheduleInfo.FrequencyType.MONTHLY_DAY);
                    schedule.setValues(explodeAndConvToIntByComma((String) datesObj));
                }
                break;
            case QUARTERLY:
                schedule.setMonthValue(1); // supporting only Jan - Apr - Jul - Oct
                if (triggersWeekly) {
                    schedule.setFrequencyType(ScheduleInfo.FrequencyType.QUARTERLY_WEEK);
                    schedule.setValues(encodeDays(explodeAndCapitalizeByComma((String) daysObj)));
                    schedule.setWeekFrequency(weekIndex);
                } else {
                    schedule.setFrequencyType(ScheduleInfo.FrequencyType.QUARTERLY_DAY);
                    schedule.setValues(explodeAndConvToIntByComma((String) datesObj));
                }
                break;
            case HALF_YEARLY:
                schedule.setMonthValue(1); // supporting only Jan - Jun
                if (triggersWeekly) {
                    schedule.setFrequencyType(ScheduleInfo.FrequencyType.HALF_YEARLY_WEEK);
                    schedule.setValues(encodeDays(explodeAndCapitalizeByComma((String) daysObj)));
                    schedule.setWeekFrequency(weekIndex);
                } else {
                    schedule.setFrequencyType(ScheduleInfo.FrequencyType.HALF_YEARLY_DAY);
                    schedule.setValues(explodeAndConvToIntByComma((String) datesObj));
                }
                break;
            case ANNUALLY:
                if (FacilioUtil.isEmptyOrNull(monthsObj)) {
                    throw new Exception("months cannot be empty for annual triggers");
                }
                schedule.setValues(encodeMonths(explodeAndCapitalizeByComma((String) monthsObj)));
                if (triggersWeekly) {
                    schedule.setFrequencyType(ScheduleInfo.FrequencyType.YEARLY_WEEK);
                    schedule.setYearlyDayOfWeekValues(encodeDays(explodeAndCapitalizeByComma((String) daysObj)));
                    schedule.setWeekFrequency(weekIndex);
                } else {
                    schedule.setFrequencyType(ScheduleInfo.FrequencyType.YEARLY);
                    List<Integer> days = explodeAndConvToIntByComma((String) datesObj);
                    if (days.size() > 1) {
                        throw new Exception("Yearly day trigger accepts a single day");
                    }
                    schedule.setYearlyDayValue(days.get(0));
                }
                break;
            default:
                throw new Exception("Unknown Trigger Type");
        }

        trigger.setType(PMTriggerV2.PMTriggerType.SCHEDULE.getIndex());
        trigger.setSchedule(FieldUtil.getAsJSON(schedule).toJSONString());
        return trigger;
    }

    public static Object persistModuleRecord(String moduleName, Object context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule plannerModule = modBean.getModule(moduleName);

        FacilioContext ctx = V3Util.createRecord(plannerModule, FieldUtil.getAsProperties(context));

        Map<String, Object> recordMap = (Map<String, Object>) ctx.get("recordMap");
        List<PMPlanner> triggerList = (List<PMPlanner>) recordMap.get(moduleName);
        return triggerList.get(0);
    }


    public static JobPlanTasksContext createJobPlanTask(PMJobPlan jobPlan,
                                                        JobPlanTaskSectionContext section,
                                                        Map<String, Object> rec) {

        JobPlanTasksContext jpTask = new JobPlanTasksContext();
        jpTask.setTaskSection(section);
        jpTask.setJobPlan(jobPlan);
        jpTask.setStatusNew(V3TaskContext.TaskStatus.OPEN.getValue());
        jpTask.setCreatedBy(AccountUtil.getCurrentUser());
        jpTask.setCreatedTime(System.currentTimeMillis());

        Object subjectObj = rec.get("jpTaskSubject");
        if (subjectObj != null) {
            jpTask.setSubject((String) subjectObj);
        }

        Object descriptionObj = rec.get("jpTaskDescription");
        if (descriptionObj != null) {
            jpTask.setDescription((String) descriptionObj);
        }

        // input type
        jpTask.setInputType(getTaskInputType(rec.get("jpInputType")));

        // default value
        Object defaultValueObj = rec.get("jpDefaultValue");
        String defaultValue = defaultValueObj == null ? "" : (String) defaultValueObj;
        jpTask.addAdditionInfo("defaultValue", defaultValue);

        // remarks
        Object remarksRequired = rec.get("jpRemarksRequired");
        Boolean remarksRequiredValue = remarksRequired != null;
        jpTask.addAdditionInfo("remarksRequired", remarksRequiredValue);

        Object remarkOptionValuesObj = rec.get("jpRemarkOptionValues");
        if (remarkOptionValuesObj == null) {
            jpTask.addAdditionInfo("remarkOptionValues", new String[]{});
        } else {
            String[] remarkOptionValues = explode((String) remarkOptionValuesObj);
            jpTask.addAdditionInfo("remarkOptionValues", remarkOptionValues);
        }

        return jpTask;
    }

    private static Integer getTaskInputType(Object inputType) {
        if (inputType == null) {
            return V3TaskContext.InputType.NONE.getVal();
        }
        String inputTypeStr = (String) inputType;
        inputTypeStr = trimAndUppercase(inputTypeStr);
        switch (inputTypeStr) {
            case "TEXT":
                return V3TaskContext.InputType.TEXT.getVal();
            case "RADIO":
                return V3TaskContext.InputType.RADIO.getVal();
            case "NUMBER":
                return V3TaskContext.InputType.NUMBER.getVal();
        }
        return V3TaskContext.InputType.NONE.getVal();
    }
}

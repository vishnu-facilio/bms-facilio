package com.facilio.bmsconsoleV3.commands.plannedmaintenance;

import bsh.StringUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.bmsconsole.context.PMTriggerV2;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.dataFetchers.Wateruos;
import com.facilio.date.calenderandclock.CalenderAndClockContext;
import com.facilio.date.calenderandclock.MonthEnum;
import com.facilio.date.calenderandclock.WeekDayEnum;
import com.facilio.date.calenderandclock.WeekEnum;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.taskengine.Season;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class constructScheduleInfo extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
        HashMap<String,String> fieldMapping = importProcessContext.getFieldMapping();
        List<Map<String, Object>> allRows = ImportAPI.getValidatedRows(importProcessContext.getId());
        List<PMTriggerV2> pmTriggerV2List = new ArrayList<>();
        for(Map<String,Object> row : allRows) {
            String rowContextString = String.valueOf(row.get("rowContextString"));
            JSONArray jsonArr = new JSONArray(rowContextString);
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObject = jsonArr.getJSONObject(i);
                JSONObject colval = new JSONObject();
                if (jsonObject.length() < 1) {
                    return true;
                } else if (jsonObject.isNull("colVal")) {
                    return true;
                }
                else{
                    int weekFrequency = -1;
                    int frequencyType = 0;
                    List<String> times = new ArrayList<>();
                    int runsEvery = 1;
                    List<Integer> values = new ArrayList<>();
                    int skipEvery = -1;
                    Integer yearlyDayValue = null;
                    int monthValue = -1;
                    long endDate = 0;
                    long startDate = System.currentTimeMillis();
                    List<Integer> yearlyDayOfWeekValue = new ArrayList<>();
                    List<Season> seasons = new ArrayList<>();

                    colval = jsonObject.getJSONObject("colVal");
                    if(colval.isNull("Trigger") || !fieldMapping.containsValue("Trigger")){
                        return true;
                    }
                    PMTriggerV2.PMTriggerFrequency pmTriggerFrequency = PMTriggerV2.PMTriggerFrequency.valueOf(colval.getString("Trigger"));
                    if(!colval.isNull("Week Frequency") && fieldMapping.containsValue("Week Frequency")){
                        weekFrequency = WeekEnum.valueOf(colval.getString("Week Frequency")).getIntValue(null);
                    }
                    frequencyType = setFrequencyType(pmTriggerFrequency,weekFrequency);
                    if(!colval.isNull("Times") && fieldMapping.containsValue("Times")){
                        times = getTimesList(colval.getString("Times"));
                    }
                    if(!colval.isNull("Runs Every") && fieldMapping.containsValue("Runs Every")){
                        runsEvery = colval.getInt("Runs Every");
                    }
                    if(!colval.isNull("Values") && fieldMapping.containsValue("Values")){
                        if(pmTriggerFrequency == PMTriggerV2.PMTriggerFrequency.ANNUALLY){
                            values = getMonthsValueList(colval.getString("Values"));
                        }
                        else if(frequencyType > 2 && weekFrequency == -1){
                           values = getDateValueList(colval.getString("Values"));
                        }
                        else if(frequencyType > 1 && (weekFrequency != -1 || pmTriggerFrequency == PMTriggerV2.PMTriggerFrequency.WEEKLY)){
                            values = getWeekDaysValueList(colval.getString("Values"));
                        }
                    }
                    if(!colval.isNull("Skip Every") && fieldMapping.containsValue("Skip Every")){
                        skipEvery = colval.getInt("Skip Every");
                    }
                    if(!colval.isNull("Yearly Day Value") && fieldMapping.containsValue("Yearly Day Value")){
                        yearlyDayValue = colval.getInt("Yearly Day Value");
                    }
                    if(!colval.isNull("Month Value") && fieldMapping.containsValue("Month Value")){
                        monthValue = MonthEnum.valueOf(colval.getString("Month Value")).getIntValue(null)+1;
                    }
                    if(!colval.isNull("Yearly Day Of Week Values") && fieldMapping.containsValue("Yearly Day Of Week Values")){
                        yearlyDayOfWeekValue = getWeekDaysValueList(colval.getString("Yearly Day Of Week Values"));
                    }
                    if(!colval.isNull("End Date") && fieldMapping.containsValue("End Date")){
                        endDate = convertDateStringToMilliSeconds(colval.getString("End Date"));
                    }
                    if(!colval.isNull("Start Date") && fieldMapping.containsValue("Start Date")){
                        startDate = convertDateStringToMilliSeconds(colval.getString("Start Date"));
                    }
                    if(!colval.isNull("Season") && fieldMapping.containsValue("Season")){
                        String[] seasonsArray = StringUtil.split(colval.getString("Season"),";");
                        if(seasonsArray.length > 5) {
                            return true;
                        }
                        if(seasonsArray.length > 0) {
                            for(String singleSeason : seasonsArray) {
                                String[] singleSeasonProp = StringUtil.split(singleSeason,":");
                                if (singleSeasonProp.length == 3) {
                                    String seasonName = singleSeasonProp[0];
                                    Integer seasonStartMonth = getMonthValue(singleSeasonProp[1].split("/")[1]);
                                    Integer seasonStartDay = Integer.parseInt(singleSeasonProp[1].split("/")[0]);
                                    Integer seasonEndMonth = getMonthValue(singleSeasonProp[2].split("/")[1]);
                                    Integer seasonEndDay = Integer.parseInt(singleSeasonProp[2].split("/")[0]);
                                    Season season = new Season();
                                    season.setName(seasonName);
                                    season.setStartMonth(seasonStartMonth)                    ;
                                    season.setStartDate(seasonStartDay);
                                    season.setEndMonth(seasonEndMonth);
                                    season.setEndDate(seasonEndDay);
                                    seasons.add(season);
                                }
                            }
                        }                    }
                    Map<String,Object> scheduleInfo = new HashMap<>();
                    scheduleInfo.put("times",times);
                    scheduleInfo.put("frequency",runsEvery);
                    scheduleInfo.put("skipEvery",skipEvery);
                    scheduleInfo.put("values",values);
                    scheduleInfo.put("frequencyType",frequencyType);
                    scheduleInfo.put("weekFrequency",weekFrequency);
                    scheduleInfo.put("yearlyDayValue",yearlyDayValue);
                    scheduleInfo.put("monthValue",monthValue);
                    scheduleInfo.put("yearlyDayOfWeekValues",yearlyDayOfWeekValue);
                    scheduleInfo.put("endDate",endDate);
                    if(CollectionUtils.isNotEmpty(seasons)) {
                        scheduleInfo.put("seasons",seasons);
                    }
                    colval.put("scheduleInfo",scheduleInfo);
                    PMTriggerV2 triggerV2 = new PMTriggerV2();
                    triggerV2.setSchedule(String.valueOf(colval.getJSONObject("scheduleInfo")));
                    if(endDate > 0) {
                        triggerV2.setEndTime(endDate);
                    }
                    triggerV2.setStartTime(startDate);
                    triggerV2.setFrequencyEnum(pmTriggerFrequency);
                    triggerV2.setType(1);
                    triggerV2.setFrequency(frequencyType);
                    triggerV2.setPmId(colval.getLong("PmId"));
                    List<ModuleBaseWithCustomFields> createTriggerRecords = new ArrayList<>();
                    createTriggerRecords.add(triggerV2);
                    ModuleBean modbean = Constants.getModBean();
                    FacilioModule pmTriggerV2Module = modbean.getModule(FacilioConstants.PM_V2.PM_V2_TRIGGER);
                    V3Util.createRecord(pmTriggerV2Module, createTriggerRecords);
                    pmTriggerV2List.add(triggerV2);
                }

            }
        }
        context.put("trigger", pmTriggerV2List);
        return false;
    }

    private static int getMonthValue(String month) {
        if(FacilioConstants.ContextNames.MONTH_LIST.contains(month)) {
            return (FacilioConstants.ContextNames.MONTH_LIST.indexOf(month) + 1);
        }
        return 1;
    }
    private static int setFrequencyType(PMTriggerV2.PMTriggerFrequency frequency, int weekFrequency){
        switch (frequency){
            case DAILY:
                return 1;
            case WEEKLY:
                 return 2;
            case MONTHLY:
                if(weekFrequency == -1){
                    return 3;
                }
                else{
                    return 4;
                }
            case ANNUALLY:
                if(weekFrequency == -1){
                    return 5;
                }
                else{
                    return 6;
                }
            case QUARTERLY:
                if(weekFrequency == -1){
                    return 7;
                }
                else{
                    return 8;
                }
            case HALF_YEARLY:
                if(weekFrequency == -1){
                    return 9;
                }
                else {
                    return 10;
                }
            default:
                return 0;
        }
    }
    private static List<String> getTimesList(String times){
        List<String> timesList = Arrays.asList(times.split(","));
        return  timesList;
    }
    private List<Integer> getWeekDaysValueList(String values){
        List<String> valueItemList = Arrays.asList(values.split(","));
        List<Integer> valueList = new ArrayList<>();
        for(String valueItem : valueItemList){
            int item = WeekDayEnum.valueOf(valueItem).getValue()-1;
            if(item == 0){
                item = 7;
            }
            valueList.add(item);
        }
        return valueList;
    }
    private List<Integer> getMonthsValueList(String values) throws Exception {
        List<String> valueItemList = Arrays.asList(values.split(","));
        List<Integer> valueList = new ArrayList<>();
        for(String valueItem : valueItemList){
            valueList.add(MonthEnum.valueOf(valueItem).getIntValue(null)+1);
        }
        return valueList;
    }
    private List<Integer> getDateValueList(String values){
        List<String> valueItemList = Arrays.asList(values.split(","));
        List<Integer> valueList = new ArrayList<>();
        for(String valueItem : valueItemList){
            valueList.add(Integer.parseInt(valueItem));
        }
        return valueList;
    }
    private long convertDateStringToMilliSeconds(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date dt = formatter.parse(date);
        return dt.getTime();
    }


}

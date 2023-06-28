package com.facilio.v3.commands;

import com.facilio.bmsconsole.calendarview.CalendarViewRequestContext;
import com.facilio.bmsconsole.calendarview.CalendarViewContext;
import com.facilio.timeline.context.CustomizationDataContext;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class ConstructCalendarViewResponseCommand extends FacilioCommand {
    private static final String MIN_START_DATE = "__minStartDate";
    private static final String DATE_FORMAT = "__date_format";
    private static final String TIMESTAMP = "__timeStamp";

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        FacilioView viewObj = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        CalendarViewRequestContext calendarViewRequest = (CalendarViewRequestContext) context.get(FacilioConstants.ViewConstants.CALENDAR_VIEW_REQUEST);
        List<Map<String, Object>> aggregateDataList = (List<Map<String, Object>>) context.get(FacilioConstants.ViewConstants.CALENDAR_VIEW_AGGREGATE_DATA);
        List<CustomizationDataContext> customizationDataContexts = (List<CustomizationDataContext>) context.get(FacilioConstants.ViewConstants.CALENDAR_VIEW_CUSTOMIZATON_DATAMAP);

        CalendarViewContext calendarViewObj = viewObj.getCalendarViewContext();
        FacilioField startTimeField = calendarViewObj.getStartDateField();
        FacilioField endTimeField = calendarViewObj.getEndDateField();

        Map<String, Object> calendarViewResult = getAggregateResult(startTimeField, endTimeField, aggregateDataList,
                customizationDataContexts, calendarViewRequest);

        JSONObject recordJSON = new JSONObject();
        recordJSON.put(moduleName, calendarViewResult);
        Constants.setJsonRecordMap(context, recordJSON);

        return false;
    }

    private Map<String, Object> getAggregateResult(FacilioField startTimeField, FacilioField endDateField,
                                                   List<Map<String, Object>> aggregateDataList,
                                                   List<CustomizationDataContext> customizationDataContexts,
                                                   CalendarViewRequestContext calendarViewRequestContext) throws Exception {
        Map<String, Object> calendarViewResult = new HashMap<>();

        if (CollectionUtils.isEmpty(customizationDataContexts)) {
            return calendarViewResult;
        }

        // map with aggregationKey(date or hour) vs count
        Map<String, Object> aggregateDataMap = getAggregateDataMap(aggregateDataList, startTimeField);

        // add customizationData to aggregateDataMap
        for (CustomizationDataContext customizationData : customizationDataContexts) {
            Map<String, Object> recordMap = customizationData.getData();
            String dateFormat = String.valueOf(recordMap.get(DATE_FORMAT));
            Map<String, Object> aggregateValueMap = (Map<String, Object>) aggregateDataMap.get(dateFormat);

            List<CustomizationDataContext> dataList;
            if (MapUtils.isNotEmpty(aggregateValueMap) && aggregateValueMap.containsKey("list")) {
                dataList = (List<CustomizationDataContext>) aggregateValueMap.get("list");
            } else {
                dataList = new ArrayList<>();
                aggregateValueMap.put("list", dataList);
            }
            dataList.add(customizationData);
        }

        if (MapUtils.isNotEmpty(aggregateDataMap)) {
            // add "hidden" param to cells with hidden records
            for (String currDate : aggregateDataMap.keySet()) {
                Map<String, Object> currDateDataMap = (Map<String, Object>) aggregateDataMap.get(currDate);
                if ((Long) currDateDataMap.get("count") > calendarViewRequestContext.getMaxResultPerCell()) {
                    // each timeStamp contains a additional data to find "hidden" records in consecutive timeStamps
                    List<CustomizationDataContext> customizationData = (List<CustomizationDataContext>) currDateDataMap.get("list");
                    CustomizationDataContext additionalCustomizationData = customizationData.get(calendarViewRequestContext.getMaxResultPerCell());
                    Map<String,Object> additionalRecordMap = additionalCustomizationData.getData();

                    if (endDateField != null && additionalRecordMap.containsKey(endDateField.getName())) {
                        long endTime = (long) additionalRecordMap.get(endDateField.getName());
                        long startTime = (long) additionalRecordMap.get(startTimeField.getName());
                        updateHiddenRecordDetail(aggregateDataMap, startTime, endTime);
                    }
                    customizationData.remove(calendarViewRequestContext.getMaxResultPerCell());
                }
            }

            // construct result data
            for (String currDate : aggregateDataMap.keySet()) {
                Map<String, Object> currDateMap = (Map<String, Object>) aggregateDataMap.get(currDate);
                long count = (Long) currDateMap.get("count");
                boolean hiddenParam = (boolean) currDateMap.getOrDefault("hidden", false);
                List<CustomizationDataContext> customizationData = (List<CustomizationDataContext>) currDateMap.get("list");

                if (hiddenParam || CollectionUtils.isNotEmpty(customizationData)) {
                    Map<String, Object> newMapToInsert = new HashMap<>();
                    newMapToInsert.put("count", count);
                    newMapToInsert.put("hidden", hiddenParam);
                    newMapToInsert.put(DATE_FORMAT, currDate);
                    newMapToInsert.put("list", customizationData);
                    calendarViewResult.put(String.valueOf(currDateMap.get(TIMESTAMP)), newMapToInsert);
                }
            }
        }

        return calendarViewResult;
    }

    public static void updateHiddenRecordDetail(Map<String, Object> aggregateMap, long startTime, long endTime) {
        for (String currDate : aggregateMap.keySet()) {
            Map<String, Object> currDateRecordMap = (Map<String, Object>)aggregateMap.get(currDate);
            long currDateTimeStamp = (long) currDateRecordMap.get(TIMESTAMP);

            if (currDateTimeStamp > startTime && currDateTimeStamp <= endTime) {
                currDateRecordMap.put("hidden", true);
            }
        }
    }

    private Map<String, Object> getAggregateDataMap(List<Map<String, Object>> aggregateValue, FacilioField startTimeField) {
        if (CollectionUtils.isEmpty(aggregateValue)) {
            return null;
        }

        Map<String, Object> aggregateMap = new HashMap<>();
        for (Map<String, Object> objectMap : aggregateValue) {
            Map<String, Object> valueMap = new HashMap<>();
            valueMap.put("count", objectMap.get("id"));
            valueMap.put(TIMESTAMP, objectMap.get(MIN_START_DATE));
            String dateFormatValue = (String) objectMap.get(startTimeField.getName());

            aggregateMap.put(dateFormatValue, valueMap);
        }

        return aggregateMap;
    }
}

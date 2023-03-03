package com.facilio.v3.commands;

import com.facilio.bmsconsole.calendarview.CalendarViewContext;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.timeline.context.CustomizationDataContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstructCalendarViewResponseCommand extends FacilioCommand {
    private static final Logger logger = LogManager.getLogger(ConstructCalendarViewResponseCommand.class);
    private static final String MIN_START_DATE = "__minStartDate";
    private static final String DATE_FORMAT = "__date_format";
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        FacilioView viewObj = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        List<Map<String, Object>> aggregateValue = (List<Map<String, Object>>) context.get(FacilioConstants.ViewConstants.CALENDAR_VIEW_AGGREGATE_DATA);
        List<CustomizationDataContext> customizationDataContexts = (List<CustomizationDataContext>) context.get(FacilioConstants.ViewConstants.CALENDAR_VIEW_CUSTOMIZATON_DATAMAP);

        CalendarViewContext calendarViewObj = viewObj.getCalendarViewContext();
        FacilioField startTimeField = calendarViewObj.getStartDateField();

        Map<String, Object> calendarViewResponse = getAggregateResult(startTimeField, aggregateValue, customizationDataContexts);
        JSONObject recordJSON = new JSONObject();
        recordJSON.put(moduleName, calendarViewResponse);
        Constants.setJsonRecordMap(context, recordJSON);
        return false;
    }

    private Map<String, Object> getAggregateResult(FacilioField startTimeField, List<Map<String, Object>> aggregateValue,
                                                   List<CustomizationDataContext> customizationDataContexts) throws Exception {
        Map<String, Object> calendarViewResult = new HashMap<>();

        if (CollectionUtils.isEmpty(customizationDataContexts)) {
            return calendarViewResult;
        }

        Map<String, Object> aggregateMap = getAggregateMap(aggregateValue, startTimeField);

        for (CustomizationDataContext customizationData : customizationDataContexts) {
            Map<String, Object> recordMap = customizationData.getData();
            Map<String, Object> aggregateValueMap = (Map<String, Object>) aggregateMap.get(recordMap.get(DATE_FORMAT));
            String minStartDate = aggregateValueMap.get(MIN_START_DATE).toString();

            Map<String, Object> dataMap = (Map<String, Object>) calendarViewResult.get(minStartDate);
            List<CustomizationDataContext> dataList;
            if (dataMap == null) {
                dataMap = new HashMap<>();
                dataList = new ArrayList<>();
                dataMap.put("list", dataList);
                dataMap.put("count", aggregateValueMap.get("count"));
                calendarViewResult.put(minStartDate, dataMap);
            } else {
                dataList = (List<CustomizationDataContext>) dataMap.get("list");
            }
            dataList.add(customizationData);
        }

        return calendarViewResult;
    }

    private Map<String, Object> getAggregateMap(List<Map<String, Object>> aggregateValue, FacilioField startTimeField) {
        if (CollectionUtils.isEmpty(aggregateValue)) {
            return null;
        }

        Map<String, Object> aggregateMap = new HashMap<>();
        for (Map<String, Object> objectMap : aggregateValue) {
            String dateFormatValue = (String) objectMap.get(startTimeField.getName());

            Map<String, Object> valueMap = new HashMap<>();
            valueMap.put(MIN_START_DATE, objectMap.get(MIN_START_DATE));
            valueMap.put("count", objectMap.get("id"));
            aggregateMap.put(dateFormatValue, valueMap);
        }

        return aggregateMap;
    }
}

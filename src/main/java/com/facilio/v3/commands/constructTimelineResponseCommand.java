package com.facilio.v3.commands;

import com.facilio.bmsconsole.timelineview.context.TimelineViewContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class constructTimelineResponseCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(constructTimelineResponseCommand.class.getName());

    private static final String MIN_START_DATE = "__minStartDate";
    private static final String DATE_FORMAT = "__date_format";

    @Override
    public boolean executeCommand(Context context) throws Exception {

        TimelineViewContext viewObj = (TimelineViewContext)context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        FacilioField startTimeField = viewObj.getStartDateField();
        FacilioField timelineGroupField = viewObj.getGroupByField();

        List<Map<String, Object>> recordMapList = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TIMELINE_V3_DATAMAP);
        List<Map<String, Object>> aggregateValue = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TIMELINE_AGGREGATE_DATA);

        Map<String, Object> timelineResponse = aggregateResult(timelineGroupField, startTimeField, recordMapList, aggregateValue);
        context.put(FacilioConstants.ContextNames.TIMELINE_DATA, timelineResponse);
        return false;
    }

    private Map<String, Object> aggregateResult(FacilioField timelineGroupField,
                                                       FacilioField startTimeField, List<Map<String, Object>> v3Contexts,
                                                       List<Map<String, Object>> aggregateValue) throws Exception {
        Map<String, Object> timelineResult = new HashMap<>();

        if (CollectionUtils.isEmpty(v3Contexts)) {
            return timelineResult;
        }

        Map<String, Object> aggregateMap = getAggregateMap(aggregateValue, timelineGroupField, startTimeField);

        for (Map<String, Object> recordMap : v3Contexts) {
            Object o = recordMap.get(timelineGroupField.getName());

            String value = getFieldValue(o, timelineGroupField);
            if (value == null) {
                continue;
            }

            Map<String, Object> groupJSON = (Map<String, Object>) timelineResult.get(value);
            if (groupJSON == null) {
                groupJSON = new HashMap<>();
                timelineResult.put(value, groupJSON);
            }

            Map<String, Object> aggregateDateMap = (Map<String, Object>) aggregateMap.get(value);
            Map<String, Object> aggregateValueMap = (Map<String, Object>) aggregateDateMap.get(recordMap.get(DATE_FORMAT));

            String minStartDate = aggregateValueMap.get(MIN_START_DATE).toString();

            Map<String, Object> dataMap = (Map<String, Object>) groupJSON.get(minStartDate);
            List<Map<String, Object>> dataList;
            if (dataMap == null) {
                dataMap = new HashMap<>();
                dataMap.put("count", aggregateValueMap.get("count"));
                groupJSON.put(minStartDate, dataMap);
                dataList = new ArrayList<>();
                dataMap.put("data", dataList);
            } else {
                dataList = (List<Map<String, Object>>) dataMap.get("data");
            }

            dataList.add(recordMap);
        }

        return timelineResult;
    }

    private String getFieldValue(Object o, FacilioField timelineGroupField) {
        String value = null;
        if (o == null) {
            return "-1";
        }

        if (timelineGroupField.getDataTypeEnum() == FieldType.ENUM) {
            value = String.valueOf(o);
        } else if (timelineGroupField.getDataTypeEnum() == FieldType.LOOKUP) {
            value = String.valueOf(((Map<String, Object>) o).get("id"));
        }
        return value;
    }

    private Map<String, Object> getAggregateMap(List<Map<String, Object>> aggregateValue, FacilioField timelineGroupField, FacilioField startTimeField) {
        if (CollectionUtils.isEmpty(aggregateValue)) {
            return null;
        }

        Map<String, Object> aggregateMap = new HashMap<>();
        for (Map<String, Object> map : aggregateValue) {
            Object value = map.get(timelineGroupField.getName());
            String fieldValue = getFieldValue(value, timelineGroupField);

            Map<String, Object> dateMap = (Map<String, Object>) aggregateMap.get(fieldValue);
            if (dateMap == null) {
                dateMap = new HashMap<>();
                aggregateMap.put(fieldValue, dateMap);
            }

            String dateFormatValue = (String) map.get(startTimeField.getName());
            Map<String, Object> valueMap = new HashMap<>();
            dateMap.put(dateFormatValue, valueMap);
            valueMap.put(MIN_START_DATE, map.get(MIN_START_DATE));
            valueMap.put("count", map.get("id"));
        }
        return aggregateMap;
    }

}

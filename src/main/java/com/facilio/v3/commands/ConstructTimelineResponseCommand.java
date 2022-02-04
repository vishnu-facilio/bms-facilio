package com.facilio.v3.commands;

import com.facilio.bmsconsole.timelineview.context.TimelineViewContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.recordcustomization.RecordCustomizationContext;
import com.facilio.timeline.context.TimelineRecordContext;
import com.facilio.v3.util.TimelineViewUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstructTimelineResponseCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(ConstructTimelineResponseCommand.class.getName());

    private static final String MIN_START_DATE = "__minStartDate";
    private static final String DATE_FORMAT = "__date_format";

    @Override
    public boolean executeCommand(Context context) throws Exception {

        TimelineViewContext viewObj = (TimelineViewContext)context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        FacilioField startTimeField = viewObj.getStartDateField();
        FacilioField timelineGroupField = viewObj.getGroupByField();
        RecordCustomizationContext customizationData = viewObj.getRecordCustomization();

        List<Map<String, Object>> recordMapList = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TIMELINE_V3_DATAMAP);
        List<Map<String, Object>> aggregateValue = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TIMELINE_AGGREGATE_DATA);

        Map<String, Object> timelineResponse = aggregateResult(timelineGroupField, startTimeField, recordMapList, aggregateValue, customizationData);
        context.put(FacilioConstants.ContextNames.TIMELINE_DATA, timelineResponse);
        return false;
    }

    private Map<String, Object> aggregateResult(FacilioField timelineGroupField,
                                                       FacilioField startTimeField, List<Map<String, Object>> v3Contexts,
                                                       List<Map<String, Object>> aggregateValue, RecordCustomizationContext customizationData) throws Exception {
        Map<String, Object> timelineResult = new HashMap<>();

        if (CollectionUtils.isEmpty(v3Contexts)) {
            return timelineResult;
        }

        FacilioField customizationField = TimelineViewUtil.getCustomizationField(customizationData);
        Map<String, String> fieldValueVsCustomization = TimelineViewUtil.getFieldValueVsCustomizationData(customizationData);

        Map<String, Object> aggregateMap = getAggregateMap(aggregateValue, timelineGroupField, startTimeField);

        for (Map<String, Object> recordMap : v3Contexts) {
            Object o = recordMap.get(timelineGroupField.getName());

            String value = TimelineViewUtil.getTimelineSupportedFieldValue(o, timelineGroupField);
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
            List<TimelineRecordContext> dataList;
            if (dataMap == null) {
                dataMap = new HashMap<>();
                dataMap.put("count", aggregateValueMap.get("count"));
                groupJSON.put(minStartDate, dataMap);
                dataList = new ArrayList<>();
                dataMap.put("list", dataList);
            } else {
                dataList = (List<TimelineRecordContext>) dataMap.get("list");
            }

            //Adding customization
            TimelineRecordContext recordDetail = new TimelineRecordContext();
            recordDetail.setData(recordMap);
            String customization = TimelineViewUtil.getRecordBasedCustomizationDetails(customizationData, customizationField, fieldValueVsCustomization, recordMap);
            if(customization != null) {
                recordDetail.setCustomization(customization);
            }

            dataList.add(recordDetail);
        }

        return timelineResult;
    }

    private Map<String, Object> getAggregateMap(List<Map<String, Object>> aggregateValue, FacilioField timelineGroupField, FacilioField startTimeField) {
        if (CollectionUtils.isEmpty(aggregateValue)) {
            return null;
        }

        Map<String, Object> aggregateMap = new HashMap<>();
        for (Map<String, Object> map : aggregateValue) {
            Object value = map.get(timelineGroupField.getName());
            String fieldValue = TimelineViewUtil.getTimelineSupportedFieldValue(value, timelineGroupField);

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

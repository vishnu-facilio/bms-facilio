package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.timelineview.context.TimelineViewContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.recordcustomization.RecordCustomizationContext;
import com.facilio.recordcustomization.RecordCustomizationValuesContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

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

        FacilioField customizationField = null;
        Map<String, String> fieldValueVsCustomization = new HashMap<>();
        if(customizationData != null)
        {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            if(customizationData.getCustomizationType() == RecordCustomizationContext.CustomizationType.FIELD.getIntVal()) {
                customizationField = modBean.getField(customizationData.getCustomizationFieldId());
                for(RecordCustomizationValuesContext value : customizationData.getValues()){
                    fieldValueVsCustomization.put(value.getFieldValue(), value.getCustomization());
                }
            }
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
                dataMap.put("list", dataList);
            } else {
                dataList = (List<Map<String, Object>>) dataMap.get("list");
            }

            //Adding customization
            Map<String, Object> recordDetail = new HashMap<String, Object>();
            recordDetail.put("data", recordMap);
            String customization = (customizationData != null) ? customizationData.getDefaultCustomization() : null;

            if(customizationData != null && customizationData.getCustomizationType() == RecordCustomizationContext.CustomizationType.NAMED_CRITERIA.getIntVal()) {
                for(RecordCustomizationValuesContext customizationValue : customizationData.getValues()){
                    if(customizationValue.getNamedCriteria().evaluate(recordMap, null, null)) {
                        customization = customizationValue.getCustomization();
                        break;
                    }
                }
            }
            else if(customizationField != null && recordMap.get(customizationField.getName()) != null) {
                String fieldValue = getFieldValue(recordMap.get(customizationField.getName()), customizationField);
                if (fieldValueVsCustomization.containsKey(fieldValue)) {
                    customization = fieldValueVsCustomization.get(fieldValue);
                }
            }
            if(customization != null) {
                recordDetail.put("customization", customization);
            }

            dataList.add(recordDetail);
        }

        return timelineResult;
    }

    private String getFieldValue(Object o, FacilioField field) {
        String value = null;
        if (o == null) {
            return "-1";
        }

        if (field.getDataTypeEnum() == FieldType.ENUM || field.getDataTypeEnum() == FieldType.BOOLEAN) {
            value = String.valueOf(o);
        } else if (field.getDataTypeEnum() == FieldType.LOOKUP) {
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

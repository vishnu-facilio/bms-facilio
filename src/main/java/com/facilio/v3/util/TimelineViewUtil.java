package com.facilio.v3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.timelineview.context.TimelineScheduledViewContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.recordcustomization.RecordCustomizationContext;
import com.facilio.recordcustomization.RecordCustomizationValuesContext;
import com.facilio.timeline.context.CustomizationDataContext;
import com.facilio.timeline.context.TimelineRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TimelineViewUtil {

    public static Criteria buildMainCriteria(FacilioField startTimeField, FacilioField endTimeField, TimelineRequest timelineRequest, FacilioField timelineGroupField, Criteria viewCriteria, Criteria filterCriteria, boolean getUnscheduledOnly) {
        Criteria mainCriteria = new Criteria();

        if(getUnscheduledOnly) {
            Criteria timeCriteria = new Criteria();
            timeCriteria.addAndCondition(CriteriaAPI.getCondition(startTimeField, CommonOperators.IS_EMPTY));
            mainCriteria.andCriteria(timeCriteria);
        }
        else {
            Criteria timeCriteria = new Criteria();
            timeCriteria.addAndCondition(CriteriaAPI.getCondition(startTimeField, timelineRequest.getDateValue(), DateOperators.BETWEEN));
            timeCriteria.addOrCondition(CriteriaAPI.getCondition(endTimeField, timelineRequest.getDateValue(), DateOperators.BETWEEN));
            mainCriteria.andCriteria(timeCriteria);

            Criteria rollOverCriteria = new Criteria();
            rollOverCriteria.addAndCondition(CriteriaAPI.getCondition(startTimeField, String.valueOf(timelineRequest.getStartTime()), NumberOperators.LESS_THAN));
            rollOverCriteria.addAndCondition(CriteriaAPI.getCondition(endTimeField, String.valueOf(timelineRequest.getEndTime()), NumberOperators.GREATER_THAN));
            mainCriteria.orCriteria(rollOverCriteria);
        }

        if(!getUnscheduledOnly || (getUnscheduledOnly && (timelineRequest.isGetUnGrouped() || CollectionUtils.isNotEmpty(timelineRequest.getGroupIds())))) {
            Criteria groupCriteria = new Criteria();
            if (CollectionUtils.isNotEmpty(timelineRequest.getGroupIds())) {
                groupCriteria.addAndCondition(CriteriaAPI.getCondition(timelineGroupField, StringUtils.join(timelineRequest.getGroupIds(), ","), NumberOperators.EQUALS));
            }
            if (timelineRequest.isGetUnGrouped()) {
                groupCriteria.addOrCondition(CriteriaAPI.getCondition(timelineGroupField, CommonOperators.IS_EMPTY));
            }
            mainCriteria.andCriteria(groupCriteria);
        }

        if(viewCriteria != null)
        {
            mainCriteria.andCriteria(viewCriteria);
        }
        if (filterCriteria != null && !filterCriteria.isEmpty()) {
            Criteria criteria = new Criteria();
            criteria.andCriteria(filterCriteria);
            mainCriteria.andCriteria(criteria);
        }
        return mainCriteria;
    }

    public static FacilioContext getTimelineContext(FacilioChain chain, TimelineRequest timelineRequest) throws Exception
    {
        return getTimelineContext(chain, timelineRequest, false, -1, -1, false);
    }
    public static FacilioContext getTimelineContext(FacilioChain chain, TimelineRequest timelineRequest, boolean isListView, int page, int perPage, boolean getUnscheduledData) throws Exception
    {
        FacilioContext context = chain.getContext();

        if (org.apache.commons.collections.MapUtils.isNotEmpty(timelineRequest.getFilters())) {
            JSONParser parser = new JSONParser();
            JSONObject filterJSON = (JSONObject) parser.parse(timelineRequest.getFilters().toJSONString());
            context.put(FacilioConstants.ContextNames.FILTERS, filterJSON);
        }
        if(isListView) {
            JSONObject pagination = new JSONObject();
            pagination.put("page", page);
            pagination.put("perPage", perPage);
            context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        }
        context.put(FacilioConstants.ContextNames.TIMELINE_GET_UNSCHEDULED_DATA, getUnscheduledData);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, timelineRequest.getModuleName());
        context.put(FacilioConstants.ContextNames.CV_NAME, timelineRequest.getViewName());
        context.put(FacilioConstants.ContextNames.TIMELINE_REQUEST, timelineRequest);
        return context;
    }

    public static long getCustomizationIdFromViewId(long viewId) throws Exception
    {
        FacilioModule timelineModule = ModuleFactory.getTimelineViewModule();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getTimelineViewFields(timelineModule))
                .table(timelineModule.getTableName())
                .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(viewId), NumberOperators.EQUALS));
        Map<String, Object> props = selectBuilder.fetchFirst();
        if(props != null && props.containsKey("recordCustomizationId"))
        {
            return (long)props.get("recordCustomizationId");
        }
        return -1;
    }

    public static String getTimelineSupportedFieldValue(Object o, FacilioField field) {
        List<FieldType> supportedFieldTypes = Arrays.asList(FieldType.ENUM, FieldType.SYSTEM_ENUM, FieldType.BOOLEAN, FieldType.STRING_SYSTEM_ENUM);
        String value = null;
        if (o == null) {
            return "-1";
        }

        if (supportedFieldTypes.contains(field.getDataTypeEnum())) {
            value = String.valueOf(o);
        } else if (field.getDataTypeEnum() == FieldType.LOOKUP) {
            value = String.valueOf(((Map<String, Object>) o).get("id"));
        }
        return value;
    }

    public static List<CustomizationDataContext> getCustomizationDataMap(List<Map<String, Object>> recordMapList, RecordCustomizationContext customizationData) throws Exception{
        List<CustomizationDataContext> timelineList = new ArrayList<>();

        FacilioField customizationField = null;
        Map<String, String> fieldValueVsCustomization = null;
        if(customizationData != null && customizationData.getCustomizationType() == RecordCustomizationContext.CustomizationType.FIELD.getIntVal()) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            customizationField = modBean.getField(customizationData.getCustomizationFieldId());
            fieldValueVsCustomization = new HashMap<>();
            for(RecordCustomizationValuesContext value : customizationData.getValues()){
                fieldValueVsCustomization.put(value.getFieldValue(), value.getCustomization());
            }
        }
        for(Map<String,Object> record : recordMapList) {
            CustomizationDataContext recordContext = new CustomizationDataContext();
            recordContext.setData(record);
            //Adding customization
            String customization = (customizationData != null) ? customizationData.getDefaultCustomization() : null;
            if(customizationData != null && customizationData.getCustomizationType() == RecordCustomizationContext.CustomizationType.NAMED_CRITERIA.getIntVal()) {
                for(RecordCustomizationValuesContext customizationValue : customizationData.getValues()){
                    if(customizationValue.getNamedCriteria().evaluate(record, null, null)) {
                        customization = customizationValue.getCustomization();
                        break;
                    }
                }
            }
            else if(customizationField != null && record.get(customizationField.getName()) != null) {
                String fieldValue = TimelineViewUtil.getTimelineSupportedFieldValue(record.get(customizationField.getName()), customizationField);
                if (fieldValueVsCustomization.containsKey(fieldValue)) {
                    customization = fieldValueVsCustomization.get(fieldValue);
                }
            }
            if(customization != null) {
                recordContext.setCustomization(customization);
            }
            timelineList.add(recordContext);
        }
        return timelineList;
    }

    public static TimelineScheduledViewContext getTimelineView(long id) throws Exception {
        Map<Long, TimelineScheduledViewContext> timelineScheduledViewMap = getAllTimelineViews(Collections.singletonList(id));
        return timelineScheduledViewMap.get(id);
    }

    public static Map<Long, TimelineScheduledViewContext> getAllTimelineViews(List<Long> viewIds) throws Exception {
        Map<Long, TimelineScheduledViewContext> timelineScheduledViewMap = new HashMap<>();

        FacilioModule timelineViewModule = ModuleFactory.getTimelineViewModule();
        List<FacilioField> timelineViewFields = FieldFactory.getTimelineViewFields(timelineViewModule);

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(timelineViewModule.getTableName())
                .select(timelineViewFields)
                .andCondition(CriteriaAPI.getIdCondition(viewIds, timelineViewModule));

        List<Map<String, Object>> timelineViewProps = selectRecordBuilder.get();

        if (CollectionUtils.isNotEmpty(timelineViewProps)) {
            List<TimelineScheduledViewContext> timelineViewContextList = FieldUtil.getAsBeanListFromMapList(timelineViewProps, TimelineScheduledViewContext.class);
            timelineScheduledViewMap = timelineViewContextList.stream()
                    .collect(Collectors.toMap(TimelineScheduledViewContext::getId, Function.identity()));
            return timelineScheduledViewMap;
        }

        return timelineScheduledViewMap;
    }
}

package com.facilio.timeline.util;

import com.facilio.beans.ModuleBean;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.timeline.context.TimelineRequest;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class TimelineAPI {

    private static final Logger LOGGER = LogManager.getLogger(TimelineAPI.class.getName());

    private static final String GROUP_CONCAT_FIELD_NAME = "__groupConcat";
    private static final String MIN_START_DATE = "__minStartDate";
    private static final String DATE_FORMAT = "__date_format";

    public static Map<String, Object> getTimeLineData(TimelineRequest timelineRequest) throws Exception {

        V3Config config = ChainUtil.getV3Config(timelineRequest.getModuleName());

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(timelineRequest.getModuleName());
        List<FacilioField> allFields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);

        FacilioField timelineGroupField = fieldMap.get("category");
        FacilioField startTimeField = fieldMap.get("actualWorkStart");
        FacilioField endTimeField = fieldMap.get("actualWorkEnd");

        Criteria timeCriteria = new Criteria();
        timeCriteria.addAndCondition(CriteriaAPI.getCondition(startTimeField, timelineRequest.getDateValue(), DateOperators.BETWEEN));
        timeCriteria.addOrCondition(CriteriaAPI.getCondition(endTimeField, timelineRequest.getDateValue(), DateOperators.BETWEEN));

        StringJoiner groupBy = new StringJoiner(",");
        Collection<FacilioField> aggregateFields = new ArrayList<>();

        aggregateFields.add(timelineGroupField);
        groupBy.add(timelineGroupField.getCompleteColumnName());

        FacilioField actualWorkStartAggrField = timelineRequest.getDateAggregator().getSelectField(startTimeField);
        aggregateFields.add(actualWorkStartAggrField);
        groupBy.add(actualWorkStartAggrField.getCompleteColumnName());

        aggregateFields.add(BmsAggregateOperators.CommonAggregateOperator.COUNT.getSelectField(FieldFactory.getIdField(module)));

        FacilioField minValueField = BmsAggregateOperators.NumberAggregateOperator.MIN.getSelectField(startTimeField);
        minValueField.setName(MIN_START_DATE);
        aggregateFields.add(minValueField);
        SelectRecordsBuilder<ModuleBaseWithCustomFields> aggregateBuilder = new SelectRecordsBuilder<>()
                .module(module)
                .setAggregation()
                .select(aggregateFields)
                .groupBy(groupBy.toString())
                .andCriteria(timeCriteria);
        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> aggregateValue = aggregateBuilder.getAsProps();
        LOGGER.error("query for aggregate value: " + aggregateBuilder + "; time taken is: " + (System.currentTimeMillis() - startTime));


        List<FacilioField> allModuleFields = new ArrayList<>(allFields);
        allModuleFields.add(FieldFactory.getStringField(DATE_FORMAT, "groupMax." + DATE_FORMAT, null));
        String subQuery = getQueryContactQuery(module, timelineGroupField, startTimeField, timelineRequest.getDateAggregator(), timeCriteria);

        SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
                .module(module)
                .innerJoinQuery(subQuery, "groupMax")
                    .on(timelineGroupField.getCompleteColumnName() + " <=> groupMax." + timelineGroupField.getName() + " AND "
                            + timelineRequest.getDateAggregator().getSelectField(startTimeField).getColumnName() + " = groupMax." + DATE_FORMAT)
                .beanClass(ChainUtil.getBeanClass(config, module))
                .select(allModuleFields);

        builder.andCustomWhere("FIND_IN_SET(" + startTimeField.getCompleteColumnName() + ", " + GROUP_CONCAT_FIELD_NAME + ") <= " + timelineRequest.getMaxResultPerCell());
        builder.andCriteria(timeCriteria);

        startTime = System.currentTimeMillis();
        List<Map<String, Object>> recordMapList = builder.getAsProps();
        LOGGER.error("query for actual record value: " + builder + "; time take is: " + (System.currentTimeMillis() - startTime));

        return aggregateResult(timelineGroupField, startTimeField, recordMapList, aggregateValue);

//        return v3Contexts;
    }

    private static Map<String, Object> aggregateResult(FacilioField timelineGroupField,
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

    private static String getFieldValue(Object o, FacilioField timelineGroupField) {
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

    private static Map<String, Object> getAggregateMap(List<Map<String, Object>> aggregateValue, FacilioField timelineGroupField, FacilioField startTimeField) {
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

    private static String getQueryContactQuery(FacilioModule module, FacilioField timelineGroupField,
                                               FacilioField startTimeField,
                                               BmsAggregateOperators.DateAggregateOperator dateAggregator, Criteria timeCriteria) throws Exception {
        SelectRecordsBuilder groupConcatQuery = new SelectRecordsBuilder()
                .module(module)
                .beanClass(V3Context.class)
                .andCriteria(timeCriteria);
//        joinAllExtendedModules(groupConcatQuery, module);

        StringJoiner groupByJoiner = new StringJoiner(",");
        Collection<FacilioField> fields = new ArrayList<>();

        fields.add(timelineGroupField);
        groupByJoiner.add(timelineGroupField.getCompleteColumnName());

        FacilioField startTimeAggrField = dateAggregator.getSelectField(startTimeField);
        startTimeAggrField.setName(DATE_FORMAT);
        fields.add(startTimeAggrField);
        groupByJoiner.add(startTimeAggrField.getCompleteColumnName());

        FacilioField dateFieldClone = getGroupConcatField(startTimeField);
        fields.add(dateFieldClone);

        groupConcatQuery.select(fields);
        groupConcatQuery.groupBy(groupByJoiner.toString());
        return groupConcatQuery.constructQueryString();
    }

    private static FacilioField getGroupConcatField(FacilioField startDateField) {
        FacilioField field = new FacilioField();
        field.setName(GROUP_CONCAT_FIELD_NAME);
        field.setDisplayName(field.getDisplayName());
        field.setColumnName("GROUP_CONCAT(" + startDateField.getColumnName() + " order by " + startDateField.getColumnName() + ")");
        field.setFieldId(field.getFieldId());
        field.setDataType(FieldType.STRING);
        return field;
    }

    private static void joinAllExtendedModules(GenericSelectRecordBuilder groupConcatQuery, FacilioModule module) {
        FacilioModule prevModule = module;
        FacilioModule extendedModule = module.getExtendModule();
        while (extendedModule != null) {
            groupConcatQuery.innerJoin(extendedModule.getTableName())
                    .on(prevModule.getTableName() + ".ID = " + extendedModule.getTableName() + ".ID");
            prevModule = extendedModule;
            extendedModule = extendedModule.getExtendModule();
        }
    }

    public static class TimelineResponse {

    }
}

package com.facilio.bmsconsole.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AggregationColumnMetaContext;
import com.facilio.bmsconsole.context.AggregationMetaContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.AggregationAPI;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.util.FilterUtil;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateRange;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class AggregationJob extends FacilioJob {

    protected final long TRANSACTION_TIME = 1800000 / 2;

    @Override
    public void execute(JobContext jc) throws Exception {
        Long id = jc.getJobId();
        AggregationMetaContext aggregationMeta = AggregationAPI.getAggregationMeta(id, true);
        Long lastSync = aggregationMeta.getLastSync();

        FacilioModule module = aggregationMeta.getColumnList().get(0).getModule();
        FacilioField ttimeField = FieldFactory.getField("ttime", "TTIME", module, FieldType.DATE_TIME);
        if (lastSync == null) {
            // get first reading time
            SelectRecordsBuilder builder = new SelectRecordsBuilder()
                    .module(aggregationMeta.getColumnList().get(0).getModule())
                    .aggregate(BmsAggregateOperators.NumberAggregateOperator.MIN, ttimeField);
            List<Map<String, Object>> asProps = builder.getAsProps();
            if (CollectionUtils.isNotEmpty(asProps)) {
                lastSync = (Long) asProps.get(0).get("ttime");
            }
        }

        if (lastSync == null) {
            // no data found
            return;
        }

        lastSync = aggregationMeta.getFrequencyTypeEnum().getAggregatedTime(lastSync);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField parentIdField = modBean.getField("parentId", module.getName());

        AggregationMetaContext.FrequencyType frequencyType = aggregationMeta.getFrequencyTypeEnum();

        long totalExecutionTime = 0;
        while (totalExecutionTime <= TRANSACTION_TIME) {
            long startTime = System.currentTimeMillis();
            Long nextSync = frequencyType.getNextSyncTime(lastSync);

            calculateAggregation(module, parentIdField, ttimeField, aggregationMeta, lastSync, nextSync, null);

            totalExecutionTime += (System.currentTimeMillis() - startTime);

            if (nextSync > System.currentTimeMillis()) {
                // break the loop
                break;
            }

            // continue next cycle
            lastSync = nextSync;
        }
        AggregationAPI.updateLastSyncTime(aggregationMeta.getId(), lastSync);
    }

    public static void calculateAggregation(FacilioModule module, FacilioField parentIdField, FacilioField ttimeField,
                                      AggregationMetaContext aggregationMeta,
                                      Long startTime, Long endTime, List<Long> parentIds) throws Exception {
        List<AggregationColumnMetaContext> columnList = aggregationMeta.getColumnList();
        AggregationMetaContext.FrequencyType frequencyType = aggregationMeta.getFrequencyTypeEnum();

        String groupByColumn = frequencyType.getAggregateOperator().getSelectField(ttimeField).getCompleteColumnName();

        SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
                .module(module)
                .select(Collections.singletonList(parentIdField))
                .beanClass(ReadingContext.class)
                .skipUnitConversion()
                .aggregate(BmsAggregateOperators.NumberAggregateOperator.MIN, ttimeField)
                .groupBy(parentIdField.getCompleteColumnName() + ", " + groupByColumn);

        if (CollectionUtils.isNotEmpty(parentIds)) {
            selectBuilder.andCondition(CriteriaAPI.getCondition(parentIdField, StringUtils.join(parentIds, ","),
                    NumberOperators.EQUALS));
        }

        for (AggregationColumnMetaContext columnMeta : columnList) {
            FacilioField field = columnMeta.getField();
            field.setName(columnMeta.getStorageField().getName());
            selectBuilder.aggregate(columnMeta.getAggregateOperatorEnum(), field);
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField marked = modBean.getField("marked", module.getName());
        if (marked != null) {
            selectBuilder.andCondition(CriteriaAPI.getCondition(marked, "false", BooleanOperators.IS));
        }

        selectBuilder.andCondition(CriteriaAPI.getCondition("TTIME",
                "ttime", startTime + "," + endTime, DateOperators.BETWEEN));

        if (aggregationMeta.getFilterJSON() != null) {
            DateRange dateRange = new DateRange(startTime, endTime);
            FilterUtil.setDataFilterCriteria(module.getName(), aggregationMeta.getFilterJSON(), dateRange, selectBuilder, null);
        }
        List<Map<String, Object>> props = selectBuilder.getAsProps();
        if (CollectionUtils.isNotEmpty(props)) {
            List<FacilioField> fields = new ArrayList<>();
            for (AggregationColumnMetaContext columnMeta : columnList) {
                fields.add(columnMeta.getStorageField());
            }
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(aggregationMeta.getStorageModule().getName()));
            fields.add(fieldMap.get("parentId"));
            FacilioField aggratedTimeField = fieldMap.get("aggregatedTime");
            fields.add(aggratedTimeField);

            Set<Long> uniqueAggregatedTime = new HashSet<>();

            InsertRecordBuilder<ModuleBaseWithCustomFields> insertBuilder = new InsertRecordBuilder<ModuleBaseWithCustomFields>()
                    .module(aggregationMeta.getStorageModule())
                    .fields(fields);
            for (Map<String, Object> prop : props) {
                Map<String, Object> map = new HashMap<>();
                map.put("parentId", prop.get("parentId"));
                Long ttime = (Long) prop.get("ttime");
                long aggregatedTime = frequencyType.getAggregatedTime(ttime);
                map.put("aggregatedTime", aggregatedTime);

                uniqueAggregatedTime.add(aggregatedTime);

                for (AggregationColumnMetaContext columnMeta : columnList) {
                    String fieldName = columnMeta.getStorageField().getName();
                    map.put(fieldName, prop.get(fieldName));
                }
                insertBuilder.addRecordProp(map);
            }

            DeleteRecordBuilder<ModuleBaseWithCustomFields> deleteRecordBuilder = new DeleteRecordBuilder<>()
                    .module(aggregationMeta.getStorageModule())
                    .andCondition(CriteriaAPI.getCondition(aggratedTimeField, uniqueAggregatedTime, NumberOperators.EQUALS));
            deleteRecordBuilder.delete();

            insertBuilder.save();
        }
    }
}

package com.facilio.bmsconsole.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.AggregationColumnMetaContext;
import com.facilio.bmsconsole.context.AggregationMetaContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.AggregationAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.util.FilterUtil;
import com.facilio.time.DateRange;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.sql.SQLException;
import java.util.*;

public class AggregationJob extends FacilioCommand {

    protected final long TRANSACTION_TIME = 1800000 / 2;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if (id != null) {
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
                return false;
            }

            lastSync = aggregationMeta.getFrequencyTypeEnum().getAggregatedTime(lastSync);

            List<AggregationColumnMetaContext> columnList = aggregationMeta.getColumnList();

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioField parentIdField = modBean.getField("parentId", module.getName());

            AggregationMetaContext.FrequencyType frequencyType = aggregationMeta.getFrequencyTypeEnum();
            String groupByColumn = frequencyType.getAggregateOperator().getSelectField(ttimeField).getCompleteColumnName();

            long totalExecutionTime = 0;
            while (totalExecutionTime <= TRANSACTION_TIME) {
                long startTime = System.currentTimeMillis();

                SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
                        .module(module)
                        .select(Collections.singletonList(parentIdField))
                        .beanClass(ReadingContext.class)
                        .aggregate(BmsAggregateOperators.NumberAggregateOperator.MIN, ttimeField)
                        .groupBy(parentIdField.getCompleteColumnName() + ", " + groupByColumn);
                for (AggregationColumnMetaContext columnMeta : columnList) {
                    selectBuilder.aggregate(columnMeta.getAggregateOperatorEnum(), columnMeta.getField());
                }

                Long nextSync = frequencyType.getNextSyncTime(lastSync);
                selectBuilder.andCondition(CriteriaAPI.getCondition("TTIME",
                        "ttime", lastSync + "," + nextSync, DateOperators.BETWEEN));

                if (aggregationMeta.getFilterJSON() != null) {
                    DateRange dateRange = new DateRange(lastSync, nextSync);
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
                            map.put(columnMeta.getStorageField().getName(), prop.get(columnMeta.getField().getName()));
                        }
                        insertBuilder.addRecordProp(map);
                    }

                    DeleteRecordBuilder<ModuleBaseWithCustomFields> deleteRecordBuilder = new DeleteRecordBuilder<>()
                            .module(aggregationMeta.getStorageModule())
                            .andCondition(CriteriaAPI.getCondition(aggratedTimeField, uniqueAggregatedTime, NumberOperators.EQUALS));
                    deleteRecordBuilder.delete();

                    insertBuilder.save();
                }
                totalExecutionTime += (System.currentTimeMillis() - startTime);

                if (nextSync > System.currentTimeMillis()) {
                    // break the loop
                    break;
                }

                // continue next cycle
                lastSync = nextSync;
            }
            updateLastSyncTime(aggregationMeta.getId(), lastSync);
        }
        return false;
    }

    private void updateLastSyncTime(Long id, Long lastSync) throws SQLException {
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getAggregationMetaModule().getTableName())
                .fields(Collections.singletonList(FieldFactory.getField("lastSync", "LAST_SYNC", FieldType.DATE_TIME)))
                .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getAggregationMetaModule()));
        Map<String, Object> map = new HashMap<>();
        map.put("lastSync", lastSync);
        builder.update(map);
    }
}

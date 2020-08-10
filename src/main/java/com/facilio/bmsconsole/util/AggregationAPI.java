package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AggregationColumnMetaContext;
import com.facilio.bmsconsole.context.AggregationMetaContext;
import com.facilio.bmsconsole.jobs.AggregationJob;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AggregationAPI {

    private static final long MAX_DIFFERENCE = 1000 * 60 * 60 * 24 * 30;

    public static AggregationMetaContext getAggregationMeta(Long id, boolean withColumn) throws Exception {
        List<AggregationMetaContext> metaList = getAggregationMetaContext(Collections.singletonList(id), withColumn);
        if (CollectionUtils.isNotEmpty(metaList)) {
            return metaList.get(0);
        }
        return null;
    }

    private static List<AggregationMetaContext> getAggregationMetaContext(Collection<Long> ids, boolean withColumn) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAggregationMetaModule().getTableName())
                .select(FieldFactory.getAggregationMetaFields())
                .andCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getAggregationMetaModule()));
        List<AggregationMetaContext> aggregationMetaContexts = FieldUtil.getAsBeanListFromMapList(builder.get(), AggregationMetaContext.class);
        if (CollectionUtils.isNotEmpty(aggregationMetaContexts)) {
            for (AggregationMetaContext aggregationMeta : aggregationMetaContexts) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule storageModule = modBean.getModule(aggregationMeta.getStorageModuleId());
                aggregationMeta.setStorageModule(storageModule);
            }
            if (withColumn) {
                Map<Long, AggregationMetaContext> metaMap = aggregationMetaContexts.stream().collect(Collectors.toMap(AggregationMetaContext::getId, Function.identity()));
                List<AggregationColumnMetaContext> aggregationColumnMetaList = getAggregationColumnMetaList(aggregationMetaContexts);
                if (CollectionUtils.isNotEmpty(aggregationColumnMetaList)) {
                    for (AggregationColumnMetaContext columnMeta : aggregationColumnMetaList) {
                        AggregationMetaContext aggregationMetaContext = metaMap.get(columnMeta.getAggregationMetaId());
                        List<AggregationColumnMetaContext> columnList = aggregationMetaContext.getColumnList();
                        if (columnList == null) {
                            columnList = new ArrayList<>();
                            aggregationMetaContext.setColumnList(columnList);
                        }
                        columnList.add(columnMeta);
                    }
                }
            }
        }
        return aggregationMetaContexts;
    }

    public static List<AggregationColumnMetaContext> getAggregationColumnMetaList(List<AggregationMetaContext> aggregationMetas) throws Exception {
        if (CollectionUtils.isEmpty(aggregationMetas)) {
            return null;
        }

        Map<Long, AggregationMetaContext> metaMap = new HashMap<>();
        for (AggregationMetaContext aggregationMetaContext : aggregationMetas) {
            metaMap.put(aggregationMetaContext.getId(), aggregationMetaContext);
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAggregationColumnMetaModule().getTableName())
                .select(FieldFactory.getAggregationColumnMetaFields())
                .andCondition(CriteriaAPI.getCondition("AGGREGATION_META_ID", "aggregationMetaId",
                        StringUtils.join(metaMap.keySet(), ","), NumberOperators.EQUALS));
        List<AggregationColumnMetaContext> columnMetaList = FieldUtil.getAsBeanListFromMapList(builder.get(), AggregationColumnMetaContext.class);
        if (CollectionUtils.isNotEmpty(columnMetaList)) {
            for (AggregationColumnMetaContext columnMeta : columnMetaList) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(columnMeta.getModuleId());
                columnMeta.setModule(module);

                FacilioField field = modBean.getField(columnMeta.getFieldId(), columnMeta.getModuleId());
                columnMeta.setField(field);

                AggregationMetaContext aggregationMeta = metaMap.get(columnMeta.getAggregationMetaId());
                FacilioField storageField = modBean.getField(columnMeta.getStorageFieldId(), aggregationMeta.getStorageModuleId());
                columnMeta.setStorageField(storageField);
            }
        }
        return columnMetaList;
    }

    private static List<AggregationMetaContext> getAggregationMetaOfModule(FacilioModule module) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAggregationColumnMetaModule().getTableName())
                .select(FieldFactory.getAggregationColumnMetaFields())
                .andCondition(CriteriaAPI.getCondition("FIELD_MODULE_ID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
        List<AggregationColumnMetaContext> columnMetaList = FieldUtil.getAsBeanListFromMapList(builder.get(), AggregationColumnMetaContext.class);
        if (CollectionUtils.isNotEmpty(columnMetaList)) {
            Set<Long> aggregationMetaIds = new HashSet<>();
            for (AggregationColumnMetaContext columnMeta : columnMetaList) {
                aggregationMetaIds.add(columnMeta.getAggregationMetaId());
            }
            return getAggregationMetaContext(aggregationMetaIds, true);
        }
        return null;
    }

    public static List<AggregationColumnMetaContext> getAggregateFields(Set<Long> fieldIds, DateRange range) throws Exception {
        if (range == null) {
            return null;
        }

        long currentTimeMillis = System.currentTimeMillis();
        if (range.getStartTime() > currentTimeMillis) {
            return null;
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAggregationColumnMetaModule().getTableName())
                .select(FieldFactory.getAggregationColumnMetaFields())
                .andCondition(CriteriaAPI.getCondition("FIELD_ID", "fieldId", StringUtils.join(fieldIds, ","), NumberOperators.EQUALS));

        List<AggregationColumnMetaContext> columnMetaList = FieldUtil.getAsBeanListFromMapList(builder.get(), AggregationColumnMetaContext.class);
        updateAggregationMetaInColumnMeta(columnMetaList);
        if (CollectionUtils.isNotEmpty(columnMetaList)) {
            long endTime = range.getEndTime();
            if (endTime > currentTimeMillis) {
                endTime = currentTimeMillis;
            }
            Iterator<AggregationColumnMetaContext> iterator = columnMetaList.iterator();
            while (iterator.hasNext()) {
                AggregationColumnMetaContext columnMetaContext = iterator.next();
                AggregationMetaContext aggregationMeta = columnMetaContext.getAggregationMeta();
                if (aggregationMeta.getFrequencyTypeEnum().getAggregatedTime(endTime) > aggregationMeta.getLastSync()) {
                    iterator.remove();
                }
            }
        }
        return columnMetaList;
    }

    private static void updateAggregationMetaInColumnMeta(List<AggregationColumnMetaContext> columnMetaList) throws Exception {
        if (CollectionUtils.isNotEmpty(columnMetaList)) {
            Set<Long> aggregationMetaIds = columnMetaList.stream().map(AggregationColumnMetaContext::getAggregationMetaId).collect(Collectors.toSet());
            List<AggregationMetaContext> list = getAggregationMetaContext(aggregationMetaIds, false);
            if (CollectionUtils.isNotEmpty(list)) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                Map<Long, AggregationMetaContext> map = list.stream().collect(Collectors.toMap(AggregationMetaContext::getId, Function.identity()));
                for (AggregationColumnMetaContext columnMeta : columnMetaList) {
                    AggregationMetaContext aggregationMeta = map.get(columnMeta.getAggregationMetaId());
                    columnMeta.setAggregationMeta(aggregationMeta);

                    FacilioModule module = modBean.getModule(columnMeta.getModuleId());
                    columnMeta.setModule(module);

                    FacilioField field = modBean.getField(columnMeta.getFieldId(), columnMeta.getModuleId());
                    columnMeta.setField(field);

                    FacilioField storageField = modBean.getField(columnMeta.getStorageFieldId(), aggregationMeta.getStorageModuleId());
                    columnMeta.setStorageField(storageField);
                }
            }
        }
    }

    public static void updateLastSyncTime(Long id, Long lastSync) throws SQLException {
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getAggregationMetaModule().getTableName())
                .fields(Collections.singletonList(FieldFactory.getField("lastSync", "LAST_SYNC", FieldType.DATE_TIME)))
                .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getAggregationMetaModule()));
        Map<String, Object> map = new HashMap<>();
        map.put("lastSync", lastSync);
        builder.update(map);
    }

    public static void aggregateData(FacilioModule module, List<Long> parentIds, Long startTime, Long endTime) throws Exception {
        if (module == null) {
            throw new IllegalArgumentException("Invalid module");
        }

        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Invalid time");
        }

        if (startTime > endTime) {
            throw new IllegalArgumentException("Start time cannot be greater than end time");
        }

        long diff = (endTime - startTime);
        List<AggregationMetaContext> aggregationMetaList = getAggregationMetaOfModule(module);
        if (CollectionUtils.isNotEmpty(aggregationMetaList)) {
            if (diff < MAX_DIFFERENCE) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                for (AggregationMetaContext aggregationMeta : aggregationMetaList) {
                    FacilioField parentIdField = modBean.getField("parentId", module.getName());
                    FacilioField ttimeField = modBean.getField("ttime", module.getName());
                    AggregationMetaContext.FrequencyType frequencyType = aggregationMeta.getFrequencyTypeEnum();
                    startTime = frequencyType.getAggregatedTime(startTime);
                    endTime = frequencyType.getNextSyncTime(
                            frequencyType.getAggregatedTime(endTime));
                    AggregationJob.calculateAggregation(module, parentIdField, ttimeField, aggregationMeta, startTime, endTime, parentIds);
                }
            }
            else {
                for (AggregationMetaContext aggregationMeta : aggregationMetaList) {
                    aggregationMeta.setLastSync(startTime);
                    updateLastSyncTime(aggregationMeta.getId(), startTime);
                }
            }
        }
    }
}

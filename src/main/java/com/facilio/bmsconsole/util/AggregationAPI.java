package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AggregationColumnMetaContext;
import com.facilio.bmsconsole.context.AggregationMetaContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AggregationAPI {
    public static AggregationMetaContext getAggregationMeta(Long id, boolean withColumn) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAggregationMetaModule().getTableName())
                .select(FieldFactory.getAggregationMetaFields())
                .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getAggregationMetaModule()));
        AggregationMetaContext aggregationMeta = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), AggregationMetaContext.class);

        if (aggregationMeta != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule storageModule = modBean.getModule(aggregationMeta.getStorageModuleId());
            aggregationMeta.setStorageModule(storageModule);

            if (withColumn) {
                List<AggregationColumnMetaContext> aggregationColumnList = getAggregationColumnMetaList(aggregationMeta);
                aggregationMeta.setColumnList(aggregationColumnList);
            }
        }
        return aggregationMeta;
    }

    public static List<AggregationColumnMetaContext> getAggregationColumnMetaList(AggregationMetaContext aggregationMeta) throws Exception {
        if (aggregationMeta == null) {
            return null;
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAggregationColumnMetaModule().getTableName())
                .select(FieldFactory.getAggregationColumnMetaFields())
                .andCondition(CriteriaAPI.getCondition("AGGREGATION_META_ID", "aggregationMetaId",
                        String.valueOf(aggregationMeta.getId()), NumberOperators.EQUALS));
        List<AggregationColumnMetaContext> columnMetaList = FieldUtil.getAsBeanListFromMapList(builder.get(), AggregationColumnMetaContext.class);
        if (CollectionUtils.isNotEmpty(columnMetaList)) {
            for (AggregationColumnMetaContext columnMeta : columnMetaList) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(columnMeta.getModuleId());
                columnMeta.setModule(module);

                FacilioField field = modBean.getField(columnMeta.getFieldId(), columnMeta.getModuleId());
                columnMeta.setField(field);

                FacilioField storageField = modBean.getField(columnMeta.getStorageFieldId(), aggregationMeta.getStorageModuleId());
                columnMeta.setStorageField(storageField);
            }
        }
        return columnMetaList;
    }

    private static List<AggregationMetaContext> getAggregationMetaContext(Collection<Long> ids) throws Exception {
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
        }
        return aggregationMetaContexts;
    }

    public static List<AggregationColumnMetaContext> getAggregateFields(Set<Long> fieldIds, long endTime) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAggregationColumnMetaModule().getTableName())
                .select(FieldFactory.getAggregationColumnMetaFields())
                .innerJoin(ModuleFactory.getAggregationMetaModule().getTableName())
                    .on(ModuleFactory.getAggregationColumnMetaModule().getTableName() + ".AGGREGATION_META_ID = " + ModuleFactory.getAggregationMetaModule().getTableName() + ".ID")
                .andCondition(CriteriaAPI.getCondition("FIELD_ID", "fieldId", StringUtils.join(fieldIds, ","), NumberOperators.EQUALS));

        if (endTime > 0) {
            builder.andCondition(CriteriaAPI.getCondition("LAST_SYNC", "lastSync", String.valueOf(endTime), NumberOperators.GREATER_THAN_EQUAL));
        }

        List<AggregationColumnMetaContext> columnMetaList = FieldUtil.getAsBeanListFromMapList(builder.get(), AggregationColumnMetaContext.class);
        if (CollectionUtils.isNotEmpty(columnMetaList)) {
            Set<Long> aggregationMetaIds = columnMetaList.stream().map(AggregationColumnMetaContext::getAggregationMetaId).collect(Collectors.toSet());
            List<AggregationMetaContext> list = getAggregationMetaContext(aggregationMetaIds);
            if (CollectionUtils.isNotEmpty(list)) {
                Map<Long, AggregationMetaContext> map = list.stream().collect(Collectors.toMap(AggregationMetaContext::getId, Function.identity()));
                for (AggregationColumnMetaContext columnMeta : columnMetaList) {
                    AggregationMetaContext aggregationMeta = map.get(columnMeta.getAggregationMetaId());
                    columnMeta.setAggregationMeta(aggregationMeta);

                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    FacilioModule module = modBean.getModule(columnMeta.getModuleId());
                    columnMeta.setModule(module);

                    FacilioField field = modBean.getField(columnMeta.getFieldId(), columnMeta.getModuleId());
                    columnMeta.setField(field);

                    FacilioField storageField = modBean.getField(columnMeta.getStorageFieldId(), aggregationMeta.getStorageModuleId());
                    columnMeta.setStorageField(storageField);
                }
            }
        }
        return columnMetaList;
    }
}

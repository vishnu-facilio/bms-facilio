package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AggregationColumnMetaContext;
import com.facilio.bmsconsole.context.AggregationMetaContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

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
}

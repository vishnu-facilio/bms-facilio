package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AggregationColumnMetaContext;
import com.facilio.bmsconsole.context.AggregationMetaContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class AddOrUpdateAggregationCommand extends FacilioCommand {

    private static final long HALF_HOUR_IN_SECONDS = 30 * 60;
    private static final String JOB_NAME = "AggregationJob";

    @Override
    public boolean executeCommand(Context context) throws Exception {
        AggregationMetaContext aggregationMeta = (AggregationMetaContext) context.get(FacilioConstants.ContextNames.AGGREGATION_META);
        if (aggregationMeta != null) {
            if (aggregationMeta.getFrequencyTypeEnum() == null) {
                throw new IllegalArgumentException("Invalid date aggregation operator");
            }

            if (aggregationMeta.getInterval() == null) {
                throw new IllegalArgumentException("Invalid interval");
            }
            if (aggregationMeta.getInterval() < HALF_HOUR_IN_SECONDS) {
                throw new IllegalArgumentException("Interval's minimum time is 30 minutes");
            }

            List<AggregationColumnMetaContext> columnList = aggregationMeta.getColumnList();
            if (CollectionUtils.isEmpty(columnList)) {
                throw new IllegalArgumentException("At least one column should be aggregated");
            }

            if (StringUtils.isEmpty(aggregationMeta.getName())) {
                throw new IllegalArgumentException("Invalid name");
            }

            if (aggregationMeta.getStorageModuleId() == null) {
                throw new IllegalArgumentException("Invalid storage module");
            }
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule storageModule = modBean.getModule(aggregationMeta.getStorageModuleId());
            if (storageModule == null) {
                throw new IllegalArgumentException("Invalid storage module");
            }
            aggregationMeta.setStorageModule(storageModule);

            validateColumnMeta(columnList, storageModule);

            if (aggregationMeta.getId() == null) {
                GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getAggregationMetaModule().getTableName())
                        .fields(FieldFactory.getAggregationMetaFields());
                long aggregationMetaId = builder.insert(FieldUtil.getAsProperties(aggregationMeta));
                aggregationMeta.setId(aggregationMetaId);
            }
            else {
                GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                        .table(ModuleFactory.getAggregationMetaModule().getTableName())
                        .fields(FieldFactory.getAggregationMetaFields())
                        .andCondition(CriteriaAPI.getIdCondition(aggregationMeta.getId(), ModuleFactory.getAggregationMetaModule()));
                builder.update(FieldUtil.getAsProperties(aggregationMeta));

                // delete existing column meta
                GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                        .table(ModuleFactory.getAggregationColumnMetaModule().getTableName())
                        .andCondition(CriteriaAPI.getCondition("AGGREGATION_META_ID", "aggregationMetaId",
                                String.valueOf(aggregationMeta.getId()), NumberOperators.EQUALS));
                deleteBuilder.delete();
            }

            // save column meta info
            Long aggregationMetaId = aggregationMeta.getId();
            for (AggregationColumnMetaContext columnMetaContext : columnList) {
                columnMetaContext.setAggregationMetaId(aggregationMetaId);
            }
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getAggregationColumnMetaModule().getTableName())
                    .fields(FieldFactory.getAggregationColumnMetaFields());
            builder.addRecords(FieldUtil.getAsMapList(columnList, AggregationColumnMetaContext.class));
            builder.save();

            FacilioTimer.deleteJob(aggregationMeta.getId(), JOB_NAME);
            FacilioTimer.schedulePeriodicJob(aggregationMeta.getId(), JOB_NAME, 30, aggregationMeta.getInterval().intValue(), "facilio");
        }
        return false;
    }

    private void validateColumnMeta(List<AggregationColumnMetaContext> columnList, FacilioModule storageModule) throws Exception {
        for (AggregationColumnMetaContext columnMetaContext : columnList) {
            if (columnMetaContext.getAggregateOperatorEnum() == null) {
                throw new IllegalArgumentException("Aggregate Operator cannot be empty");
            }
            if (columnMetaContext.getModuleId() == null) {
                throw new IllegalArgumentException("Invalid module");
            }
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(columnMetaContext.getModuleId());
            if (module == null) {
                throw new IllegalArgumentException("Invalid module");
            }
            columnMetaContext.setModule(module);

            if (columnMetaContext.getFieldId() == null) {
                throw new IllegalArgumentException("Invalid column field");
            }
            FacilioField field = modBean.getField(columnMetaContext.getFieldId(), columnMetaContext.getModuleId());
            if (field == null) {
                throw new IllegalArgumentException("Invalid column field");
            }
            columnMetaContext.setField(field);

            if (columnMetaContext.getStorageFieldId() == null) {
                throw new IllegalArgumentException("Invalid storage field");
            }
            FacilioField storageField = modBean.getField(columnMetaContext.getStorageFieldId(), storageModule.getModuleId());
            if (storageField == null) {
                throw new IllegalArgumentException("Invalid storage field");
            }
            columnMetaContext.setStorageField(storageField);
        }
    }
}

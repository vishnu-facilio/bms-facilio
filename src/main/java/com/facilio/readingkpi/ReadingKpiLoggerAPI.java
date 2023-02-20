package com.facilio.readingkpi;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingkpi.context.KpiLoggerContext;
import com.facilio.readingkpi.context.KpiResourceLoggerContext;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ReadingKpiLoggerAPI {
    public static long insertLog(Long kpiId, Integer kpiType, Boolean isSysCreated, Integer resourceCount) throws Exception {
        return insertLog(kpiId, kpiType, null, null, isSysCreated, resourceCount);
    }

    public static long insertLog(Long kpiId, Integer kpiType, Long intervalStartTime, Long intervalEndTime, Boolean isSysCreated, Integer resourceCount) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        KpiLoggerContext kpiLoggerContext = new KpiLoggerContext();

        ReadingKPIContext kpi = new ReadingKPIContext();
        kpi.setId(kpiId);
        kpiLoggerContext.setKpi(kpi);
        if (!isSysCreated) {
            kpiLoggerContext.setStartTime(intervalStartTime);
            kpiLoggerContext.setEndTime(intervalEndTime);
        }
        kpiLoggerContext.setExecStartTime(System.currentTimeMillis());
        kpiLoggerContext.setStatus(KpiResourceLoggerContext.KpiLoggerStatus.IN_PROGRESS.getIndex());
        kpiLoggerContext.setIsSysCreated(isSysCreated);
        kpiLoggerContext.setResourceCount(resourceCount);
        kpiLoggerContext.setKpiType(kpiType);

        kpiLoggerContext.setSysCreatedBy(AccountUtil.getCurrentUser());
        kpiLoggerContext.setSysCreatedTime(System.currentTimeMillis());


        InsertRecordBuilder<KpiLoggerContext> insertRecordBuilder = new InsertRecordBuilder<KpiLoggerContext>()
                .moduleName(FacilioConstants.ReadingKpi.KPI_LOGGER_MODULE)
                .fields(modBean.getAllFields(FacilioConstants.ReadingKpi.KPI_LOGGER_MODULE));
        return insertRecordBuilder.insert(kpiLoggerContext);
    }

    public static void updateLogWithId(Long id, Integer status, Long execEndTime, Integer successCount) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ReadingKpi.KPI_LOGGER_MODULE);

        KpiLoggerContext kpiLoggerContext = new KpiLoggerContext();
        kpiLoggerContext.setExecEndTime(execEndTime);
        kpiLoggerContext.setStatus(status);
        if (successCount != null) {
            kpiLoggerContext.setSuccessCount(successCount);
        }
        UpdateRecordBuilder<KpiLoggerContext> updateRecordBuilder = new UpdateRecordBuilder<KpiLoggerContext>()
                .moduleName(FacilioConstants.ReadingKpi.KPI_LOGGER_MODULE)
                .fields(fields)
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(modBean.getModule(FacilioConstants.ReadingKpi.KPI_LOGGER_MODULE)), String.valueOf(id), NumberOperators.EQUALS));

        updateRecordBuilder.update(kpiLoggerContext);
    }

    public static void updateLogWithKpiId(Long kpiId, Integer status, Long execEndTime, Integer successCount) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ReadingKpi.KPI_LOGGER_MODULE);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

        KpiLoggerContext kpiLoggerContext = new KpiLoggerContext();
        kpiLoggerContext.setExecEndTime(execEndTime);
        kpiLoggerContext.setStatus(status);
        if (successCount != null) {
            kpiLoggerContext.setSuccessCount(successCount);
        }
        UpdateRecordBuilder<KpiLoggerContext> updateRecordBuilder = new UpdateRecordBuilder<KpiLoggerContext>()
                .moduleName(FacilioConstants.ReadingKpi.KPI_LOGGER_MODULE)
                .fields(fields)
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("kpi"), String.valueOf(kpiId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("status"), String.valueOf(KpiResourceLoggerContext.KpiLoggerStatus.IN_PROGRESS.getIndex()), NumberOperators.EQUALS));

        updateRecordBuilder.update(kpiLoggerContext);
    }

    public static Integer getSuccessCount(Long parentLoggerId) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule kpiResourceLoggerModule = modBean.getModule(FacilioConstants.ReadingKpi.KPI_RESOURCE_LOGGER_MODULE);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ReadingKpi.KPI_RESOURCE_LOGGER_MODULE);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectRecordsBuilder = new GenericSelectRecordBuilder()
                .table(kpiResourceLoggerModule.getTableName())
                .select(new HashSet<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(kpiResourceLoggerModule))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("parentLoggerId"), Collections.singleton(parentLoggerId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("status"), String.valueOf(KpiResourceLoggerContext.KpiLoggerStatus.SUCCESS.getIndex()), NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectRecordsBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            Long count = null;
            for (Map<String, Object> prop : props) {
                count = (Long) prop.get("id");
            }
            if (count != null) {
                return count.intValue();
            }
        }
        return 0;
    }

    public static List<KpiResourceLoggerContext> getResourceIdsForLogger(Long parentLoggerId) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule kpiResourceLoggerModule = modBean.getModule(FacilioConstants.ReadingKpi.KPI_RESOURCE_LOGGER_MODULE);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ReadingKpi.KPI_RESOURCE_LOGGER_MODULE);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<KpiResourceLoggerContext> builder = new SelectRecordsBuilder<KpiResourceLoggerContext>()
                .module(kpiResourceLoggerModule)
                .select(Arrays.asList(fieldsMap.get("resourceId"), fieldsMap.get("status"), fieldsMap.get("message")))
                .beanClass(KpiResourceLoggerContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("parentLoggerId"), Collections.singleton(parentLoggerId), NumberOperators.EQUALS));
        return builder.get();
    }

    public static List<Map<String, Object>> getResourceLoggersForParentLogger(Long parentLoggerId) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule kpiResourceLoggerModule = modBean.getModule(FacilioConstants.ReadingKpi.KPI_RESOURCE_LOGGER_MODULE);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ReadingKpi.KPI_RESOURCE_LOGGER_MODULE);
        fields.add(FieldFactory.getIdField(kpiResourceLoggerModule));
        fields = fields.stream().filter(x -> !Objects.equals(x.getName(), "sysCreatedBy")).collect(Collectors.toList());
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(kpiResourceLoggerModule.getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("parentLoggerId"), Collections.singleton(parentLoggerId), NumberOperators.EQUALS));
        return builder.get();
    }

    public static void insertResourceLog(Long kpiId, Long parentLoggerId, Long resourceId, Long intervalStartTime, Long intervalEndTime, Boolean isHistorical) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        KpiResourceLoggerContext kpiResourceLoggerContext = new KpiResourceLoggerContext();
        kpiResourceLoggerContext.setKpiId(kpiId);
        kpiResourceLoggerContext.setParentLoggerId(parentLoggerId);
        kpiResourceLoggerContext.setResourceId(resourceId);
        kpiResourceLoggerContext.setStartTime(intervalStartTime);
        kpiResourceLoggerContext.setEndTime(intervalEndTime);
        kpiResourceLoggerContext.setSysCreatedBy(AccountUtil.getCurrentUser());
        kpiResourceLoggerContext.setSysCreatedTime(System.currentTimeMillis());
        kpiResourceLoggerContext.setCalculationStartTime(System.currentTimeMillis());
        kpiResourceLoggerContext.setStatus(KpiResourceLoggerContext.KpiLoggerStatus.IN_PROGRESS.getIndex());
        kpiResourceLoggerContext.setIsHistorical(isHistorical);

        InsertRecordBuilder<KpiResourceLoggerContext> insertRecordBuilder = new InsertRecordBuilder<KpiResourceLoggerContext>()
                .moduleName(FacilioConstants.ReadingKpi.KPI_RESOURCE_LOGGER_MODULE)
                .fields(modBean.getAllFields(FacilioConstants.ReadingKpi.KPI_RESOURCE_LOGGER_MODULE));
        insertRecordBuilder.insert(kpiResourceLoggerContext);
    }

    public static void updateResourceLog(Long id, KpiResourceLoggerContext.KpiLoggerStatus status) throws Exception {
        updateResourceLog(id, status, null);
    }

    public static void updateResourceLog(Long id, KpiResourceLoggerContext.KpiLoggerStatus status, String message) throws Exception {

        ModuleBean modBean = Constants.getModBean();
        FacilioModule kpiResourceLoggerModule = modBean.getModule(FacilioConstants.ReadingKpi.KPI_RESOURCE_LOGGER_MODULE);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ReadingKpi.KPI_RESOURCE_LOGGER_MODULE);
        KpiResourceLoggerContext kpiResourceLoggerContext = new KpiResourceLoggerContext();

        kpiResourceLoggerContext.setStatus(status.getIndex());
        if(message!=null) {
            kpiResourceLoggerContext.setMessage(message);
        }
        UpdateRecordBuilder<KpiResourceLoggerContext> updateRecordBuilder = new UpdateRecordBuilder<KpiResourceLoggerContext>()
                .module(kpiResourceLoggerModule)
                .fields(fields)
                .andCondition(CriteriaAPI.getIdCondition(id, kpiResourceLoggerModule));

        updateRecordBuilder.update(kpiResourceLoggerContext);
    }

    public static void updateResourceLog(Long kpiId, Long resourceId, Integer status, Long intervalStartTime, Long calcEndTime) throws Exception {
        updateResourceLog(kpiId, resourceId, status, intervalStartTime, calcEndTime, null);
    }

    public static void updateResourceLog(Long kpiId, Long resourceId, Integer status, Long intervalStartTime, Long calcEndTime, String message) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule kpiResourceLoggerModule = modBean.getModule(FacilioConstants.ReadingKpi.KPI_RESOURCE_LOGGER_MODULE);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ReadingKpi.KPI_RESOURCE_LOGGER_MODULE);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        KpiResourceLoggerContext kpiResourceLoggerContext = new KpiResourceLoggerContext();

        kpiResourceLoggerContext.setCalculationEndTime(calcEndTime);
        kpiResourceLoggerContext.setStatus(status);
        if (StringUtils.isNotEmpty(message)) {
            kpiResourceLoggerContext.setMessage(message);
        }
        UpdateRecordBuilder<KpiResourceLoggerContext> updateRecordBuilder = new UpdateRecordBuilder<KpiResourceLoggerContext>()
                .module(kpiResourceLoggerModule)
                .fields(fields)
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("kpiId"), String.valueOf(kpiId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("resourceId"), String.valueOf(resourceId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("startTime"), String.valueOf(intervalStartTime), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("status"), String.valueOf(KpiResourceLoggerContext.KpiLoggerStatus.IN_PROGRESS.getIndex()), NumberOperators.EQUALS));

        updateRecordBuilder.update(kpiResourceLoggerContext);
    }

    public static long getNextJobId() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.ReadingKpi.KPI_LOGGER_MODULE);
        FacilioField idField = FieldFactory.getIdField(module);

        SelectRecordsBuilder<KpiLoggerContext> selectBuilder = new SelectRecordsBuilder<KpiLoggerContext>()
                .select(Collections.singletonList(idField))
                .module(module)
                .beanClass(KpiLoggerContext.class)
                .orderBy("ID DESC")
                .limit(1);

        List<KpiLoggerContext> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            long jobId = props.get(0).getId();
            return jobId + 1;
        }
        return 1L;
    }
}

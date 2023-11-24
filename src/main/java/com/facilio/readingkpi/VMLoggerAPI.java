package com.facilio.readingkpi;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingkpi.context.KpiLoggerContext;
import com.facilio.readingkpi.context.KpiResourceLoggerContext;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.v3.context.Constants;

public class VMLoggerAPI {

	
	public static long insertLog(Long vmId, Integer kpiType, Boolean isSysCreated, Integer resourceCount) throws Exception {
        return insertLog(vmId, kpiType, null, null, isSysCreated, resourceCount);
    }

    public static long insertLog(Long vmId, Integer kpiType, Long intervalStartTime, Long intervalEndTime, Boolean isSysCreated, Integer resourceCount) throws Exception {
        ModuleBean modBean = Constants.getModBean();

        VirtualMeterTemplateReadingContext vm = new VirtualMeterTemplateReadingContext();
        vm.setId(vmId);

        KpiLoggerContext vmLoggerContext = new KpiLoggerContext(
        		vm,
                kpiType,
                KpiResourceLoggerContext.KpiLoggerStatus.IN_PROGRESS.getIndex(),
                isSysCreated,
                System.currentTimeMillis(),
                resourceCount
        );
        vmLoggerContext.setStartTime(intervalStartTime);
        vmLoggerContext.setEndTime(intervalEndTime);

        vmLoggerContext.setSysCreatedBy(AccountUtil.getCurrentUser());
        vmLoggerContext.setSysCreatedTime(System.currentTimeMillis());


        InsertRecordBuilder<KpiLoggerContext> insertRecordBuilder = new InsertRecordBuilder<KpiLoggerContext>()
                .moduleName(FacilioConstants.Meter.VIRTUAL_METER_LOGGER)
                .fields(modBean.getAllFields(FacilioConstants.Meter.VIRTUAL_METER_LOGGER));
        return insertRecordBuilder.insert(vmLoggerContext);
    }
    
    
    public static void insertResourceLog(Long vmId, Long parentLoggerId, Long resourceId, Long intervalStartTime, Long intervalEndTime, Boolean isHistorical) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        KpiResourceLoggerContext vmResourceLoggerContext = new KpiResourceLoggerContext(
        		vmId,
                parentLoggerId,
                resourceId,
                intervalStartTime,
                intervalEndTime,
                System.currentTimeMillis(),
                KpiResourceLoggerContext.KpiLoggerStatus.IN_PROGRESS.getIndex(),
                isHistorical
        );
        vmResourceLoggerContext.setVmId(vmId);
        vmResourceLoggerContext.setSysCreatedBy(AccountUtil.getCurrentUser());
        vmResourceLoggerContext.setSysCreatedTime(System.currentTimeMillis());

        InsertRecordBuilder<KpiResourceLoggerContext> insertRecordBuilder = new InsertRecordBuilder<KpiResourceLoggerContext>()
                .moduleName(FacilioConstants.Meter.VIRTUAL_METER_RESOURCE_LOGGER)
                .fields(modBean.getAllFields(FacilioConstants.Meter.VIRTUAL_METER_RESOURCE_LOGGER));
        
        insertRecordBuilder.insert(vmResourceLoggerContext);
    }
    
    public static void updateLogWithId(Long id, Integer status, Long execEndTime, Integer successCount) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Meter.VIRTUAL_METER_LOGGER);

        KpiLoggerContext vmLoggerContext = new KpiLoggerContext();
        vmLoggerContext.setExecEndTime(execEndTime);
        vmLoggerContext.setStatus(status);
        if (successCount != null) {
        	vmLoggerContext.setSuccessCount(successCount);
        }
        UpdateRecordBuilder<KpiLoggerContext> updateRecordBuilder = new UpdateRecordBuilder<KpiLoggerContext>()
                .moduleName(FacilioConstants.Meter.VIRTUAL_METER_LOGGER)
                .fields(fields)
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(modBean.getModule(FacilioConstants.Meter.VIRTUAL_METER_LOGGER)), String.valueOf(id), NumberOperators.EQUALS));

        updateRecordBuilder.update(vmLoggerContext);
    }
    
    public static List<Map<String, Object>> getResourceLoggersForParentLogger(Long parentLoggerId) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule vmResourceLoggerModule = modBean.getModule(FacilioConstants.Meter.VIRTUAL_METER_RESOURCE_LOGGER);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Meter.VIRTUAL_METER_RESOURCE_LOGGER);
        fields.add(FieldFactory.getIdField(vmResourceLoggerModule));
        fields = fields.stream().filter(x -> !Objects.equals(x.getName(), "sysCreatedBy")).collect(Collectors.toList());
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(vmResourceLoggerModule.getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("parentLoggerId"), Collections.singleton(parentLoggerId), NumberOperators.EQUALS));
        return builder.get();
    }
    
    
    public static void updateResourceLog(Long id, KpiResourceLoggerContext kpiResourceLoggerContext) throws Exception {

        ModuleBean modBean = Constants.getModBean();
        FacilioModule vmResourceLoggerModule = modBean.getModule(FacilioConstants.Meter.VIRTUAL_METER_RESOURCE_LOGGER);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Meter.VIRTUAL_METER_RESOURCE_LOGGER);
        
        UpdateRecordBuilder<KpiResourceLoggerContext> updateRecordBuilder = new UpdateRecordBuilder<KpiResourceLoggerContext>()
                .module(vmResourceLoggerModule)
                .fields(fields)
                .andCondition(CriteriaAPI.getIdCondition(id, vmResourceLoggerModule));

        updateRecordBuilder.update(kpiResourceLoggerContext);
    }
    
    
}

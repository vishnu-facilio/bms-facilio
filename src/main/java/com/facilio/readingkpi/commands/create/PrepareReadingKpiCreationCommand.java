package com.facilio.readingkpi.commands.create;

import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.ns.NamespaceConstants;
import com.facilio.ns.context.NSType;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.readingkpi.context.KPIType;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrepareReadingKpiCreationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ReadingKPIContext> list = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(list)) {
            for (ReadingKPIContext kpi : list) {
                setContextForNsAndWorkflow(context, kpi);
                String kpiName = kpi.getName();
                context.put(FacilioConstants.ContextNames.READING_NAME, kpiName);
                if (kpi.getKpiTypeEnum() != KPIType.DYNAMIC) {
                    addReadingRelatedFieldsAndDataToContext(context, kpi, kpiName);
                } else {
                    kpi.setReadingFieldId(null);
                    kpi.setReadingModuleId(null);
                }
                setReadingParent(kpi, context);
                ReadingKpiAPI.setCategory(kpi);
            }
        }
        return false;
    }

    private void addReadingRelatedFieldsAndDataToContext(Context context, ReadingKPIContext kpi, String kpiName) {
        NumberField field = FieldFactory.getField(null, kpiName, "RESULT", null, FieldType.DECIMAL);
        String customUnit = kpi.getCustomUnit();
        if (customUnit != null) {
            field.setUnit(customUnit);
            field.setMetric(0);
        } else {
            int unitId = 0;
            if (kpi.getUnitId() != null) {
                unitId = kpi.getUnitId();
            }
            if (unitId > 0) {
                field.setUnitId(unitId);
                field.setMetric(kpi.getMetricId());
            }
        }
        field.setDisplayType(FacilioField.FieldDisplayType.ENPI);
        kpi.setReadingField(field);
        ArrayList<FacilioField> fieldList = new ArrayList<FacilioField>() {
            {
                add(kpi.getReadingField());
            }
        };
        context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fieldList);

        context.put(FacilioConstants.ContextNames.MODULE_TYPE, getModuleTypeFromKpiType(kpi.getKpiTypeEnum()));
        context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, FacilioConstants.ReadingKpi.READING_KPI_READINGS_TABLE);
    }

    private void setReadingParent(ReadingKPIContext kpi, Context context) throws Exception {
        switch (kpi.getResourceTypeEnum()) {
            case ASSET_CATEGORY:
                setAssetCategory(kpi);//TODO:remove once assetCategory removed
                context.put(FacilioConstants.ContextNames.PARENT_MODULE, FacilioConstants.ContextNames.ASSET_CATEGORY);
                context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, kpi.getCategoryId());
                context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
                break;
            case METER_CATEGORY:
                context.put(FacilioConstants.ContextNames.PARENT_MODULE, FacilioConstants.ContextNames.METER);
                context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, kpi.getCategoryId());
                break;
            case SITE:
                context.put(FacilioConstants.ContextNames.PARENT_MODULE, FacilioConstants.ContextNames.SITE);
                context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, kpi.getCategoryId());
                break;
        }
    }

    private void setAssetCategory(ReadingKPIContext kpi) {
        AssetCategoryContext assetCategoryContext=new AssetCategoryContext();
        assetCategoryContext.setId(kpi.getCategoryId());
        kpi.setAssetCategory(assetCategoryContext);
    }

    public FacilioModule.ModuleType getModuleTypeFromKpiType(KPIType type) {
        switch (type) {
            case SCHEDULED:
                return FacilioModule.ModuleType.SCHEDULED_FORMULA;
            case LIVE:
                return FacilioModule.ModuleType.LIVE_FORMULA;
        }
        return null;
    }

    public void setContextForNsAndWorkflow(Context context, ReadingKPIContext kpi) throws Exception {
        WorkflowContext workflow = kpi.getNs().getWorkflowContext();
        if (workflow != null) {
            context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
        }

        context.put(NamespaceConstants.NAMESPACE_FIELDS, kpi.getNs().getFields());
        context.put(NamespaceConstants.NAMESPACE, kpi.getNs());
        kpi.getNs().setExecInterval(kpi.getFrequencyEnum() != null ? kpi.getFrequencyEnum().getMs() : null);
        kpi.getNs().setType(NSType.KPI_RULE.getIndex());
        kpi.getNs().setStatus(kpi.getKpiTypeEnum() == KPIType.LIVE);
    }
}

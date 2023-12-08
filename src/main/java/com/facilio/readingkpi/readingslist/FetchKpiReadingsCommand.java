package com.facilio.readingkpi.readingslist;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.connected.CommonConnectedUtil;
import com.facilio.connected.ResourceType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;

public class FetchKpiReadingsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        String groupBy = (String) context.get(FacilioConstants.ContextNames.REPORT_GROUP_BY);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule readingKpiModule = modBean.getModule(FacilioConstants.ReadingKpi.READING_KPI);
        List<FacilioField> fields = modBean.getAllFields(readingKpiModule.getName());
        Map<String, FacilioField> readingKpiFieldMap = FieldFactory.getAsMap(fields);

        FacilioModule rdmModule = ModuleFactory.getReadingDataMetaModule();
        Map<String, FacilioField> rdmFieldMap = FieldFactory.getAsMap(FieldFactory.getReadingDataMetaFields());

        FacilioModule fieldsModule = ModuleFactory.getNumberFieldModule();
        Map<String, FacilioField> fieldsFieldMap = FieldFactory.getAsMap(FieldFactory.getNumberFieldFields());

        List<FacilioField> selectFields = new ArrayList<>();
        selectFields.add(readingKpiFieldMap.get("resourceType"));
        selectFields.add(readingKpiFieldMap.get("categoryId"));

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(rdmModule.getTableName())
                .innerJoin(readingKpiModule.getTableName()).on(readingKpiFieldMap.get("readingFieldId").getCompleteColumnName() + "=" + rdmFieldMap.get("fieldId").getCompleteColumnName())
                .andCondition(CriteriaAPI.getCondition(rdmFieldMap.get("value"), "-1", NumberOperators.NOT_EQUALS))
                .andCondition(CriteriaAPI.getCondition(readingKpiModule.getTableName() + ".SYS_DELETED", "sysDeleted", String.valueOf(Boolean.FALSE), BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition(readingKpiFieldMap.get("status"), String.valueOf(Boolean.TRUE), BooleanOperators.IS));


        if (groupBy.equals("kpi")) {
            ReadingKPIContext readingKpi = getReadingKpi(recordId);
            Long fieldId = readingKpi.getReadingFieldId();
            List<Long> includedAssetIds = readingKpi.getNs().getIncludedAssetIds();
            builder.andCondition(CriteriaAPI.getCondition(rdmFieldMap.get("fieldId"), String.valueOf(fieldId), NumberOperators.EQUALS))
                    .innerJoin(fieldsModule.getTableName()).on(fieldsFieldMap.get("fieldId").getCompleteColumnName() + "=" + rdmFieldMap.get("fieldId").getCompleteColumnName());


            String resourceTypeModule = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            joinTablesBasedOnResourceType(resourceTypeModule, builder, selectFields, rdmFieldMap);

            if (CollectionUtils.isNotEmpty(includedAssetIds)) {
                builder.andCondition(CriteriaAPI.getCondition(rdmFieldMap.get("resourceId"), includedAssetIds, NumberOperators.EQUALS));
            }
        } else {
            builder.innerJoin(fieldsModule.getTableName()).on(fieldsFieldMap.get("fieldId").getCompleteColumnName() + "=" + rdmFieldMap.get("fieldId").getCompleteColumnName())
                    .andCondition(CriteriaAPI.getCondition(rdmFieldMap.get("resourceId"), String.valueOf(recordId), NumberOperators.EQUALS));
            selectFields.add(readingKpiFieldMap.get("name"));
        }

        getSelectFields(rdmFieldMap, fieldsFieldMap, selectFields);
        builder.select(selectFields);

        List<Map<String, Object>> records = fetchData(context, builder);

        long count = fetchCount(rdmFieldMap, builder, 0);

        context.put(FacilioConstants.ContextNames.COUNT, count);
        context.put(FacilioConstants.ContextNames.DATA, records);

        return false;
    }

    private static long fetchCount(Map<String, FacilioField> rdmFieldMap, GenericSelectRecordBuilder builder, long count) throws Exception {
        builder.select(new HashSet<>()).aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, rdmFieldMap.get("id"));

        Map<String, Object> props = builder.offset(0).fetchFirst();
        if (MapUtils.isNotEmpty(props)) {
            count = (long) props.get("id");
        }
        return count;
    }

    private static void getSelectFields(Map<String, FacilioField> rdmFieldMap, Map<String, FacilioField> fieldsFieldMap, List<FacilioField> selectFields) {
        selectFields.add(rdmFieldMap.get("fieldId"));
        selectFields.add(rdmFieldMap.get("unit"));
        selectFields.add(rdmFieldMap.get("resourceId"));
        selectFields.add(rdmFieldMap.get("value"));
        selectFields.add(rdmFieldMap.get("ttime"));

        FacilioField fieldsUnitField = fieldsFieldMap.get("unit");
        fieldsUnitField.setName("unitLabel");
        selectFields.add(fieldsUnitField);
        selectFields.add(fieldsFieldMap.get("metric"));
    }

    private static List<Map<String, Object>> fetchData(Context context, GenericSelectRecordBuilder builder) throws Exception {
        ReadingKpiAPI.addFilterToBuilder(context, null, builder);
        ReadingKpiAPI.addPaginationPropsToBuilder(context, builder);
        List<Map<String, Object>> records = builder.get();
        for (Map<String, Object> record : records) {
            setReadingField(record);
            setCategoryModuleName(record);
            setMetricAndUnit(record);
        }
        return records;
    }

    private static void setReadingField(Map<String, Object> record) throws Exception {
        Long fieldId = (Long) record.get("fieldId");
        record.put("readingField", Constants.getModBean().getField(fieldId));
    }

    private static void setCategoryModuleName(Map<String, Object> record) throws Exception {
        int resourceType = (int) record.get("resourceType");
        Long categoryId = (Long) record.getOrDefault("categoryId",null);
        ResourceType resourceType1 = ResourceType.valueOf(resourceType);
        String moduleNameFromCategory = CommonConnectedUtil.getModuleNameFromCategory(resourceType1, categoryId);
        record.put("parentModuleName", moduleNameFromCategory);
    }

    private static void setMetricAndUnit(Map<String, Object> record) throws Exception {
        if (record.get("unit") == null) return;
        NumberField field = new NumberField();
        field.setMetric((Integer) record.get("metric"));
        field.setUnitId((Integer) record.get("unit"));

        Double convertedValue = (Double) UnitsUtil.convertToDisplayUnit(record.get("value"), field);
        record.replace("value", convertedValue);
    }

    private void joinTablesBasedOnResourceType(String moduleName, GenericSelectRecordBuilder builder, List<FacilioField> selectFields, Map<String, FacilioField> rdmFieldMap) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);// moduleName would be meter, asset, site or space
        builder.innerJoin(module.getTableName()).on(module.getTableName() + ".ID" + "=" + rdmFieldMap.get("resourceId").getCompleteColumnName());

        if (!FacilioConstants.Meter.METER.equals(moduleName)) {
            // join resource table if chosen resource type(moduleName) is not meter
            FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
            String resourceTable = resourceModule.getTableName();
            Map<String, FacilioField> resourceFieldMap = FieldFactory.getAsMap(modBean.getAllFields(resourceModule.getName()));
            FacilioField resourceNameField = resourceFieldMap.get("name").clone();
            selectFields.add(resourceNameField);
            builder.innerJoin(resourceTable).on(module.getTableName() + ".ID" + "=" + resourceTable + ".ID");
        } else {
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Meter.METER);
            FacilioField meterNameField = FieldFactory.getAsMap(fields).get("name");
            selectFields.add(meterNameField);
        }

    }

    public ReadingKPIContext getReadingKpi(Long recordId) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule readingKpiModule = modBean.getModule(FacilioConstants.ReadingKpi.READING_KPI);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getModuleFields(FacilioConstants.ReadingKpi.READING_KPI));
        SelectRecordsBuilder<ReadingKPIContext> builder = new SelectRecordsBuilder<ReadingKPIContext>()
                .select(Collections.singleton(fieldsMap.get("readingFieldId")))
                .module(readingKpiModule)
                .beanClass(ReadingKPIContext.class)
                .andCondition(CriteriaAPI.getIdCondition(recordId, readingKpiModule));
        List<ReadingKPIContext> kpis = builder.get();
        if (CollectionUtils.isEmpty(kpis)) {
            throw new IllegalArgumentException("Invalid Kpi Id");
        }
        NameSpaceContext ns = NamespaceAPI.getNameSpaceByRuleId(recordId, NSType.KPI_RULE);
        ReadingKPIContext kpi = kpis.get(0);
        kpi.setNs(ns);
        return kpi;
    }
}

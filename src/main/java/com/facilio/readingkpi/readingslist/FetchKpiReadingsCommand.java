package com.facilio.readingkpi.readingslist;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.readingkpi.context.KPIType;
import com.facilio.readingkpi.context.ReadingKPIContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class FetchKpiReadingsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        String groupBy = (String) context.get(FacilioConstants.ContextNames.REPORT_GROUP_BY);
        Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        KPIType kpiType = KPIType.valueOf((String) context.get(FacilioConstants.ReadingKpi.KPI_TYPE));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule readingKpiModule = modBean.getModule(FacilioConstants.ReadingKpi.READING_KPI);
        List<FacilioField> fields = modBean.getAllFields(readingKpiModule.getName());
        Map<String, FacilioField> readingKpiFieldMap = FieldFactory.getAsMap(fields);

        FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
        String resourceTable = resourceModule.getTableName();
        Map<String, FacilioField> resourceFieldMap = FieldFactory.getAsMap(modBean.getAllFields(resourceModule.getName()));
        FacilioField resourceNameField = resourceFieldMap.get("name").clone();

        // remove this inner join to assets once space support is added
        FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        Map<String, FacilioField> assetFieldMap = FieldFactory.getAsMap(modBean.getAllFields(assetModule.getName()));
        String assetTableName = assetModule.getTableName();

        FacilioModule rdmModule = ModuleFactory.getReadingDataMetaModule();
        Map<String, FacilioField> rdmFieldMap = FieldFactory.getAsMap(FieldFactory.getReadingDataMetaFields());

        FacilioModule fieldsModule = ModuleFactory.getNumberFieldModule();
        Map<String, FacilioField> fieldsFieldMap = FieldFactory.getAsMap(FieldFactory.getNumberFieldFields());

        List<FacilioField> selectFields = new ArrayList<>();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(rdmModule.getTableName())
                .andCondition(CriteriaAPI.getCondition(rdmFieldMap.get("value"), "-1", NumberOperators.NOT_EQUALS))
                .andCondition(CriteriaAPI.getCondition(readingKpiFieldMap.get("kpiType"), String.valueOf(kpiType.getIndex()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(readingKpiModule.getTableName() + ".SYS_DELETED", "sysDeleted", String.valueOf(Boolean.FALSE), BooleanOperators.IS));


        if (groupBy.equals("kpi")) {
            ReadingKPIContext readingKpi = ReadingKpiAPI.getReadingKpi(recordId);
            Long fieldId = readingKpi.getReadingFieldId();
            List<Long> includedAssetIds = readingKpi.getNs().getIncludedAssetIds();
            selectFields.add(resourceNameField);
            builder.andCondition(CriteriaAPI.getCondition(rdmFieldMap.get("fieldId"), String.valueOf(fieldId), NumberOperators.EQUALS))
                    .innerJoin(resourceTable).on(rdmFieldMap.get("resourceId").getCompleteColumnName() + "=" + resourceTable + ".ID")
                    .innerJoin(assetModule.getTableName()).on(assetTableName + ".ID" + "=" + resourceTable + ".ID")
                    .innerJoin(fieldsModule.getTableName()).on(fieldsFieldMap.get("fieldId").getCompleteColumnName() + "=" + rdmFieldMap.get("fieldId").getCompleteColumnName())
                    .innerJoin(readingKpiModule.getTableName()).on(readingKpiFieldMap.get("readingFieldId").getCompleteColumnName() + "=" + rdmFieldMap.get("fieldId").getCompleteColumnName());
            if (CollectionUtils.isNotEmpty(includedAssetIds)) {
                builder.andCondition(CriteriaAPI.getCondition(rdmFieldMap.get("resourceId"), includedAssetIds, NumberOperators.EQUALS));
            }
        } else {
            builder.innerJoin(readingKpiModule.getTableName()).on(readingKpiFieldMap.get("readingFieldId").getCompleteColumnName() + "=" + rdmFieldMap.get("fieldId").getCompleteColumnName())
                    .innerJoin(fieldsModule.getTableName()).on(fieldsFieldMap.get("fieldId").getCompleteColumnName() + "=" + rdmFieldMap.get("fieldId").getCompleteColumnName())
                    .andCondition(CriteriaAPI.getCondition(rdmFieldMap.get("resourceId"), String.valueOf(recordId), NumberOperators.EQUALS));
            selectFields.add(readingKpiFieldMap.get("name"));
        }

        selectFields.add(rdmFieldMap.get("fieldId"));
        selectFields.add(fieldsFieldMap.get("unit"));
        selectFields.add(rdmFieldMap.get("resourceId"));
        selectFields.add(rdmFieldMap.get("value"));
        selectFields.add(rdmFieldMap.get("ttime"));

        if (filterCriteria != null)
            builder.andCriteria(filterCriteria);
        builder.select(selectFields);

        int page = (int) context.get(FacilioConstants.ContextNames.PAGE);
        int perPage = (int) context.get(FacilioConstants.ContextNames.PER_PAGE);

        if (perPage != -1) {
            int offset = ((page - 1) * perPage);
            if (offset < 0) {
                offset = 0;
            }

            builder.offset(offset);
            builder.limit(perPage);

        }

        List<Map<String, Object>> records = builder.get();

        builder.select(new HashSet<>()).aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, rdmFieldMap.get("id"));

        long count = 0;
        Map<String, Object> props = builder.offset(0).fetchFirst();
        if (MapUtils.isNotEmpty(props)) {
            count = (long) props.get("id");
        }

        context.put(FacilioConstants.ContextNames.COUNT, count);
        context.put(FacilioConstants.ContextNames.DATA, records);


        return false;
    }
}

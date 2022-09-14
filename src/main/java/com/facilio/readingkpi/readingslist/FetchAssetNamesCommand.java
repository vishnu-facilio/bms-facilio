package com.facilio.readingkpi.readingslist;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class FetchAssetNamesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Map<String, Object>> assets = new ArrayList<>();

        List<Map<String, Object>> assetProps = fetchBuilder(context, false).get();
        for (Map<String, Object> prop : assetProps) {
            Map<String, Object> row = new HashMap<>();
            row.put("name", prop.get("name"));
            row.put("id", prop.get("resourceId"));
            assets.add(row);
        }

        long count = 0;
        Map<String, Object> countProps = fetchBuilder(context,true).fetchFirst();
        if (MapUtils.isNotEmpty(countProps)) {
            count = (long) countProps.get("id");
        }

        context.put(FacilioConstants.ContextNames.COUNT, count);
        context.put(FacilioConstants.ContextNames.ASSETS, assets);
        return false;
    }

    private GenericSelectRecordBuilder fetchBuilder(Context context, Boolean fetchCount) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule readingKpiModule = modBean.getModule(FacilioConstants.ReadingKpi.READING_KPI);
        List<FacilioField> fields = modBean.getAllFields(readingKpiModule.getName());
        Map<String, FacilioField> readingKpiFieldMap = FieldFactory.getAsMap(fields);

        FacilioModule rdmModule = ModuleFactory.getReadingDataMetaModule();
        Map<String, FacilioField> rdmFieldMap = FieldFactory.getAsMap(FieldFactory.getReadingDataMetaFields());

        FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
        String resourceTable = resourceModule.getTableName();
        Map<String, FacilioField> resourceFieldMap = FieldFactory.getAsMap(modBean.getAllFields(resourceModule.getName()));

        FacilioField resourceIdField = FieldFactory.getIdField(resourceModule);
        resourceIdField.setColumnName("DISTINCT(" + resourceIdField.getCompleteColumnName() + ")");
        resourceIdField.setModule(null);

        List<FacilioField> rdmSelectFields = Arrays.asList(resourceIdField, resourceFieldMap.get("name"), rdmFieldMap.get("resourceId"));
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(rdmModule.getTableName())
                .innerJoin(readingKpiModule.getTableName()).on(rdmFieldMap.get("fieldId").getCompleteColumnName() + "=" + readingKpiFieldMap.get("readingFieldId").getCompleteColumnName())
                .innerJoin(resourceTable).on(rdmFieldMap.get("resourceId").getCompleteColumnName() + "=" + resourceTable + ".ID")
                .andCondition(CriteriaAPI.getCondition(readingKpiFieldMap.get("status"), "true", BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition(rdmFieldMap.get("value"), "-1", NumberOperators.NOT_EQUALS));

        if (!fetchCount) {
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
            builder.select(rdmSelectFields);

        } else {
            builder.select(new HashSet<>()).aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, resourceIdField);
        }
        String searchText = (String) context.get(FacilioConstants.ContextNames.SEARCH_QUERY);
        if (StringUtils.isNotEmpty(searchText)) {
            builder.andCondition(CriteriaAPI.getCondition(resourceFieldMap.get("name"), searchText, StringOperators.CONTAINS));

        }
        return builder;
    }
}

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
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingkpi.context.KPIType;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.mockito.internal.runners.StrictRunner;

import java.util.*;

public class FetchKpiNamesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Map<String, Object>> kpis = new ArrayList<>();

        List<Map<String, Object>> props = fetchBuilder(context, false).get();
        for (Map<String, Object> prop : props) {
            Map<String, Object> row = new HashMap<>();
            row.put("name", prop.get("name"));
            row.put("id", prop.get("id"));
            row.put("frequency", prop.get("frequency"));

            kpis.add(row);
        }

        long count = 0;
        Map<String, Object> countProps = fetchBuilder(context, true).fetchFirst();
        if (MapUtils.isNotEmpty(countProps)) {
            count = (long) countProps.get("id");
        }
        context.put(FacilioConstants.ContextNames.COUNT, count);
        context.put(FacilioConstants.ContextNames.KPI_LIST, kpis);
        return false;
    }

    private GenericSelectRecordBuilder fetchBuilder(Context context, Boolean fetchCount) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule readingKpiModule = modBean.getModule(FacilioConstants.ReadingKpi.READING_KPI);
        List<FacilioField> fields = modBean.getAllFields(readingKpiModule.getName());
        Map<String, FacilioField> readingKpiFieldMap = FieldFactory.getAsMap(fields);

        FacilioField readingKpiIdField = FieldFactory.getIdField(readingKpiModule);

        FacilioField selectDistinctField = readingKpiIdField.clone();
        selectDistinctField.setColumnName("DISTINCT(" + readingKpiIdField.getCompleteColumnName() + ")");
        selectDistinctField.setModule(null);

        List<FacilioField> selectFields = Arrays.asList(selectDistinctField, readingKpiFieldMap.get("name"), readingKpiFieldMap.get("frequency"));

        FacilioModule rdmModule = ModuleFactory.getReadingDataMetaModule();
        Map<String, FacilioField> rdmFieldMap = FieldFactory.getAsMap(FieldFactory.getReadingDataMetaFields());

        String kpiTypeString = (String) context.get(FacilioConstants.ReadingKpi.KPI_TYPE);
        KPIType kpiType = KPIType.valueOf(kpiTypeString.toUpperCase());
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(rdmModule.getTableName())
                .innerJoin(readingKpiModule.getTableName()).on(rdmFieldMap.get("fieldId").getCompleteColumnName() + "=" + readingKpiFieldMap.get("readingFieldId").getCompleteColumnName())
                .andCondition(CriteriaAPI.getCondition(readingKpiFieldMap.get("status"), "true", BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition(rdmFieldMap.get("value"), "-1", NumberOperators.NOT_EQUALS))
                .andCondition(CriteriaAPI.getCondition(readingKpiFieldMap.get("kpiType"), String.valueOf(kpiType.getIndex()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(readingKpiModule.getTableName() +".SYS_DELETED", "sysDeleted", String.valueOf(Boolean.FALSE), BooleanOperators.IS));



        if (!fetchCount) {
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
        } else {
            builder.select(new HashSet<>()).aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, selectDistinctField);
        }
        String searchText = (String) context.get(FacilioConstants.ContextNames.SEARCH_QUERY);
        if (StringUtils.isNotEmpty(searchText)) {
            builder.andCondition(CriteriaAPI.getCondition(readingKpiFieldMap.get("name"), searchText, StringOperators.CONTAINS));
        }
        ArrayList<Long> frequencies = (ArrayList) context.get(FacilioConstants.ContextNames.FREQUENCY);
        if (CollectionUtils.isNotEmpty(frequencies)) {
            builder.andCondition(CriteriaAPI.getCondition(readingKpiFieldMap.get("frequency"), frequencies, NumberOperators.EQUALS));
        }
        return builder;
    }

}

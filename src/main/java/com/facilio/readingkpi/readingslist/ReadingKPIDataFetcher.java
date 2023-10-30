package com.facilio.readingkpi.readingslist;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReadingKPIDataFetcher extends KpiAnalyticsDataFetcher {
    public ReadingKPIDataFetcher(FacilioModule module, Context context, List<FacilioField> additionalSelectFields) throws Exception {
        super(module, context, additionalSelectFields);
    }

    @Override
    public GenericSelectRecordBuilder fetchModuleBuilder() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule readingKpiModule = modBean.getModule(FacilioConstants.ReadingKpi.READING_KPI);
        List<FacilioField> fields = modBean.getAllFields(readingKpiModule.getName());
        Map<String, FacilioField> readingKpiFieldMap = FieldFactory.getAsMap(fields);

        FacilioModule rdmModule = ModuleFactory.getReadingDataMetaModule();
        Map<String, FacilioField> rdmFieldMap = FieldFactory.getAsMap(FieldFactory.getReadingDataMetaFields());

        Long resourceType = (Long) context.get(FacilioConstants.ContextNames.RESOURCE_TYPE);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(readingKpiModule.getTableName())
                .leftJoin(rdmModule.getTableName()).on(rdmFieldMap.get("fieldId").getCompleteColumnName() + "=" + readingKpiFieldMap.get("readingFieldId").getCompleteColumnName())
                .andCondition(CriteriaAPI.getCondition(readingKpiFieldMap.get("status"), "true", BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition(rdmFieldMap.get("value"), "-1", NumberOperators.NOT_EQUALS))
                .andCondition(CriteriaAPI.getCondition(readingKpiFieldMap.get("resourceType"), String.valueOf(resourceType), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(readingKpiModule.getTableName() + ".SYS_DELETED", "sysDeleted", String.valueOf(Boolean.FALSE), BooleanOperators.IS));

        ArrayList<Long> frequencies = (ArrayList) context.get(FacilioConstants.ContextNames.FREQUENCY);
        if (CollectionUtils.isNotEmpty(frequencies)) {
            builder.andCondition(CriteriaAPI.getCondition(readingKpiFieldMap.get("frequency"), frequencies, NumberOperators.EQUALS));
        }
        return builder;
    }
}

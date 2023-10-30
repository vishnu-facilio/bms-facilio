package com.facilio.readingkpi.readingslist;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.List;

public class MeterDataFetcher extends KpiAnalyticsDataFetcher {
    public MeterDataFetcher(FacilioModule module, Context context, List<FacilioField> additionSelectFields) throws Exception {
        super(module, context, additionSelectFields);
    }

    @Override
    protected GenericSelectRecordBuilder fetchModuleBuilder() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Meter.METER);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Meter.METER);
        FacilioField utilityTypeField = FieldFactory.getAsMap(fields).get("utilityType");

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .orderBy(module.getTableName() + ".ID DESC");

        Long utilityType = (Long) context.get(FacilioConstants.Meter.UTILITY_TYPE);
        if (utilityType != null) {
            builder.andCondition(CriteriaAPI.getCondition(utilityTypeField, String.valueOf(utilityType), PickListOperators.IS));
        }

        return builder;
    }
}

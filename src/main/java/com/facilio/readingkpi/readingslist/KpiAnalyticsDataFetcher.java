package com.facilio.readingkpi.readingslist;

import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

import static com.facilio.readingkpi.ReadingKpiAPI.getCountBuilder;
import static com.facilio.readingkpi.ReadingKpiAPI.getDataBuilder;

public abstract class KpiAnalyticsDataFetcher {
    FacilioModule module;
    Map<String, FacilioField> fieldsMap;
    List<FacilioField> additionSelectFields;
    Context context;

    public KpiAnalyticsDataFetcher(FacilioModule module, Context context, List<FacilioField> additionSelectFields) throws Exception {
        this.module = module;
        this.fieldsMap = getFieldsMap();
        this.additionSelectFields = additionSelectFields;
        this.context = context;
    }

    public Map<String, FacilioField> getFieldsMap() throws Exception {
        return FieldFactory.getAsMap(Constants.getModBean().getAllFields(module.getName()));
    }
    protected abstract GenericSelectRecordBuilder fetchModuleBuilder() throws Exception;

    public GenericSelectRecordBuilder fetchBuilder(Context context, Boolean fetchCount) throws Exception {
        GenericSelectRecordBuilder builder = fetchModuleBuilder();
        ReadingKpiAPI.addFilterToBuilder(context, fieldsMap , builder);
        return fetchCount
                ? getCountBuilder(builder, module)
                : getDataBuilder(context, module, fieldsMap, builder, additionSelectFields);


    }
}

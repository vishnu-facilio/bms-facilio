package com.facilio.readingkpi.readingslist;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AssetDataFetcher extends KpiAnalyticsDataFetcher {

    public AssetDataFetcher(FacilioModule module, Context context, List<FacilioField> additionSelectFields) throws Exception {
        super(module, context, additionSelectFields);
    }

    @Override
    protected GenericSelectRecordBuilder fetchModuleBuilder() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(assetModule.getName()));

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(assetModule.getTableName())
                .innerJoin(resourceModule.getTableName())
                .on(resourceModule.getTableName() + ".ID=" + assetModule.getTableName() + ".ID")
                .orderBy(assetModule.getTableName() + ".ID DESC");
        Long categoryId = (Long) context.get(FacilioConstants.ReadingKpi.RESOURCE_CATEGORY_ID);
        if (categoryId != null) {
            builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("category"), Collections.singleton(categoryId), NumberOperators.EQUALS));
        }
        return builder;
    }
}

package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AggregationMetaContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AggregationAPI {
    public static AggregationMetaContext getAggregationMeta(Long id) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAggregationMetaModule().getTableName())
                .select(FieldFactory.getAggregationMetaFields())
                .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getAggregationMetaModule()));
        AggregationMetaContext aggregationMeta = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), AggregationMetaContext.class);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        return aggregationMeta;
    }
}

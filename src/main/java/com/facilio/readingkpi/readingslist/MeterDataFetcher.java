package com.facilio.readingkpi.readingslist;

import com.facilio.beans.ModuleBean;
import com.facilio.connected.ResourceType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.facilio.readingkpi.ReadingKpiAPI.getInclResIdsFromProps;

public class MeterDataFetcher extends KpiAnalyticsDataFetcher {

    public MeterDataFetcher(FacilioModule module, Context context, List<FacilioField> additionSelectFields) throws Exception {
        super(module, context, additionSelectFields);
    }

    @Override
    protected GenericSelectRecordBuilder fetchModuleBuilder() throws Exception {

        Long utilityType = (Long) context.get(FacilioConstants.ReadingKpi.RESOURCE_CATEGORY_ID);
        List<Map<String, Object>> props =  ReadingKpiAPI.getMatchedResourcesOfAllKpis(utilityType, ResourceType.METER_CATEGORY);
        if (props == null || props.isEmpty()) {
            return null;
        }
        Set<Long> inclResIds = getInclResIdsFromProps(props);

        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.METER);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.METER));
        FacilioField utilityTypeField= fieldsMap.get("utilityType");
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition(utilityTypeField, String.valueOf(utilityType), PickListOperators.IS))
                .andCondition(CriteriaAPI.getCondition(module.getTableName() + ".SYS_DELETED", "sysDeleted", String.valueOf(Boolean.FALSE), BooleanOperators.IS));;
        if (CollectionUtils.isNotEmpty(inclResIds)) {
            selectBuilder.andCondition(CriteriaAPI.getIdCondition(inclResIds, module));
        }
        return selectBuilder;
    }
}

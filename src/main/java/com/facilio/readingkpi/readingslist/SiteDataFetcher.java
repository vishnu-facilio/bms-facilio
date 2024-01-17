package com.facilio.readingkpi.readingslist;

import com.facilio.beans.ModuleBean;
import com.facilio.connected.ResourceType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.facilio.readingkpi.ReadingKpiAPI.getInclResIdsFromProps;

public class SiteDataFetcher extends KpiAnalyticsDataFetcher {

    public SiteDataFetcher(FacilioModule module, Context context, List<FacilioField> additionSelectFields) throws Exception {
        super(module, context, additionSelectFields);
    }

    @Override
    protected GenericSelectRecordBuilder fetchModuleBuilder() throws Exception {

        List<Map<String, Object>> props = ReadingKpiAPI.getMatchedResourcesOfAllKpis(null, ResourceType.SITE);
        if (props == null || props.isEmpty()) {
            return null;
        }
        Set<Long> inclResIds = getInclResIdsFromProps(props);
        ModuleBean modBean = Constants.getModBean();
        FacilioModule siteModule = modBean.getModule(FacilioConstants.ContextNames.SITE);
        FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(siteModule.getTableName())
                .innerJoin(resourceModule.getTableName())
                .on(resourceModule.getTableName() + ".ID=" + siteModule.getTableName() + ".ID")
                .orderBy(siteModule.getTableName() + ".ID DESC")
                .andCondition(CriteriaAPI.getCondition(resourceModule.getTableName() + ".SYS_DELETED", "sysDeleted", String.valueOf(Boolean.FALSE), BooleanOperators.IS));
        if (CollectionUtils.isNotEmpty(inclResIds)) {
            selectBuilder.andCondition(CriteriaAPI.getIdCondition(inclResIds, siteModule));
        }

        return selectBuilder;
    }
}


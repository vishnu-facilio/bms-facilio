package com.facilio.readingkpi.readingslist;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.factory.NamespaceModuleAndFieldFactory;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.readingkpi.context.KPIType;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import java.util.*;

import static com.facilio.ns.NamespaceAPI.getNameSpaceByRuleId;

public class FetchDynamicKpiReadingsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        String groupBy = (String) context.get(FacilioConstants.ContextNames.REPORT_GROUP_BY);

        List<String> resourceTypes = new ArrayList<>();
        resourceTypes.add(FacilioConstants.ContextNames.ASSET);
        resourceTypes.add(FacilioConstants.ContextNames.METER);
        resourceTypes.add(FacilioConstants.ContextNames.SITE);

        List<Map<String, Object>> props;
        List<Map<String, Object>> records = new ArrayList<>();

        if (resourceTypes.contains(groupBy)) {
            props = fetchBuilderForResourceSelected(context, recordId, false).get();
        } else {
            NameSpaceContext ns = getNameSpaceByRuleId(recordId, NSType.KPI_RULE);
            List<Long> resourceIds = NamespaceAPI.getMatchedResources(ns, ns.getCategoryId());
            props = fetchBuilderForKpiSelected(context, resourceIds, false).get();

        }

        for (Map<String, Object> prop : props) {
            Map<String, Object> row = new HashMap<>();
            row.put("name", prop.get("name"));
            row.put("id", prop.get("id"));
            records.add(row);
        }

        long count = 0;
        Map<String, Object> countProps = fetchBuilderForResourceSelected(context, recordId, true).fetchFirst();
        if (MapUtils.isNotEmpty(countProps)) {
            count = (long) countProps.get("id");
        }
        context.put(FacilioConstants.ContextNames.COUNT, count);
        context.put(FacilioConstants.ContextNames.DATA, records);


        return false;
    }

    private static GenericSelectRecordBuilder fetchBuilderForKpiSelected(Context context, List<Long> resourceIds, boolean fetchCount) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(resourceIds, module));

        return ReadingKpiAPI.addFilterAndReturnBuilder(context, fetchCount, module, fieldsMap, builder);
    }


    /**
     * Query:
     * SELECT DISTINCT(ReadingKPI.ID) AS `id`, ReadingKPI.NAME AS `name` FROM ReadingKPI
     * INNER JOIN Namespace ON ReadingKPI.ID=Namespace.PARENT_RULE_ID
     * LEFT JOIN Namespace_Inclusions ON Namespace.ID=Namespace_Inclusions.NAMESPACE_ID
     * WHERE ReadingKPI.ORGID = 1 AND
     * (Namespace_Inclusions.RESOURCE_ID = resId OR Namespace_Inclusions.RESOURCE_ID IS NULL)
     * AND ReadingKPI.KPI_TYPE = 3 AND Namespace.TYPE = 3 AND
     * (ReadingKPI.SYS_DELETED IS NULL OR ReadingKPI.SYS_DELETED = false)
     * AND ReadingKPI.STATUS = true
     * AND ReadingKPI.CATEGORY_ID = 1
     * LIMIT 20 OFFSET 0
     *
     */
    private static GenericSelectRecordBuilder fetchBuilderForResourceSelected(Context context, Long recordId, boolean fetchCount) throws Exception {

        ModuleBean modBean = Constants.getModBean();
        FacilioModule kpiModule = modBean.getModule(FacilioConstants.ReadingKpi.READING_KPI);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ReadingKpi.READING_KPI);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

        FacilioModule nsModule = NamespaceModuleAndFieldFactory.getNamespaceModule();
        FacilioModule nsInclModule = NamespaceModuleAndFieldFactory.getNamespaceInclusionModule();
        Map<String, FacilioField> nsInclFieldsMap = FieldFactory.getAsMap(NamespaceModuleAndFieldFactory.getNamespaceInclusionFields());
        Map<String, FacilioField> nsFieldsMap = FieldFactory.getAsMap(NamespaceModuleAndFieldFactory.getNamespaceFields());


        Criteria resIdCriteria = new Criteria();
        resIdCriteria.addAndCondition(CriteriaAPI.getCondition(nsInclFieldsMap.get("resourceId"), Collections.singleton(recordId), NumberOperators.EQUALS));
        resIdCriteria.addOrCondition(CriteriaAPI.getCondition(nsInclFieldsMap.get("resourceId"), CommonOperators.IS_EMPTY));

        Long categoryId = (Long) context.getOrDefault(FacilioConstants.ContextNames.ASSET_CATEGORY_ID, getCategoryForResource(recordId, context));
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(kpiModule.getTableName())
                .innerJoin(nsModule.getTableName())
                .on(kpiModule.getTableName() + ".ID=" + nsModule.getTableName() + ".PARENT_RULE_ID")
                .leftJoin(nsInclModule.getTableName())
                .on(nsModule.getTableName() + ".ID=" + nsInclModule.getTableName() + ".NAMESPACE_ID")
                .andCriteria(resIdCriteria)
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("kpiType"), String.valueOf(KPIType.DYNAMIC.getIndex()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(nsFieldsMap.get("type"), String.valueOf(NSType.KPI_RULE.getIndex()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(kpiModule.getTableName() + ".SYS_DELETED", "sysDeleted", String.valueOf(Boolean.FALSE), BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("status"), String.valueOf(Boolean.TRUE), BooleanOperators.IS));
        if (categoryId > 0) {
            builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("categoryId"), String.valueOf(categoryId), NumberOperators.EQUALS));
        }

        return ReadingKpiAPI.addFilterAndReturnBuilder(context, fetchCount, kpiModule, fieldsMap, builder);
    }

    private static Long getCategoryForResource(Long recordId, Context context) throws Exception {
        String groupBy = (String) context.get(FacilioConstants.ContextNames.REPORT_GROUP_BY);
        switch (groupBy) {
            case FacilioConstants.ContextNames.ASSET:
                return Optional.ofNullable(AssetsAPI.getAssetInfo(recordId))
                        .map(AssetContext::getCategory)
                        .map(ModuleBaseWithCustomFields::getId)
                        .orElse(-1L);
            case FacilioConstants.ContextNames.SITE:
            case FacilioConstants.ContextNames.METER:
            case FacilioConstants.ContextNames.KPI:
                return -1L;
            default:
                throw new IllegalStateException("Unexpected value: " + groupBy);
        }
    }
}

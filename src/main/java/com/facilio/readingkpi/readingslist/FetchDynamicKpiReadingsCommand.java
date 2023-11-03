package com.facilio.readingkpi.readingslist;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.connected.CommonConnectedUtil;
import com.facilio.connected.ResourceType;
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
import static com.facilio.readingkpi.ReadingKpiAPI.getCountBuilder;
import static com.facilio.readingkpi.ReadingKpiAPI.getDataBuilder;

public class FetchDynamicKpiReadingsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        String groupBy = (String) context.get(FacilioConstants.ContextNames.REPORT_GROUP_BY);
        String searchModuleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        List<String> resourceTypes = new ArrayList<>();
        resourceTypes.add(FacilioConstants.ContextNames.ASSET);
        resourceTypes.add(FacilioConstants.Meter.METER);
        resourceTypes.add(FacilioConstants.ContextNames.SITE);

        List<Map<String, Object>> props;
        List<Map<String, Object>> records = new ArrayList<>();

        Map<String, Object> countProps;
        if (resourceTypes.contains(groupBy)) {
            props = fetchBuilderForResourceSelected(context, recordId, false).get();
            countProps = fetchBuilderForResourceSelected(context, recordId, true).fetchFirst();

        } else {
            NameSpaceContext ns = getNameSpaceByRuleId(recordId, NSType.KPI_RULE);
            List<Long> resourceIds = NamespaceAPI.getMatchedResources(ns);
            props = fetchBuilderForKpiSelected(context, resourceIds, searchModuleName, false).get();
            countProps = fetchBuilderForKpiSelected(context, resourceIds, searchModuleName, true).fetchFirst();
        }

        for (Map<String, Object> prop : props) {
            Map<String, Object> row = new HashMap<>();
            row.put("name", prop.get("name"));
            row.put("id", prop.get("id"));
            records.add(row);
        }

        long count = 0;
        if (MapUtils.isNotEmpty(countProps)) {
            count = (long) countProps.get("id");
        }
        context.put(FacilioConstants.ContextNames.COUNT, count);
        context.put(FacilioConstants.ContextNames.DATA, records);


        return false;
    }

    private static GenericSelectRecordBuilder fetchBuilderForKpiSelected(Context context, List<Long> resourceIds, String searchModuleName, boolean fetchCount) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(searchModuleName);
        FacilioModule resourcesModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .innerJoin(resourcesModule.getTableName())
                .on(module.getTableName() + ".ID=" + resourcesModule.getTableName() + ".ID")
                .andCondition(CriteriaAPI.getIdCondition(resourceIds, module));


        ReadingKpiAPI.addFilterToBuilder(context, fieldsMap, builder);
        return fetchCount
                ? getCountBuilder(builder, module)
                : getDataBuilder(context, module, fieldsMap, builder);
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
     */
    private static GenericSelectRecordBuilder fetchBuilderForResourceSelected(Context context, Long recordId, boolean fetchCount) throws Exception {

        ModuleBean modBean = Constants.getModBean();
        FacilioModule kpiModule = modBean.getModule(FacilioConstants.ReadingKpi.READING_KPI);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ReadingKpi.READING_KPI);
        Map<String, FacilioField> kpiFieldsMap = FieldFactory.getAsMap(fields);

        FacilioModule nsModule = NamespaceModuleAndFieldFactory.getNamespaceModule();
        FacilioModule nsInclModule = NamespaceModuleAndFieldFactory.getNamespaceInclusionModule();
        Map<String, FacilioField> nsInclFieldsMap = FieldFactory.getAsMap(NamespaceModuleAndFieldFactory.getNamespaceInclusionFields());
        Map<String, FacilioField> nsFieldsMap = FieldFactory.getAsMap(NamespaceModuleAndFieldFactory.getNamespaceFields());


        Criteria resIdCriteria = new Criteria();
        resIdCriteria.addAndCondition(CriteriaAPI.getCondition(nsInclFieldsMap.get("resourceId"), Collections.singleton(recordId), NumberOperators.EQUALS));
        resIdCriteria.addOrCondition(CriteriaAPI.getCondition(nsInclFieldsMap.get("resourceId"), CommonOperators.IS_EMPTY));

        String groupBy = (String) context.get(FacilioConstants.ContextNames.REPORT_GROUP_BY);
        ResourceType resourceType = ResourceType.getResourceTypeFromModuleName(groupBy);
        Long categoryId = (Long) context.get(FacilioConstants.ReadingKpi.RESOURCE_CATEGORY_ID);
        if (categoryId == null) {
            categoryId = CommonConnectedUtil.getCategoryForResource(recordId, resourceType);
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(kpiModule.getTableName())
                .innerJoin(nsModule.getTableName())
                .on(kpiModule.getTableName() + ".ID=" + nsModule.getTableName() + ".PARENT_RULE_ID")
                .leftJoin(nsInclModule.getTableName())
                .on(nsModule.getTableName() + ".ID=" + nsInclModule.getTableName() + ".NAMESPACE_ID")
                .andCriteria(resIdCriteria)
                .andCondition(CriteriaAPI.getCondition(kpiFieldsMap.get("kpiType"), String.valueOf(KPIType.DYNAMIC.getIndex()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(nsFieldsMap.get("type"), String.valueOf(NSType.KPI_RULE.getIndex()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(kpiModule.getTableName() + ".SYS_DELETED", "sysDeleted", String.valueOf(Boolean.FALSE), BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition(kpiFieldsMap.get("status"), String.valueOf(Boolean.TRUE), BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition(kpiFieldsMap.get("resourceType"), String.valueOf(resourceType.getIndex()), NumberOperators.EQUALS));
        if (categoryId > 0) {
            builder.andCondition(CriteriaAPI.getCondition(kpiFieldsMap.get("categoryId"), String.valueOf(categoryId), NumberOperators.EQUALS));
        }

        ReadingKpiAPI.addFilterToBuilder(context, kpiFieldsMap, builder);
        return fetchCount
                ? getCountBuilder(builder, kpiModule)
                : getDataBuilder(context, kpiModule, kpiFieldsMap, builder);
    }
}

package com.facilio.readingkpi.commands.list;


import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.factory.NamespaceModuleAndFieldFactory;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.facilio.ns.factory.NamespaceModuleAndFieldFactory.getNamespaceInclusionModule;

public class AddNamespaceInKpiListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ReadingKPIContext> list = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(list)) {
            for (ReadingKPIContext kpi : list) {
                NameSpaceContext namespaceContext = NamespaceAPI.getNameSpaceByRuleId(kpi.getId(), NSType.KPI_RULE);
                List<Long> resourceIds = fetchResourceIdsFromNamespaceInclusions(namespaceContext.getId());
                if(CollectionUtils.isNotEmpty(resourceIds)) {
                    kpi.setAssets(resourceIds);
                    String firstAssetName = getResourceName(resourceIds.get(0));
                    kpi.setFirstAssetName(firstAssetName);
                }
                namespaceContext.setIncludedAssetIds(resourceIds);
                kpi.setNs(namespaceContext);
            }
        }
        return false;
    }

    private List<Long> fetchResourceIdsFromNamespaceInclusions(Long nameSpaceId) throws Exception {
        List<FacilioField> fields = NamespaceModuleAndFieldFactory.getNamespaceInclusionFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
        selectBuilder.select(fields)
                .table(getNamespaceInclusionModule().getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("namespaceId"), nameSpaceId.toString(), NumberOperators.EQUALS));

        List<Map<String, Object>> maps = selectBuilder.get();
        List<Long> resourceIds = new ArrayList<>();
        for (Map<String, Object> m : maps) {
            resourceIds.add((Long) m.get("resourceId"));
        }

        return resourceIds;
    }

    private String getResourceName(Long id) throws Exception {
        AssetContext asset = AssetsAPI.getAssetInfo(id);
        return asset.getName();
    }

}

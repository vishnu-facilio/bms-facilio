package com.facilio.readingkpi.commands.list;


import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AddNamespaceInKpiListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ReadingKPIContext> list = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(list)) {
            for (ReadingKPIContext kpi : list) {
                NameSpaceContext namespaceContext = NamespaceAPI.getNameSpaceByRuleId(kpi.getId(), NSType.KPI_RULE);
                List<Long> resourceIds = namespaceContext.getIncludedAssetIds();
                if(CollectionUtils.isNotEmpty(resourceIds)) {
                    kpi.setAssets(resourceIds);
                    String firstAssetName = getResourceName(resourceIds.get(0));
                    kpi.setFirstAssetName(firstAssetName);
                }
                kpi.setNs(namespaceContext);
            }
        }
        return false;
    }

    private String getResourceName(Long id) throws Exception {
        AssetContext asset = AssetsAPI.getAssetInfo(id, true);
        return asset!=null ? asset.getName() : "";
    }

}

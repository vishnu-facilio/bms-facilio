package com.facilio.readingkpi.commands.list;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.readingkpi.ReadingKpiLoggerAPI;
import com.facilio.readingkpi.context.KpiResourceLoggerContext;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchResourceNamesForKpiLogger extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long parentLoggerId = (Long) context.get("parentLoggerId");
        List<KpiResourceLoggerContext> resourceLoggers = ReadingKpiLoggerAPI.getResourceIdsForLogger(parentLoggerId);
        List<Map<String, Object>> resourceList = new ArrayList<>();
        for (KpiResourceLoggerContext resourceLogger : resourceLoggers) {
            Map<String, Object> resourcesVsStatusMap = new HashMap<>();
            AssetContext asset = AssetsAPI.getAssetInfo(resourceLogger.getResourceId());
            if(asset!=null) {
                resourcesVsStatusMap.put("name", asset.getName());
                resourcesVsStatusMap.put("status", resourceLogger.getStatus());
                if (StringUtils.isNotEmpty(resourceLogger.getMessage())) {
                    resourcesVsStatusMap.put("errMsg", resourceLogger.getMessage());
                }
                resourceList.add(resourcesVsStatusMap);
            }
        }
        context.put("assetList", resourceList);
        return false;
    }
}

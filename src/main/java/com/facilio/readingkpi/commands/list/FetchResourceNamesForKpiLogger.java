package com.facilio.readingkpi.commands.list;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingkpi.ReadingKpiLoggerAPI;
import com.facilio.readingkpi.context.KpiLoggerContext;
import com.facilio.readingkpi.context.KpiResourceLoggerContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchResourceNamesForKpiLogger extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<KpiLoggerContext> kpiLoggerContexts = (List<KpiLoggerContext>) recordMap.get(FacilioConstants.ReadingKpi.KPI_LOGGER_MODULE);
        for (KpiLoggerContext logger : kpiLoggerContexts) {
            List<KpiResourceLoggerContext> resourceLoggers = ReadingKpiLoggerAPI.getResourceIdsForLogger(logger.getId());
            List<Map<String, String>> resourceList = new ArrayList<>();
            for (KpiResourceLoggerContext resourceLogger : resourceLoggers) {
                Map<String, String> resourcesVsStatusMap = new HashMap<>();
                AssetContext asset = AssetsAPI.getAssetInfo(resourceLogger.getResourceId());
                if(asset!=null) {
                    resourcesVsStatusMap.put("name", asset.getName());
                    resourcesVsStatusMap.put("status", getStatus(resourceLogger));
                    resourceList.add(resourcesVsStatusMap);
                }
            }
            logger.setResourceList(resourceList);
        }
        return false;
    }

    private String getStatus(KpiResourceLoggerContext resourceLogger) {
        KpiResourceLoggerContext.KpiLoggerStatus status = KpiResourceLoggerContext.KpiLoggerStatus.valueOf(resourceLogger.getStatus());
        return status != null ? status.getName() : " ";
    }
}

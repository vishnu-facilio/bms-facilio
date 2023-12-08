package com.facilio.readingkpi.commands.list;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.MetersAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.command.FacilioCommand;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.readingkpi.ReadingKpiLoggerAPI;
import com.facilio.readingkpi.context.KpiResourceLoggerContext;
import com.facilio.readingkpi.context.ReadingKPIContext;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class FetchResourceNamesForKpiLogger extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long parentLoggerId = (Long) context.get("parentLoggerId");
        List<KpiResourceLoggerContext> resourceLoggers = ReadingKpiLoggerAPI.getResourceIdsForLogger(parentLoggerId);
        List<Map<String, Object>> resourceList = new ArrayList<>();
        for (KpiResourceLoggerContext resourceLogger : resourceLoggers) {
            Map<String, Object> resourcesVsStatusMap = new HashMap<>();

            String asset = getKpiLoggerResourceName(resourceLogger);
            if(asset!=null) {
                resourcesVsStatusMap.put("name", asset);
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

    private static String getKpiLoggerResourceName(KpiResourceLoggerContext resourceLogger) throws Exception {
        ReadingKPIContext kpi = ReadingKpiAPI.getReadingKpi(Collections.singletonList(resourceLogger.getKpiId())).get(0);
        switch (kpi.getResourceTypeEnum()) {
            case METER_CATEGORY:
                V3MeterContext meterContext = MetersAPI.getMeters(Collections.singletonList(resourceLogger.getResourceId())).get(0);
                return meterContext.getName();
            default:
                ResourceContext resourceContext = ResourceAPI.getResource(resourceLogger.getResourceId());
                return resourceContext != null ? resourceContext.getName() : null;
        }
    }
}

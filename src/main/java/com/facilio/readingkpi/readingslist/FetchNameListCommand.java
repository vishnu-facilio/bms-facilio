package com.facilio.readingkpi.readingslist;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingkpi.ReadingKpiAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchNameListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        KpiAnalyticsDataFetcher fetcher = ReadingKpiAPI.getKpiAnalyticsDataFetcher(moduleName, context);
        List<Map<String, Object>> kpis = new ArrayList<>();

        List<Map<String, Object>> props = fetcher.fetchBuilder(context, false).get();
        for (Map<String, Object> prop : props) {
            Map<String, Object> row = new HashMap<>();
            row.put("name", prop.get("name"));
            row.put("id", prop.get("id"));

            // will not be null for kpis
            row.put("kpiType", prop.get("kpiType"));

            kpis.add(row);
        }

        long count = 0;
        Map<String, Object> countProps = fetcher.fetchBuilder(context, true).fetchFirst();
        if (MapUtils.isNotEmpty(countProps)) {
            count = (long) countProps.get("id");
        }
        context.put(FacilioConstants.ContextNames.COUNT, count);
        context.put(FacilioConstants.ContextNames.DATA, kpis);
        return false;
    }
}

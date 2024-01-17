package com.facilio.analytics.v2.command;

import com.facilio.command.FacilioCommand;
import com.facilio.connected.ResourceType;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.readingkpi.context.KpiContextWrapper;
import com.facilio.readingkpi.context.ReadingKPIContext;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class V2AnalyticsGetKPIListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        Long categoryId = (Long) context.get("categoryId");
        String type = (String) context.get("type");
        String searchText = (String) context.get("searchText");
        if(type.equalsIgnoreCase(ResourceType.METER_CATEGORY.getName()))
        {
            context.put("kpis",this.getKpisList(categoryId, ResourceType.METER_CATEGORY, searchText));
        }
        else if(type.equalsIgnoreCase(ResourceType.ASSET_CATEGORY.getName()))
        {
            context.put("kpis",this.getKpisList(categoryId, ResourceType.ASSET_CATEGORY, searchText));
        }
        return false;
    }


    private Map<Long, KpiContextWrapper> getKpisList(Long categoryId, ResourceType type, String searchText)throws Exception
    {
        Map<Long, KpiContextWrapper> kpiIdVsKpiContext = new HashMap<>();
        List<ReadingKPIContext> kpis_list =  ReadingKpiAPI.getKpisOfCategory(categoryId, type);
        if(kpis_list != null)
        {
            for (ReadingKPIContext kpiContext : kpis_list)
            {
                if(searchText != null && !"".equals(searchText) && kpiContext.getName() != null && !kpiContext.getName().toUpperCase().contains(searchText.toUpperCase())){
                    continue;
                }
                kpiIdVsKpiContext.put(kpiContext.getId(), new KpiContextWrapper(kpiContext));
            }
        }
        return kpiIdVsKpiContext;
    }

}

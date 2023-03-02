package com.facilio.bmsconsoleV3.commands.plannedmaintenance;

import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class RemoveDuplicateSites extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null || recordMap.isEmpty()){
            return false;
        }
        List<ModuleBaseWithCustomFields> plannedmaintenanceList = (List<ModuleBaseWithCustomFields>) recordMap.get(FacilioConstants.ContextNames.PLANNEDMAINTENANCE);
        for(ModuleBaseWithCustomFields record : plannedmaintenanceList){
            PlannedMaintenance plannedMaintenance = (PlannedMaintenance) record;
            if(plannedMaintenance.getSites()!= null){
                ArrayList<V3SiteContext> siteList = (ArrayList<V3SiteContext>) plannedMaintenance.getSites();
                LinkedHashSet<V3SiteContext> siteSet = new LinkedHashSet<>(siteList);
                plannedMaintenance.setSites(new ArrayList<>(siteSet));
            }
        }
        recordMap.put(FacilioConstants.ContextNames.PLANNEDMAINTENANCE,plannedmaintenanceList);
        context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
        return false;
    }
}

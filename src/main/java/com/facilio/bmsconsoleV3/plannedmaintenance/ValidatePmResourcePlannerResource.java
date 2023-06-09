package com.facilio.bmsconsoleV3.plannedmaintenance;

import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3SpaceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;

public class ValidatePmResourcePlannerResource extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null || recordMap.size() < 1){
            return false;
        }
        if(!recordMap.containsKey(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER)){
            return false;
        }
        List<PMResourcePlanner> pmResourcePlannerList = (List<PMResourcePlanner>) recordMap.get(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER);
         if(CollectionUtils.isEmpty(pmResourcePlannerList)){
             return false;
         }
         for(PMResourcePlanner resourcePlanner : pmResourcePlannerList){
             Long pmId = resourcePlanner.getPmId();
             PlannedMaintenance plannedMaintenance = PlannedMaintenanceAPI.getPmV2fromId(pmId);
             List<V3SiteContext> pmSiteList = plannedMaintenance.getSites();
             boolean check = checkForSiteScope(plannedMaintenance,resourcePlanner,pmSiteList);
             if(!check){
                 throw new IllegalArgumentException(resourcePlanner.getResource().getName()+" is not inside the scope of site associated with ppm - # "+resourcePlanner.getPmId());
             }
         }
        return false;
    }
    public boolean checkForSiteScope(PlannedMaintenance plannedMaintenance, PMResourcePlanner resourcePlanner, List<V3SiteContext> pmSiteList){
        boolean check = false;
        if(resourcePlanner == null){
            throw new IllegalArgumentException("No resource in Resource Planner");
        }
        if(CollectionUtils.isEmpty(pmSiteList)){
            throw new IllegalArgumentException("No site Associated with pm -# "+plannedMaintenance.getId());
        }
        V3ResourceContext resource = resourcePlanner.getResource();
        for(V3SiteContext siteContext : pmSiteList){
            if(check){
                break;
            }
            if(resource.getSiteId() == siteContext.getSiteId()){
                check = true;
            }
            else if(resource.getId() == siteContext.getSiteId()){
                check = true;
            }
        }
        return check;
    }
}

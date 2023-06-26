package com.facilio.bmsconsoleV3.commands.plannedmaintenance;

import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ValidateDuplicateResourcesInsidePlannerCommand extends FacilioCommand {
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
        for(PMResourcePlanner pmResourcePlanner : pmResourcePlannerList){
            boolean check = checkForDuplicates(pmResourcePlanner.getPlanner().getId(),pmResourcePlanner.getResourceId());
            if(!check){
                throw new IllegalArgumentException("Duplicates Resource Insertion Failed for Planner - #"+pmResourcePlanner.getPlanner().getId());
            }
        }
        return false;
    }
    public boolean checkForDuplicates(Long plannerId, Long resourceId) throws Exception{
        if(plannerId == null){
            throw new IllegalArgumentException("Planner Is Null");
        }
        List<PMResourcePlanner> resourcePlannerList = PlannedMaintenanceAPI.getResourcePlanners(plannerId);
        if(CollectionUtils.isEmpty(resourcePlannerList)){
            return true;
        }
        List<Long> idList = resourcePlannerList.stream().map(PMResourcePlanner::getResourceId).collect(Collectors.toList());
        if(!idList.contains(resourceId)){
            return true;
        }
        return false;
    }
}

package com.facilio.bmsconsoleV3.commands.plannedmaintenance;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class DeletePMPlannerPreOpenWorkOrders extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if (recordMap == null ) {
            return false;
        }
        List<Long> pmPlannerIdList = new ArrayList<>();
        if(recordMap.containsKey(FacilioConstants.PM_V2.PM_V2_PLANNER)){
            List<PMPlanner> pmPlannerList = (List<PMPlanner>) recordMap.get(FacilioConstants.PM_V2.PM_V2_PLANNER);
            if(pmPlannerList == null){
                return false;
            }
            for(PMPlanner planner : pmPlannerList){
                pmPlannerIdList.add((Long)planner.getId());
            }
            if(pmPlannerIdList == null || pmPlannerIdList.isEmpty()){
                return false;
            }
            PlannedMaintenanceAPI.deleteWorkOrdersFromPlannerId(pmPlannerIdList);
        }
        return false;
    }
}
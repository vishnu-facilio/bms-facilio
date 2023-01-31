package com.facilio.bmsconsoleV3.commands.plannedmaintenance;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class DeletePPMPreOpenWorkorders extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> pmIdList = new ArrayList<>();
        if(context.containsKey("pmId")){
            pmIdList.add((Long)context.get("pmId"));
        }
        else if(context.containsKey("pmIds")){
            pmIdList = (List<Long>) context.get("pmIds");
        }
        Map<String, List> recordMap = (Map<String, List>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap != null && recordMap.containsKey(FacilioConstants.ContextNames.PLANNEDMAINTENANCE)){
            List<PlannedMaintenance> plannedMaintenanceList = (List<PlannedMaintenance>) recordMap.get(FacilioConstants.ContextNames.PLANNEDMAINTENANCE);
            if(plannedMaintenanceList == null){
                return false;
            }
            for(PlannedMaintenance plannedMaintenance : plannedMaintenanceList){
               pmIdList.add(plannedMaintenance.getId());
            }
        }
        if(pmIdList == null || pmIdList.isEmpty()){
            return false;
        }
        PlannedMaintenanceAPI.deleteWorkOrderFromPmId(pmIdList);
        return false;
    }
}


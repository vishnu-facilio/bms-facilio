package com.facilio.bmsconsoleV3.commands.plannedmaintenance;

import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FetchPPMDetailsFromPlannerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        List<PMPlanner> pmPlannerList = (List<PMPlanner>) recordMap.get(FacilioConstants.PM_V2.PM_V2_PLANNER);
        if(CollectionUtils.isEmpty(pmPlannerList)){
            return false;
        }
        Set<Long> pmIds = new HashSet<>();
        for(PMPlanner planner : pmPlannerList){
            pmIds.add(planner.getPmId());
        }
        context.put("pmIds",pmIds.stream().collect(Collectors.toList()));
        return false;
    }
}

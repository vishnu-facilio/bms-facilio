package com.facilio.bmsconsoleV3.commands.plannedmaintenance;

import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FetchPMDetailsFromPmResourcePlanner extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        List<PMResourcePlanner> pmResourcePlannerList = (List<PMResourcePlanner>) recordMap.get(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER);
        if(CollectionUtils.isEmpty(pmResourcePlannerList)){
            return false;
        }
        Set<Long> pmIds = new HashSet<>();
        for(PMResourcePlanner record : pmResourcePlannerList){
            pmIds.add(record.getPmId());
        }
        context.put("pmIds",pmIds.stream().collect(Collectors.toList()));
        return false;
    }
}

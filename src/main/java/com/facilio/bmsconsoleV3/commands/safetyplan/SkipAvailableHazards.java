package com.facilio.bmsconsoleV3.commands.safetyplan;

import com.facilio.bmsconsole.util.HazardsAPI;
import com.facilio.bmsconsoleV3.context.safetyplans.V3HazardContext;
import com.facilio.bmsconsoleV3.context.safetyplans.V3SafetyPlanHazardContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkipAvailableHazards extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get("recordMap");
        List<V3SafetyPlanHazardContext> safetyPlanHazardList = recordMap.get("safetyPlanHazard");
        Map<String, Object> paramMap = (Map<String, Object>) context.get("bodyParams");
        Long safetyPlanId = (Long) paramMap.get("safetyPlan");
        List<V3SafetyPlanHazardContext> filteredSafetyPlanList = new ArrayList<>();
        if(safetyPlanId != null) {
            List<Long> HazardIds = HazardsAPI.fetchAssociatedSafetyPlanHazardIds(String.valueOf(safetyPlanId));

            if (CollectionUtils.isNotEmpty(safetyPlanHazardList)) {
                for (V3SafetyPlanHazardContext safetyPlanHazard : safetyPlanHazardList) {
                    V3HazardContext hazard = safetyPlanHazard.getHazard();
                    if (hazard != null) {
                        Long hazardId = hazard.getId();
                        if(!HazardIds.contains(hazardId)){
                            filteredSafetyPlanList.add(safetyPlanHazard);
                        }
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(filteredSafetyPlanList)){
                recordMap.put("safetyPlanHazard",filteredSafetyPlanList);
                context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
            }else{
                return true;
            }
        }
        return false;
    }
}

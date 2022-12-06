package com.facilio.bmsconsoleV3.commands.safetyplan;

import com.facilio.bmsconsole.util.HazardsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcludeAssociatedHazardPrecautions extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> queryMap = (Map<String, List>) context.get("queryParams");
        List excludeAvailableHazards = queryMap.get("excludeAvailableHazards");
        List parentModuleNameList = queryMap.get("parentModuleName");
        List excludeAvailablePrecautions = queryMap.get("excludeAvailablePrecautions");
        List<Long> results = new ArrayList<>();
        if(excludeAvailableHazards != null && parentModuleNameList != null && excludeAvailableHazards.size() > 0 && parentModuleNameList.size() > 0) {
            String moduleId = (String) excludeAvailableHazards.get(0);
            String parentModuleName = (String) parentModuleNameList.get(0);
            if(parentModuleName.equals("safetyPlan")) {
                results = HazardsAPI.fetchAssociatedSafetyPlanHazardIds(moduleId);
            }
            else if(parentModuleName.equals("asset")){
                results = HazardsAPI.fetchAssociatedAssetHazardIds(moduleId);
            }else if(parentModuleName.equals("space")){
                results = HazardsAPI.fetchAssociatedSpaceHazardIds(moduleId);
            }else if(parentModuleName.equals("workorder")){
                results = HazardsAPI.fetchAssociatedWorkOrderHazardIds(moduleId);
            }
        }else if(excludeAvailablePrecautions != null && excludeAvailablePrecautions.size() > 0){
            String hazardId = (String) excludeAvailablePrecautions.get(0);
            results = HazardsAPI.fetchAssociatedHazardPrecautions(hazardId);
        }
        if (CollectionUtils.isNotEmpty(results)) {
            Condition hazardCondition = CriteriaAPI.getCondition("ID", "id",
                    StringUtils.join(results, ','), NumberOperators.NOT_EQUALS);
            context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, hazardCondition);
        }
        return false;
    }
}

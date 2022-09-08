package com.facilio.bmsconsoleV3.commands.safetyplan;

import com.facilio.bmsconsoleV3.context.failurecode.V3FailureCodeRemediesContext;
import com.facilio.bmsconsoleV3.context.safetyplans.V3HazardContext;
import com.facilio.bmsconsoleV3.context.safetyplans.V3SafetyPlanHazardContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcludeAvailableHazards extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> queryMap = (Map<String, List>) context.get("queryParams");
        List excludeAvailableHazards = queryMap.get("excludeAvailableHazards");
        List<Long> hazardIds = new ArrayList<>();
        if(excludeAvailableHazards != null && excludeAvailableHazards.size() > 0) {
            String safetyPlanId = (String) excludeAvailableHazards.get(0);
            Criteria criteria = new Criteria();
            Condition condition = CriteriaAPI.getCondition("SAFETY_PLAN_ID", "safetyPlan", safetyPlanId, NumberOperators.EQUALS);
            criteria.addAndCondition(condition);
            Map<Long, V3SafetyPlanHazardContext> props = V3RecordAPI.getRecordsMap(FacilioConstants.ContextNames.SAFETYPLAN_HAZARD, null, V3SafetyPlanHazardContext.class, criteria, null);
            if(props != null){
                for (V3SafetyPlanHazardContext safetyPlanHazard : props.values()){
                    V3HazardContext hazard = safetyPlanHazard.getHazard();
                    hazardIds.add(hazard.getId());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(hazardIds)) {
            Condition hazardCondition = CriteriaAPI.getCondition("ID", "id",
                    StringUtils.join(hazardIds, ','), NumberOperators.NOT_EQUALS);
            context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, hazardCondition);
        }
        return false;
    }
}

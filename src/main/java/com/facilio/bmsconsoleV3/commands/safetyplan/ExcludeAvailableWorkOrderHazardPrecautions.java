package com.facilio.bmsconsoleV3.commands.safetyplan;

import com.facilio.bmsconsole.context.HazardContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderHazardContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderHazardPrecautionContext;
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

public class ExcludeAvailableWorkOrderHazardPrecautions extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> queryMap = (Map<String, List>) context.get("queryParams");
        List excludeAvailableWorkOrderHazardPrecautions = queryMap.get("excludeAvailableWorkOrderHazardPrecautions");
        List<Long> precautionIds = new ArrayList<>();
        if(excludeAvailableWorkOrderHazardPrecautions != null && excludeAvailableWorkOrderHazardPrecautions.size() > 0) {
            String workorderHazardId = (String) excludeAvailableWorkOrderHazardPrecautions.get(0);
            Criteria criteria = new Criteria();
            Condition condition = CriteriaAPI.getCondition("WORKORDER_HAZARD_ID", "workorderHazard", workorderHazardId, NumberOperators.EQUALS);
            criteria.addAndCondition(condition);
            Map<Long, V3WorkorderHazardPrecautionContext> props = V3RecordAPI.getRecordsMap(FacilioConstants.ContextNames.WORKORDER_HAZARD_PRECAUTION, null, V3WorkorderHazardPrecautionContext.class, criteria, null);
            if(props != null){
                for (V3WorkorderHazardPrecautionContext workorderHazardPrecaution : props.values()){
                    precautionIds.add(workorderHazardPrecaution.getPrecaution().getId());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(precautionIds)) {
            Condition precautionCondition = CriteriaAPI.getCondition("ID", "id",
                    StringUtils.join(precautionIds, ','), NumberOperators.NOT_EQUALS);
            context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, precautionCondition);
        }
        return false;
    }
}

package com.facilio.bmsconsoleV3.commands.decommission;

import com.facilio.agentv2.AgentConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class DecommissionPicklistCheckCommand extends FacilioCommand {
    private static  final String falseCheck = "false";

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addOrCondition(CriteriaAPI.getCondition("Resources.IS_DECOMMISSIONED", FacilioConstants.ContextNames.DECOMMISSION, falseCheck, BooleanOperators.IS));
        List<Long> defaultIdList = (List<Long>) context.get(FacilioConstants.PickList.DEFAULT_ID_LIST);
        if(CollectionUtils.isNotEmpty(defaultIdList)) {
            criteria.addOrCondition(CriteriaAPI.getCondition("Resources.ID", AgentConstants.ID, StringUtils.join(defaultIdList, ","), NumberOperators.EQUALS));
        }
        Map<String, List> query = (Map<String, List>) context.get(FacilioConstants.ContextNames.QUERY_PARAMS);
        if(query != null) {
            if (!query.containsKey(FacilioConstants.ContextNames.IS_TO_FETCH_DECOMMISSIONED_RESOURCE)) {
                context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, criteria);
            }
        }
        return false;
    }
}

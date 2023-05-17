package com.facilio.bmsconsoleV3.commands.decommission;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class DecommissionPicklistCheckCommand extends FacilioCommand {
    private static  final String falseCheck = "false";

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Condition condition = CriteriaAPI.getCondition("IS_DECOMMISSIONED", FacilioConstants.ContextNames.DECOMMISSION, falseCheck, BooleanOperators.IS);
        Map<String, List> query = (Map<String, List>) context.get(FacilioConstants.ContextNames.QUERY_PARAMS);
        if(!query.containsKey("isDecommissionGlobalSwitch")){
        context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, condition);
        }
        return false;
    }
}

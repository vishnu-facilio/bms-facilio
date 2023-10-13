package com.facilio.flows.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.flows.context.FlowContext;
import com.facilio.flows.context.FlowType;
import com.facilio.flows.util.FlowUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;

public class GetKPIFlowListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("TYPE","flowType", String.valueOf(FlowType.KPI.getIndex()), NumberOperators.EQUALS));

        List<FlowContext> flowList =  FlowUtil.getFlowList(criteria,pagination,"ID ASC");

        context.put(FacilioConstants.ContextNames.FLOWS,flowList);
        return false;
    }
}

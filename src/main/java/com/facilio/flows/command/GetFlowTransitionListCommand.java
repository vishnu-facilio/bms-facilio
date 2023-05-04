package com.facilio.flows.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.flows.context.FlowTransitionContext;
import com.facilio.flows.util.FlowUtil;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetFlowTransitionListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
       long flowId = (long) context.get(FacilioConstants.ContextNames.FLOW_ID);

       if (flowId <= 0){
           throw new IllegalArgumentException("Id cannot be null");
       }

        List<FlowTransitionContext> flowTransitions = FlowUtil.getFlowTransitionList(flowId);

       context.put(FacilioConstants.ContextNames.FLOW_TRANSITIONS,flowTransitions);

        return false;
    }
}

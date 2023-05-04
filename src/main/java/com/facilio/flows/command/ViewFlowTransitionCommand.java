package com.facilio.flows.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.flows.context.FlowTransitionContext;
import com.facilio.flows.util.FlowUtil;
import org.apache.commons.chain.Context;

public class ViewFlowTransitionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
       long id = (long) context.get(FacilioConstants.ContextNames.ID);

       if (id <= 0){
           throw new IllegalArgumentException("Invalid id");
       }

        FlowTransitionContext flowTransition = FlowUtil.getFlowTransition(id);

       context.put(FacilioConstants.ContextNames.FLOW_TRANSITION,flowTransition);

        return false;
    }
}

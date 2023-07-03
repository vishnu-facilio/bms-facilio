package com.facilio.flows.command;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.flows.context.FlowTransitionContext;
import com.facilio.flows.util.FlowUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

public class ViewFlowTransitionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
       long id = (long) context.get(FacilioConstants.ContextNames.ID);

       if (id <= 0){
           throw new IllegalArgumentException("Invalid id");
       }

       FacilioContext flowContext = FlowUtil.getFlowTransitionWithConfig(id);
       FlowTransitionContext flowTransitionContext = (FlowTransitionContext) flowContext.get(FacilioConstants.ContextNames.FLOW_TRANSITION);

       FacilioUtil.throwIllegalArgumentException(flowTransitionContext==null,"FlowTransition with id:"+id+" does not exist");

       context.put(FacilioConstants.ContextNames.FLOW_TRANSITION,flowTransitionContext);
       context.put(FacilioConstants.ContextNames.SUPPLEMENTS,flowContext.get(FacilioConstants.ContextNames.SUPPLEMENTS));

        return false;
    }
}

package com.facilio.flows.command;

import com.facilio.blockfactory.enums.BlockType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.flowengine.context.Constants;
import com.facilio.flows.context.FlowTransitionContext;
import com.facilio.flows.util.FlowChainUtil;
import com.facilio.flows.util.FlowUtil;
import com.facilio.modules.FieldUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.Map;

public class ViewFlowTransitionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
       long id = (long) context.get(FacilioConstants.ContextNames.ID);

       if (id <= 0){
           throw new IllegalArgumentException("Invalid id");
       }

       FlowTransitionContext flowTransition = FlowUtil.getFlowTransitionWithConfig(id);

       FacilioUtil.throwIllegalArgumentException(flowTransition==null,"FlowTransition with id:"+id+" does not exist");

       context.put(FacilioConstants.ContextNames.FLOW_TRANSITION,flowTransition);

        return false;
    }
}

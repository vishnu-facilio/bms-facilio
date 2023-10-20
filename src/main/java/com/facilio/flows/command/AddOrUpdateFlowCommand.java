package com.facilio.flows.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.flows.context.FlowContext;
import com.facilio.flows.util.FlowUtil;
import org.apache.commons.chain.Context;

public class AddOrUpdateFlowCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        FlowContext flowContext = (FlowContext) context.get(FacilioConstants.ContextNames.FLOW);

        FlowUtil.addOrUpdateFlow(flowContext);

        context.put(FacilioConstants.ContextNames.FLOW,flowContext);

        return false;
    }
}

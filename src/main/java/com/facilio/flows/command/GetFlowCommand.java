package com.facilio.flows.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.flows.context.FlowContext;
import com.facilio.flows.util.FlowUtil;
import org.apache.commons.chain.Context;

public class GetFlowCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.get(FacilioConstants.ContextNames.ID);

        if (id <= 0){
            throw new IllegalArgumentException("Id cannot be null");
        }

        FlowContext flow = FlowUtil.getFlow(id);

        context.put(FacilioConstants.ContextNames.FLOW,flow);
        return false;
    }
}

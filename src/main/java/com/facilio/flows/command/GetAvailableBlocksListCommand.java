package com.facilio.flows.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.flows.context.FlowTransitionContext;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetAvailableBlocksListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {


        int type = (int) context.get(FacilioConstants.ContextNames.TYPE);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

            List<Map<String, Object>> availableBlocks = new ArrayList<>();


            if (type == 2) {
                Map<String, Object> groupsVsBlockMap = FlowTransitionContext.Blocks.getGroupBlockMap();
                availableBlocks.add(groupsVsBlockMap);
            }

            context.put(FacilioConstants.ContextNames.AVAILABLE_BLOCKS,availableBlocks);


        return false;
    }
}

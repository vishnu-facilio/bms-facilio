package com.facilio.flows.blockconfigcommand;

import com.facilio.blockfactory.BlockFactory;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.flowengine.context.Constants;
import com.facilio.flows.context.ScriptFlowTransitionContext;
import org.apache.commons.chain.Context;

import java.util.Map;

public class LoadScriptBlockCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ScriptFlowTransitionContext scriptFlowTransitionContext = (ScriptFlowTransitionContext) context.get(FacilioConstants.ContextNames.FLOW_TRANSITION);
        Map<String,Object> configMap = BlockFactory.getAsMapFromJsonString(scriptFlowTransitionContext.getConfigData());
        long functionId = (long) configMap.getOrDefault(Constants.FUNCTION_ID,-1l);

        scriptFlowTransitionContext.setFunctionId(functionId);
        return false;
    }
}

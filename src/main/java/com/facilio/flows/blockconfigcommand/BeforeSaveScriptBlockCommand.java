package com.facilio.flows.blockconfigcommand;

import com.facilio.blockfactory.BlockFactory;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.flowengine.context.Constants;
import com.facilio.flows.context.ScriptFlowTransitionContext;
import com.facilio.modules.FieldUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import org.apache.commons.chain.Context;

import java.util.Map;

public class BeforeSaveScriptBlockCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ScriptFlowTransitionContext scriptFlowTransitionContext = (ScriptFlowTransitionContext) context.get(FacilioConstants.ContextNames.FLOW_TRANSITION);
        long functionId = scriptFlowTransitionContext.getFunctionId();
        FacilioUtil.throwIllegalArgumentException(functionId==-1l,"functionId is can not be null");

        WorkflowUserFunctionContext userFunction = UserFunctionAPI.getUserFunction(functionId);
        FacilioUtil.throwIllegalArgumentException(userFunction==null,"functionId:"+functionId+ " does not exist");

        Map<String,Object> configMap = BlockFactory.getAsMapFromJsonString(scriptFlowTransitionContext.getConfigData());
        configMap.put(Constants.FUNCTION_ID,functionId);
        scriptFlowTransitionContext.setConfigData(FieldUtil.getAsJSON(configMap).toJSONString());
        return false;
    }
}

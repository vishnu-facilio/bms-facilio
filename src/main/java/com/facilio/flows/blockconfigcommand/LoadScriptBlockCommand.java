package com.facilio.flows.blockconfigcommand;

import com.facilio.blockfactory.BlockFactory;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.flowengine.context.Constants;
import com.facilio.flows.context.NotificationFlowTransitionContext;
import com.facilio.flows.context.ScriptFlowTransitionContext;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadScriptBlockCommand extends FacilioCommand {
    boolean listMode;
    public LoadScriptBlockCommand(){
    }
    public LoadScriptBlockCommand(boolean listMode){
        this.listMode = listMode;
    }
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ScriptFlowTransitionContext> scriptFlowTransitiontList = getScriptFlowTransitionContextFromContext(context);

        for(ScriptFlowTransitionContext scriptFlowTransitionContext : scriptFlowTransitiontList){
            Map<String,Object> configMap = BlockFactory.getAsMapFromJsonString(scriptFlowTransitionContext.getConfigData());
            long functionId = (long) configMap.getOrDefault(Constants.FUNCTION_ID,-1l);
            scriptFlowTransitionContext.setFunctionId(functionId);
        }

        return false;
    }
    private List<ScriptFlowTransitionContext> getScriptFlowTransitionContextFromContext(Context context){
        List<ScriptFlowTransitionContext> list = new ArrayList<>();
        if(listMode){
            list = (List<ScriptFlowTransitionContext>) context.get(FacilioConstants.ContextNames.FLOW_TRANSITIONS);
        }else {
            ScriptFlowTransitionContext scriptFlowTransitionContext = (ScriptFlowTransitionContext)
                    context.get(FacilioConstants.ContextNames.FLOW_TRANSITION);
            if(scriptFlowTransitionContext!=null){
                list.add(scriptFlowTransitionContext);
            }
        }
        return list;
    }
}

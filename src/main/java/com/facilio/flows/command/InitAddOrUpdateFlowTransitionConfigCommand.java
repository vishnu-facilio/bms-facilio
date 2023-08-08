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
import org.json.simple.JSONObject;

import java.util.Map;

public class InitAddOrUpdateFlowTransitionConfigCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject flowTransition = (JSONObject) context.get(FacilioConstants.ContextNames.FLOW_TRANSITION);
        FacilioUtil.throwIllegalArgumentException(flowTransition == null, "flowTransition can not be null");

        Long id = (Long) flowTransition.get("id");
        BlockType blockType = null;


        FlowTransitionContext oldFlowTransition = null;
        if (id != null && id != -1l) {
            oldFlowTransition = FlowUtil.getFlowTransitionWithConfig(id);
            FacilioUtil.throwIllegalArgumentException(oldFlowTransition == null, "FlowTransition with id:" + id + " doesn't exist");
            blockType = oldFlowTransition.getBlockType();
        } else {
            String blockTypeStr = (String) flowTransition.get(Constants.BLOCK_TYPE);
            FacilioUtil.throwIllegalArgumentException(blockTypeStr == null, "blockType can not be null");
            blockType = BlockType.valueOf(blockTypeStr);
        }

        Class<? extends FlowTransitionContext> beanClass = FlowChainUtil.getBeanClassByBlockType(blockType);
        FlowTransitionContext flowTransitionContext = FieldUtil.getAsBeanFromJson(flowTransition, beanClass);


        if (flowTransitionContext.getId() > 0) { //UPDATE
            Map<String, Object> clientProp = FieldUtil.getAsProperties(flowTransition);
            Map<String, Object> oldProp = FieldUtil.getAsProperties(oldFlowTransition);
            for (String patchKey : clientProp.keySet()) {
                oldProp.put(patchKey, clientProp.get(patchKey));
            }
            flowTransitionContext = FieldUtil.getAsBeanFromMap(oldProp,beanClass);

            FacilioChain flowConfigChain = FlowChainUtil.getFlowTransitionUpdateChain(blockType);
            FacilioContext flowConfigChainContext = flowConfigChain.getContext();
            flowConfigChainContext.put(FacilioConstants.ContextNames.FLOW_TRANSITION, flowTransitionContext);

            flowConfigChain.execute();
            context.put(FacilioConstants.ContextNames.FLOW_TRANSITION, flowConfigChainContext.get(FacilioConstants.ContextNames.FLOW_TRANSITION));

        }else{ //CREATE
            FacilioChain flowConfigChain = FlowChainUtil.getFlowTransitionCreateChain(blockType);
            FacilioContext flowConfigChainContext = flowConfigChain.getContext();
            flowConfigChainContext.put(FacilioConstants.ContextNames.FLOW_TRANSITION, flowTransitionContext);

            flowConfigChain.execute();
            context.put(FacilioConstants.ContextNames.FLOW_TRANSITION, flowConfigChainContext.get(FacilioConstants.ContextNames.FLOW_TRANSITION));

        }

        return false;
    }

}

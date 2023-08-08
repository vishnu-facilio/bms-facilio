package com.facilio.flows.command;

import com.facilio.blockfactory.BlockFactory;
import com.facilio.blockfactory.blocks.BaseBlock;
import com.facilio.command.FacilioCommand;
import com.facilio.flowengine.context.Constants;
import com.facilio.flows.context.FlowTransitionContext;
import com.facilio.flows.util.FlowUtil;
import com.facilio.util.FacilioUtil;

import java.util.Map;

public abstract class BaseFlowTransitionCommand extends FacilioCommand {
    protected static void validateFlowTransition(FlowTransitionContext flowTransition) throws Exception {
        long flowId = flowTransition.getFlowId();

        FacilioUtil.throwIllegalArgumentException(flowId==-1l,"Flow Id can not be empty");
        FacilioUtil.throwIllegalArgumentException(flowTransition.getIsStartBlock()==null ,"isStartBlock can not be empty");
        long connectedFrom = flowTransition.getConnectedFrom();

        if (connectedFrom !=-1l && connectedFrom!=-99l && !(FlowUtil.validateConnectedFrom(connectedFrom,flowId))){
            throw new IllegalArgumentException("Invalid connectedFrom");
        }
        FlowTransitionContext childTransition = FlowUtil.getConnectedToBlock(connectedFrom,flowId, flowTransition.getHandlePosition());

        if(childTransition != null && childTransition.getId()!=flowTransition.getId()){
            throw new IllegalArgumentException("Already a child transition block exists for given handle position");
        }

        FlowTransitionContext startBlock = FlowUtil.getStartBlock(flowId);
        if (startBlock!=null&&startBlock.getId()!=flowTransition.getId() && flowTransition.getIsStartBlock()){
            throw new IllegalArgumentException("Start block is already declared");
        }

        FacilioUtil.throwIllegalArgumentException(flowTransition.getBlockType() == null,"blockType can't be empty");
        validateConfigData(flowTransition);

    }
    private static void validateConfigData(FlowTransitionContext flowTransition) throws Exception {
        Map<String, Object> blockConfig = BlockFactory.getAsMapFromJsonString(flowTransition.getConfigData());

        blockConfig.put(Constants.BLOCK_ID,flowTransition.getId());

        BaseBlock baseBlock =BlockFactory.createBlock(blockConfig,flowTransition.getBlockType());

        baseBlock.validateBlockConfiguration();
    }

}

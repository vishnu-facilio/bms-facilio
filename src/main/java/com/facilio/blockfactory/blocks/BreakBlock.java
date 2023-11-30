package com.facilio.blockfactory.blocks;

import com.facilio.flowLog.FlowLogLevel;
import com.facilio.flowengine.exception.FlowException;

import java.util.Map;

public class BreakBlock extends BaseBlock{
    public BreakBlock(Map<String, Object> config) {
        super(config);
    }

    @Override
    public void execute(Map<String, Object> memory) throws FlowException {
        try{
            BaseLoopBlock rootParentBlock = flowEngineInterFace.getRootParentBlock();
            if(rootParentBlock == null){
                throw new FlowException("Root parent block can not be null for BreakBlock");
            }
            rootParentBlock.setBreakLoop(true);
        }catch (Exception exception){
            flowEngineInterFace.log(FlowLogLevel.SEVERE,exception.getMessage());
            FlowException flowException = exception instanceof FlowException?(FlowException)exception:new FlowException(exception.getMessage());
            flowEngineInterFace.emitBlockError(this,memory,flowException);
            throw flowException;
        }
    }

    @Override
    public void validateBlockConfiguration() throws FlowException {

    }
}

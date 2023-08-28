package com.facilio.flowengine.observers;

import com.facilio.blockfactory.blocks.BaseBlock;
import com.facilio.flowengine.exception.FlowException;
import com.facilio.flowengine.executor.FlowEngineInterFace;

import java.util.Map;

public abstract class Observer {
    public abstract void onFlowStart(FlowEngineInterFace flowEngineInterFace, Map<String, Object> memor) throws Exception;
    public abstract void onFlowError(FlowEngineInterFace flowEngineInterFace, Map<String, Object> memory, FlowException flowException) throws Exception;

    public abstract void onFlowEnd(FlowEngineInterFace flowEngineInterFace,Map<String, Object> memory) throws Exception;

    public abstract void onBlockStart(FlowEngineInterFace flowEngineInterFace,BaseBlock block, Map<String, Object> memory) throws Exception;

    public abstract void onBlockEnd(FlowEngineInterFace flowEngineInterFace,BaseBlock block, Map<String, Object> memory) throws Exception;
    public abstract void onBlockError(FlowEngineInterFace flowEngineInterFace,BaseBlock block, Map<String, Object> memory,FlowException flowException) throws Exception;


}


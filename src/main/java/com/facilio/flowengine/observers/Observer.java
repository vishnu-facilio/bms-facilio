package com.facilio.flowengine.observers;

import com.facilio.blockfactory.blocks.BaseBlock;

import java.util.Map;

public abstract class Observer {
    public abstract void onFlowStart(Map<String, Object> memory) throws Exception;

    public abstract void onFlowEnd(Map<String, Object> memory) throws Exception;

    public abstract void onBlockStart(BaseBlock block, Map<String, Object> memory) throws Exception;

    public abstract void onBlockEnd(BaseBlock block, Map<String, Object> memory) throws Exception;

}


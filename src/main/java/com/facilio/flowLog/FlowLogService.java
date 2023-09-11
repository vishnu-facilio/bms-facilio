package com.facilio.flowLog;

import com.facilio.flowengine.executor.FlowEngineInterFace;
import lombok.NonNull;

public abstract class FlowLogService {

    protected FlowEngineInterFace flowEngineInterFace;

    public FlowLogService(@NonNull FlowEngineInterFace flowEngineInterFace) {
        this.flowEngineInterFace = flowEngineInterFace;
    }
    abstract public void log(String message);
    abstract public void onFlowError() throws Exception;
    abstract public void onFlowStart() throws Exception;
    abstract public void onFlowEnd() throws Exception;
}


package com.facilio.flowengine.executor;

import com.facilio.blockfactory.blocks.BaseBlock;
import com.facilio.blockfactory.blocks.BaseLoopBlock;
import com.facilio.flowLog.FlowLogLevel;
import com.facilio.flowLog.FlowLogService;
import com.facilio.flowengine.exception.FlowException;
import com.facilio.flows.context.FlowContext;
import org.json.simple.JSONObject;

import java.util.Map;

public interface FlowEngineInterFace {
    FlowContext getFlow();
    JSONObject getCurrentRecord();
    BaseBlock getCurrentExecutionBlock();
    void log(FlowLogLevel logLevel,String logs);
    void setFlowLogService(FlowLogService flowLogService);
    FlowLogService getFlowLogService();
    void emitBlockError(BaseBlock block, Map<String,Object> memory, FlowException flowException);
    BaseLoopBlock getRootParentBlock();
}

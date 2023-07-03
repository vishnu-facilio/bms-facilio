package com.facilio.flowengine.executor;

import com.facilio.flows.context.FlowContext;
import org.json.simple.JSONObject;

public interface FlowEngineInterFace {
    FlowContext getFlow();
    JSONObject getCurrentRecord();
}

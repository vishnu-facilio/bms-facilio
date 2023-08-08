package com.facilio.flows.context;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class ScriptFlowTransitionContext extends FlowTransitionContext{
    private long functionId=-1l;
}

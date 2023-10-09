package com.facilio.fsm.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class PriorityContext extends V3Context {
    private String priority;
    private String displayName;
    private String color;
}


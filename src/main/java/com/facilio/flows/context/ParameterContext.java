package com.facilio.flows.context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParameterContext{

    private long id = -1;
    private long flowId = -1;
    private String parameter;

}

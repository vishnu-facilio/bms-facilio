package com.facilio.flows.context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParametersContext{

    private long id = -1;
    private long flowId = -1;
    private String parameters;

}

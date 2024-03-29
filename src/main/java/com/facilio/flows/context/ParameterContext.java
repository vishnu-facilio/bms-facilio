package com.facilio.flows.context;

import com.facilio.flowengine.enums.FlowVariableDataType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ParameterContext implements Serializable {

    private long id = -1;
    private long flowId = -1;
    private String parameterName;
    private FlowVariableDataType dataType;
}

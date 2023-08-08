package com.facilio.flowengine.context;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter@Setter
public class ExpressionGroup implements Serializable {
    private String joinType;
    private List<FlowCondition> conditions;

    @Override
    public String toString() {
        return "ExpressionGroup{" +
                "joinType='" + joinType + '\'' +
                ", conditions=" + conditions +
                '}';
    }
}

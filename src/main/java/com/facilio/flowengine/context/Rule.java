package com.facilio.flowengine.context;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter@Setter
public class Rule implements Serializable {
    private List<ExpressionGroup> groups;

    @Override
    public String toString() {
        return "Rule{" +
                "groups=" + groups +
                '}';
    }
}

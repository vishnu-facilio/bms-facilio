package com.facilio.flowengine.context;

import com.facilio.flowengine.operators.FlowOperator;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter@Setter
public class FlowCondition implements Serializable {
    private String joinType;
    private String left;
    private FlowOperator operator;
    private String right;
    public void setOperator(Integer operatorId) {
        this.operator = FlowOperator.getOperator(operatorId);
        if(this.operator == null){
            throw new IllegalArgumentException("Invalid flow operatorId:"+operatorId);
        }
    }

    @Override
    public String toString() {
        return "FlowCondition{" +
                "joinType='" + joinType + '\'' +
                ", left='" + left + '\'' +
                ", operator=" + operator +
                ", right='" + right + '\'' +
                '}';
    }
}

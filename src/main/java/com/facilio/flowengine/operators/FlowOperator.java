package com.facilio.flowengine.operators;

public interface FlowOperator {
    boolean evaluate(Object left,Object right);
    int getOperatorId();

    String getOperator();
    boolean isValueNeeded();
    default boolean isBothValueNotEmpty(Object left, Object right){
        if(left!=null && right!=null){
            return true;
        }
        return false;
    }
    static FlowOperator getOperator(Integer operatorId){
        return OperatorUtil.getOperatorMap().get(operatorId);
    }
}

package com.facilio.flowengine.operators;

import com.facilio.util.FacilioUtil;
import org.apache.commons.lang.math.NumberUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum BooleanOperators implements FlowOperator{
    IS_EMPTY(17,"is empty",false){
        @Override
        public boolean evaluate(Object left, Object right) {
            if(left == null){
                return true;
            }
            return false;
        }
    },
    IS_NOT_EMPTY(18,"is not empty",false){
        @Override
        public boolean evaluate(Object left, Object right) {
            if(left != null){
                return true;
            }
            return false;
        }
    },
    IS(19,"is",true){
        @Override
        public boolean evaluate(Object left, Object right) {
            boolean leftBool = FacilioUtil.parseBoolean(left);
            boolean rightBool = FacilioUtil.parseBoolean(right);

            return leftBool==rightBool;
        }
    };
    private int operatorId;

    @Override
    public int getOperatorId() {
        return operatorId;
    }

    @Override
    public String getOperator() {
        return operator;
    }

    private String operator;
    private boolean valueNeeded;

    @Override
    public boolean isValueNeeded() {
        return valueNeeded;
    }
    BooleanOperators(int operatorId, String operator,Boolean valueNeeded) {
        this.operatorId = operatorId;
        this.operator = operator;
        this.valueNeeded = valueNeeded;
    }
    private static final Map<Integer, FlowOperator> OPERATOR_MAP = Collections.unmodifiableMap(initOperatorMap());
    private static Map<Integer, FlowOperator> initOperatorMap() {
        Map<Integer, FlowOperator> operatorMap = new HashMap();
        for(FlowOperator operator:values()){
            operatorMap.put(operator.getOperatorId(),operator);
        }
        return operatorMap;
    }
    public static Map<Integer,FlowOperator> getOperatorMap(){
        return OPERATOR_MAP;
    }
}

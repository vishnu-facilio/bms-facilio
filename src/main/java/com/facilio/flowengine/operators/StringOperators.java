package com.facilio.flowengine.operators;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum StringOperators implements FlowOperator, Serializable {
    IS_EMPTY(1,"is empty",false){
        @Override
        public boolean evaluate(Object left, Object right) {
            if(left == null || StringUtils.isEmpty(left.toString())){
                return true;
            }
            return false;
        }
    },
    IS_NOT_EMPTY(2,"is not empty",false){
        @Override
        public boolean evaluate(Object left, Object right) {
            if(left != null && StringUtils.isNotEmpty(left.toString())){
                return true;
            }
            return false;
        }
    },
    IS(3,"is",true){
        @Override
        public boolean evaluate(Object left, Object right) {
            return (left == null ? right == null : stringEquals(left,right));
        }
    },
    IS_NOT(4,"isn't",true){
        @Override
        public boolean evaluate(Object left, Object right) {
            return !(left == null ? right == null : stringEquals(left,right));
        }
    },
    CONTAINS(5,"contains",true){
        @Override
        public boolean evaluate(Object left, Object right) {
            if(isBothValueNotEmpty(left,right)){
                if(left.toString().contains(right.toString())){
                    return true;
                }
            }
            return false;
        }
    },
    NOT_CONTAINS(6,"doesn't contain",true){
        @Override
        public boolean evaluate(Object left, Object right) {
            if(isBothValueNotEmpty(left,right)){
                if(!(left.toString().contains(right.toString()))){
                    return true;
                }
            }
            return false;
        }
    },
    STARTS_WITH(7,"starts with",true){
        @Override
        public boolean evaluate(Object left, Object right) {
            if(isBothValueNotEmpty(left,right)){
                if(left.toString().startsWith(right.toString())){
                    return true;
                }
            }
            return false;
        }
    },
    ENDS_WITH(8,"ends with",true){
        @Override
        public boolean evaluate(Object left, Object right) {
            if(isBothValueNotEmpty(left,right)){
                if(left.toString().endsWith(right.toString())){
                    return true;
                }
            }
            return false;
        }
    };
    private int operatorId;
    private String operator;
    private boolean valueNeeded;

    @Override
    public boolean isValueNeeded() {
        return valueNeeded;
    }

    @Override
    public int getOperatorId() {
        return operatorId;
    }

    @Override
    public String getOperator() {
        return operator;
    }

    StringOperators(int operatorId, String operator,Boolean valueNeeded) {
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
    public boolean stringEquals(Object left,Object right){
        if(isBothValueNotEmpty(left,right)){
           return left.toString().equals(right.toString());
        }
        return false;
    }
}

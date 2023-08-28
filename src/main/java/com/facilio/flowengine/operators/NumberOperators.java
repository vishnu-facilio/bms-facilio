package com.facilio.flowengine.operators;

import org.apache.commons.lang.math.NumberUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public enum NumberOperators implements FlowOperator, Serializable {
    EQUALS(9,"equals",true){
        @Override
        public boolean evaluate(Object left, Object right) {
            return left==null?right==null:numberEquals(left,right);
        }
    },
    NOT_EQUALS(10,"not equals",true){
        @Override
        public boolean evaluate(Object left, Object right) {
            return !(left==null?right==null:numberEquals(left,right));
        }
    },
    GREATER_THAN(11,"greater than",true){
        @Override
        public boolean evaluate(Object left, Object right) {
            if(isBothValueNotEmpty(left,right)){
                if(NumberUtils.isNumber(left.toString())&&NumberUtils.isNumber(right.toString())){
                    double l = Double.parseDouble(left.toString());
                    double r = Double.parseDouble(right.toString());
                    if(l>r){
                        return true;
                    }else {
                        return false;
                    }
                }
            }
            return false;
        }
    },
    GREATER_THAN_OR_EQUALS(12,"greater than or equals",true){
        @Override
        public boolean evaluate(Object left, Object right) {
            if(isBothValueNotEmpty(left,right)){
                if(NumberUtils.isNumber(left.toString())&&NumberUtils.isNumber(right.toString())){
                    double l = Double.parseDouble(left.toString());
                    double r = Double.parseDouble(right.toString());
                    if(l>=r){
                        return true;
                    }else {
                        return false;
                    }
                }
            }
            return false;
        }
    },
    LESSER_THAN(13,"lesser than",true){
        @Override
        public boolean evaluate(Object left, Object right) {
            if(isBothValueNotEmpty(left,right)){
                if(NumberUtils.isNumber(left.toString())&&NumberUtils.isNumber(right.toString())){
                    double l = Double.parseDouble(left.toString());
                    double r = Double.parseDouble(right.toString());
                    if(l<r){
                        return true;
                    }else {
                        return false;
                    }
                }
            }
            return false;
        }
    },
    LESSER_THAN_OR_EQUALS(14,"lesser than or equals",true){
        @Override
        public boolean evaluate(Object left, Object right) {
            if(isBothValueNotEmpty(left,right)){
                if(NumberUtils.isNumber(left.toString())&&NumberUtils.isNumber(right.toString())){
                    double l = Double.parseDouble(left.toString());
                    double r = Double.parseDouble(right.toString());
                    if(l<=r){
                        return true;
                    }else {
                        return false;
                    }
                }
            }
            return false;
        }
    },
    IS_EMPTY(15,"is empty",false){
        @Override
        public boolean evaluate(Object left, Object right) {
            if(left == null){
                return true;
            }
            return false;
        }
    },
    IS_NOT_EMPTY(16,"is not empty",false){
        @Override
        public boolean evaluate(Object left, Object right) {
            if(left != null){
                return true;
            }
            return false;
        }
    }
    ;

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

    NumberOperators(int operatorId, String operator, Boolean valueNeeded) {
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
    public boolean numberEquals(Object left,Object right){
        if(isBothValueNotEmpty(left,right)){
            if(NumberUtils.isNumber(left.toString())&&NumberUtils.isNumber(right.toString())){
                double l = Double.parseDouble(left.toString());
                double r = Double.parseDouble(right.toString());

                return l==r;
            }
        }
        return false;
    }
}
package com.facilio.flowengine.enums;

import com.facilio.flowengine.operators.BooleanOperators;
import com.facilio.flowengine.operators.FlowOperator;
import com.facilio.flowengine.operators.NumberOperators;
import com.facilio.flowengine.operators.StringOperators;
import com.facilio.modules.FacilioStringEnum;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public enum FlowVariableDataType implements FacilioStringEnum {
    NUMBER("Number", NumberOperators.getOperatorMap()){
        public boolean checkDataType(Object source){
            if(source instanceof Number){
                return true;
            }
            return false;
        }
    },
    STRING("String", StringOperators.getOperatorMap()){
        public boolean checkDataType(Object source){
            if(source instanceof String){
                return true;
            }
            return false;
        }
    },
    BOOLEAN("Boolean", BooleanOperators.getOperatorMap()){
        public boolean checkDataType(Object source){
            if(source instanceof Boolean){
                return true;
            }
            return false;
        }
    },
    MAP("Map",null){
        public boolean checkDataType(Object source){
            if(source instanceof Map){
                return true;
            }
            return false;
        }
    },
    LIST("List",null){
        public boolean checkDataType(Object source){
            if(source instanceof List){
                return true;
            }
            return false;
        }
    };
    private String dataType;
    private Map<Integer,FlowOperator> operatorIdVsOperatorMap;

    FlowVariableDataType(String dataType,Map<Integer,FlowOperator> operatorIdVsOperatorMap) {
        this.dataType = dataType;
        this.operatorIdVsOperatorMap = operatorIdVsOperatorMap;
    }
    private static final Map<String, FlowVariableDataType> DATA_TYPE_MAP = Collections.unmodifiableMap(initOperatorMap());
    private static Map<String, FlowVariableDataType> initOperatorMap() {
        Map<String, FlowVariableDataType> dataTypeMap = new HashMap();
        for(FlowVariableDataType dataType:values()){
           dataTypeMap.put(dataType.getIndex(),dataType);
        }
        return dataTypeMap;
    }
    public static Map<String, FlowVariableDataType> getDataTypeMap(){
        return DATA_TYPE_MAP;
    }
    public static FlowVariableDataType getDataType(String dataType){
        return DATA_TYPE_MAP.get(dataType);
    }
    public abstract boolean checkDataType(Object source);
}

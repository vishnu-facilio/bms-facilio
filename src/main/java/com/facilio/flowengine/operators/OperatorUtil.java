package com.facilio.flowengine.operators;

import com.facilio.flowengine.enums.FlowVariableDataType;
import org.apache.commons.collections4.MapUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OperatorUtil {
    private static Map<Integer,FlowOperator> opertorMap = Collections.unmodifiableMap(initOperatorMap());

    private static Map<Integer,FlowOperator> initOperatorMap(){
        Map<Integer,FlowOperator> opertorMap = new HashMap<>();
        Map<String, FlowVariableDataType> dataTypeMap = FlowVariableDataType.getDataTypeMap();
        for(Map.Entry<String,FlowVariableDataType> entry: dataTypeMap.entrySet()){
            FlowVariableDataType flowVariableDataType = entry.getValue();
            Map<Integer,FlowOperator> flowOperatorMap = flowVariableDataType.getOperatorIdVsOperatorMap();
            if(MapUtils.isNotEmpty(flowOperatorMap)){
                opertorMap.putAll(flowOperatorMap);
            }
        }
        return opertorMap;
    }
    public static Map<Integer,FlowOperator> getOperatorMap(){
        return opertorMap;
    }
}

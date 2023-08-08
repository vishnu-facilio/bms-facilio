package com.facilio.flows.command;

import com.facilio.command.FacilioCommand;
import com.facilio.flowengine.enums.FlowVariableDataType;
import com.facilio.flowengine.operators.FlowOperator;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

public class GetFlowOperatorsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,List<Map<String,Object>>> operatorsMap = new HashMap<>();
        Map<String, FlowVariableDataType> dataTypeMap = FlowVariableDataType.getDataTypeMap();
        for(String dataTypeName : dataTypeMap.keySet()){
            FlowVariableDataType dataType = dataTypeMap.get(dataTypeName);
            Map<Integer, FlowOperator> operatorIdVsOperatorMap = dataType.getOperatorIdVsOperatorMap();
            if(MapUtils.isNotEmpty(operatorIdVsOperatorMap)){
                List<Map<String,Object>> list = new ArrayList();
                for(FlowOperator operator : operatorIdVsOperatorMap.values()){
                    Map<String,Object> props = new HashMap<>();
                    props.put("name",operator);
                    props.put("displayName",operator.getOperator());
                    props.put("operatorId",operator.getOperatorId());
                    props.put("valueNeeded",operator.isValueNeeded());
                    list.add(props);
                }
                operatorsMap.put(dataTypeName,list);
            }
        }
        context.put("operatorsMap",operatorsMap);
        return false;
    }
}

package com.facilio.blockfactory.blocks;

import com.facilio.blockfactory.blocks.BaseBlock;
import com.facilio.flowengine.context.Constants;
import com.facilio.flowengine.exception.FlowException;
import com.facilio.flowengine.executor.FlowEngineUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter@Setter
public class SetVariableBlock extends BaseBlock {
    private String variableName;
    private Object variableValue;

    public SetVariableBlock(Map<String,Object> config) throws Exception{
        super(config);
    }
    @Override
    public void execute(Map<String, Object> context) throws FlowException{
        try{
            init();
            if(variableValue instanceof String){
                variableValue = FlowEngineUtil.replacePlaceHolder(variableValue,context);
            }
            context.put(variableName,variableValue);
        }catch (Exception ex){
            if (ex instanceof FlowException){
                throw (FlowException) ex;
            }else {
                throw new FlowException(ex.getMessage());
            }
        }
    }
    private void init(){
        this.variableName= (String) config.get(Constants.VARIABLE_NAME);
        this.variableValue = config.get(Constants.VARIABLE_VALUE);
    }

    @Override
    public void validateBlockConfiguration() throws FlowException {
        Object variableName = config.get(Constants.VARIABLE_NAME);
        Object variableValue = config.get(Constants.VARIABLE_VALUE);
        if(variableName == null){
            throw new FlowException("variableName can not be empty for SetVariableBlock");
        }
        if(!(variableName instanceof String)){
            throw new FlowException("variableName:'"+variableName+"' not a string for SetVariableBlock");
        }
        if(variableValue == null){
            throw new FlowException("variableValue can not be empty for SetVariableBlock");
        }
    }
}

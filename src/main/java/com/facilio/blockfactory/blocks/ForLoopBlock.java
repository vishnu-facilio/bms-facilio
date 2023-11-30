package com.facilio.blockfactory.blocks;

import com.facilio.flowLog.FlowLogLevel;
import com.facilio.flowengine.context.Constants;
import com.facilio.flowengine.exception.FlowException;
import com.facilio.flowengine.executor.FlowEngineUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class ForLoopBlock extends BaseLoopBlock {
    private String variableName;
    private Object iterableObjects;
    private Iterator iterator;
    private Object currentObject;
    public ForLoopBlock(Map<String, Object> config) {
        super(config);
    }

    @Override
    public void execute(Map<String, Object> memory) throws FlowException {
        try{
            init();
            setExecutablePosition("2");
            Object replacedIterableObjects = FlowEngineUtil.replacePlaceHolder(iterableObjects,memory);

            if(replacedIterableObjects == null){
               return;
            }
            if(!(replacedIterableObjects instanceof Collection)){
                throw new FlowException("iteratingObject data type should be a Collection");
            }
            Collection itrObjects = (Collection)replacedIterableObjects;
            iterator = itrObjects.iterator();
        }catch (Exception exception){
            flowEngineInterFace.log(FlowLogLevel.SEVERE,exception.getMessage());
            FlowException flowException = exception instanceof FlowException?(FlowException)exception:new FlowException(exception.getMessage());
            flowEngineInterFace.emitBlockError(this,memory,flowException);
            throw flowException;
        }

    }
    @Override
    public  boolean hasNext(){
        return iterator!=null?iterator.hasNext():false;
    }
    @Override
    public Object next(){
        currentObject = iterator.next();
        return currentObject;
    }

    @Override
    public void putToMemory(Map<String, Object> memory, Object itrObject) {
        memory.put(variableName,itrObject);
    }

    @Override
    public final BaseBlock getNextBlock() {
        if (StringUtils.isNotEmpty(executablePosition) && MapUtils.isNotEmpty(blockMap)) {
            return blockMap.get(executablePosition);
        }
        return null;
    }
    private void init(){
        this.variableName= (String) config.get(Constants.VARIABLE_NAME);
        this.iterableObjects = config.get(Constants.ForLoopBlockConstants.ITERABLE_OBJECTS);
    }

    @Override
    public void validateBlockConfiguration() throws FlowException {
        Object variableName = config.get(Constants.VARIABLE_NAME);
        Object iterableObjects = config.get(Constants.ForLoopBlockConstants.ITERABLE_OBJECTS);
        if(variableName == null){
            throw new FlowException("variableName can not be empty for ForLoopBlock");
        }
        if(!(variableName instanceof String)){
            throw new FlowException("variableName:'"+variableName+"' not a string for ForLoopBlock");
        }
        if(iterableObjects==null){
            throw new FlowException("iterableObjects can not be empty for ForLoopBlock");
        }
        if(!(iterableObjects instanceof String)){
            throw new FlowException("iterableObjects should be place holder");
        }
    }
}

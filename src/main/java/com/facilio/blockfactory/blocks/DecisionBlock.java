package com.facilio.blockfactory.blocks;


import com.facilio.flowengine.exception.FlowException;
import com.facilio.flowengine.executor.FlowEngineUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public abstract class DecisionBlock extends BaseBlock {

    private String executablePosition;

    public DecisionBlock(Map<String, Object> config) {
        super(config);
    }

    @Override
    public final BaseBlock getNextBlock() {
        if (StringUtils.isNotEmpty(executablePosition) && MapUtils.isNotEmpty(blockMap)) {
            return blockMap.get(executablePosition);
        }
        return null;
    }

    protected void setExecutablePosition(String executablePosition) {
        this.executablePosition = executablePosition;
    }

    public Boolean evaluate(Map<String, Object> memory, String expressionStr) throws Exception {
        Object ob = FlowEngineUtil.evaluateExpression(memory,expressionStr);
        if(!(ob instanceof Boolean)){
            throw new FlowException("Evaluated expression value is not a boolean for {"+expressionStr+"} in DecisionBlock");
        }
        return (Boolean) ob;
    }
}

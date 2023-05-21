package com.facilio.blockfactory.blocks;

import com.facilio.flowengine.context.Constants;
import com.facilio.flowengine.exception.FlowException;

import java.util.Map;

public class If_Else_Block extends DecisionBlock {
    private String condition;

    public If_Else_Block(Map<String, Object> config) {
        super(config);
    }

    @Override
    public void execute(Map<String, Object> context) throws FlowException {
        try {
            init();
            boolean flag = evaluate(context, condition);
            if (flag) {
                setExecutablePosition("2");
            } else {
                setExecutablePosition("1");
            }
        } catch (Exception ex) {
            if (ex instanceof FlowException) {
                throw (FlowException) ex;
            } else {
                throw new FlowException(ex.getMessage());
            }
        }
    }

    private void init() {
        this.condition = (String) config.get(Constants.CONDITION);
    }

    public void validateBlockConfiguration() throws FlowException {
        Object condition = config.get(Constants.CONDITION);
        if (condition == null) {
            throw new FlowException("condition can not be empty for If_Else_Block");
        }
        if (!(condition instanceof String)) {
            throw new FlowException("condition:'" + condition + "' is not a valid expression string for If_Else_Block");
        }
    }
}

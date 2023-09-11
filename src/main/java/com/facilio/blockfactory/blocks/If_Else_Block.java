package com.facilio.blockfactory.blocks;

import com.facilio.flowLog.FlowLogLevel;
import com.facilio.flowengine.context.Constants;
import com.facilio.flowengine.context.Rule;
import com.facilio.flowengine.exception.FlowException;
import com.facilio.modules.FieldUtil;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Map;

public class If_Else_Block extends DecisionBlock {
    private Rule rule;
    private JSONObject jsonObject;

    public If_Else_Block(Map<String, Object> config) {
        super(config);
    }

    @Override
    public void execute(Map<String, Object> memory) throws FlowException {
        try {
            init();
            boolean flag = evaluate(memory, rule);
            if (flag) {
                setExecutablePosition("2");
            } else {
                setExecutablePosition("1");
            }
        } catch (Exception exception) {
            flowEngineInterFace.log(FlowLogLevel.SEVERE,exception.getMessage());
            FlowException flowException = exception instanceof FlowException?(FlowException)exception:new FlowException(exception.getMessage());
            flowEngineInterFace.emitBlockError(this,memory,flowException);
            throw flowException;
        }
    }

    private void init() throws IOException {
        this.jsonObject = (JSONObject) config.get(Constants.RULE);
        this.rule = FieldUtil.getAsBeanFromJson(jsonObject, Rule.class);
    }

    public void validateBlockConfiguration() throws FlowException {
        Object jsonObject = config.get(Constants.RULE);
        if (jsonObject == null) {
            throw new FlowException("rule can not be empty for If_Else_Block");
        }
        if (!(jsonObject instanceof JSONObject)) {
            throw new FlowException("rule:'" + jsonObject + "' is not a valid JSONObject If_Else_Block");
        }
        try{
            FieldUtil.getAsBeanFromJson((JSONObject) jsonObject, Rule.class);
        }catch (Exception e){
            throw new FlowException("Rule jsonObject can not be converted to Rule instance:"+e.getMessage());
        }

    }
}

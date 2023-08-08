package com.facilio.blockfactory.blocks;

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
    public void execute(Map<String, Object> context) throws FlowException {
        try {
            init();
            boolean flag = evaluate(context, rule);
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

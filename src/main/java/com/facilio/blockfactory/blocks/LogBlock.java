package com.facilio.blockfactory.blocks;

import com.facilio.flowengine.exception.FlowException;
import com.facilio.modules.FieldUtil;

import java.util.LinkedHashMap;
import java.util.Map;

public class LogBlock extends BaseBlock {

    public LogBlock(Map<String, Object> config) {
        super(config);
    }

    @Override
    public void execute(Map<String, Object> context) throws FlowException {
        try {
            context.put(getId().toString(), FieldUtil.cloneBean(context, LinkedHashMap.class));
        }catch (Exception e){
            throw new FlowException(e.getMessage());
        }
    }

    @Override
    public void validateBlockConfiguration() throws FlowException {

    }
}

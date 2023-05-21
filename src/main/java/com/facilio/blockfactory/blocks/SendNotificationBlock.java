package com.facilio.blockfactory.blocks;

import com.facilio.flowengine.exception.FlowException;

import java.util.Map;

public class SendNotificationBlock extends BaseBlock {
    SendNotificationBlock(Map<String, Object> config) {
        super(config);
    }

    @Override
    public void execute(Map<String, Object> context) throws FlowException{
    }

    @Override
    public void validateBlockConfiguration() throws FlowException {

    }
}

package com.facilio.wmsv2.handler;

import com.facilio.wmsv2.endpoint.WmsBroadcaster;
import com.facilio.wmsv2.message.Message;

public class PingHandler extends BaseHandler {

    @Override
    public void processIncomingMessage(Message message) {
        WmsBroadcaster.getBroadcaster().sendMessage(message);
    }

}
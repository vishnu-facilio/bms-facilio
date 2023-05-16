package com.facilio.wmsv2.handler;

import com.facilio.db.util.DBConf;
import com.facilio.wmsv2.message.Message;

public class PingHandler extends BaseHandler {

    @Override
    public void processIncomingMessage(Message message) {
        message = Processor.getInstance().filterOutgoingMessage(message);
        BaseHandler handler = Processor.getInstance().getHandler(message.getTopic());
        handler.processOutgoingMessage(message);
        if(handler.isLiveMessage()) {
            DBConf.getInstance().pushToLiveSession(message);
        }
    }
}
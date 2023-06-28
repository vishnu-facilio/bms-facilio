package com.facilio.wmsv2.handler;

import com.facilio.wmsv2.message.WebMessage;
import com.facilio.wmsv2.util.WmsUtil;

public class PingHandler extends WmsHandler {

    @Override
    public void processIncomingMessage(WebMessage message) {
        message = WmsProcessor.getInstance().filterOutgoingMessage(message);
        WmsUtil.sendObject(message.getLiveSession(), message);
    }
}
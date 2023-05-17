package com.facilio.wmsv2.handler;

import com.facilio.wmsv2.message.Message;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;


@Log4j
public class ResourceDecommissioningHandler extends BaseHandler{
    public static final String TOPIC = "resource-decommission";
    @Override
    @SneakyThrows
    public void processOutgoingMessage(Message message) {
        try {
            if(message.getOrgId() != null && message.getOrgId() > 0) {

                // impl
            }
        } catch (Exception e) {
            LOGGER.error("error occurred", e);
        }
    }
}
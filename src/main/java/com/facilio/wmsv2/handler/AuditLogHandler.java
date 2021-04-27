package com.facilio.wmsv2.handler;

import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;

import java.util.logging.Logger;

@TopicHandler(
        topic = Topics.System.auditLogs,
        priority = -5,
        deliverTo = TopicHandler.DELIVER_TO.SESSION
)
public class AuditLogHandler extends BaseHandler {

    private static final Logger LOGGER = Logger.getLogger(AuditLogHandler.class.getName());

    @Override
    public void processIncomingMessage(Message message) {
        LOGGER.severe(message.toString());
    }

    @Override
    public Message processOutgoingMessage(Message message) {
        LOGGER.severe(message.toString());
        return null;
    }
}

package com.facilio.wmsv2.handler;

import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;

@TopicHandler(
        topic = Topics.System.agentpoints,
        priority = -10,
        deliverTo = TopicHandler.DELIVER_TO.SESSION
)
public class AgentPointHandler extends BaseHandler {
	
	@Override
	public Message processOutgoingMessage(Message message) {
		return message;
	}
}

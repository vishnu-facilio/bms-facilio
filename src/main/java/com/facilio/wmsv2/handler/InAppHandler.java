package com.facilio.wmsv2.handler;

import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.message.TopicHandler;

@TopicHandler(
        topic = Topics.InApp.inApp,
        priority = -6,
        deliverTo = TopicHandler.DELIVER_TO.USER
)
public class InAppHandler extends BaseHandler{

}

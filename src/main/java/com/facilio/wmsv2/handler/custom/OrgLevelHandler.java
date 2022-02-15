package com.facilio.wmsv2.handler.custom;

import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.handler.BaseHandler;
import com.facilio.wmsv2.message.TopicHandler;

@TopicHandler(
        topic = Topics.System.customOrg + "/#",
        priority = -10,
        deliverTo = TopicHandler.DELIVER_TO.ORG
)
public class OrgLevelHandler extends BaseHandler {
}

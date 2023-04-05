package com.facilio.wmsv2.handler;

import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.message.Group;
import com.facilio.wmsv2.message.TopicHandler;
import lombok.extern.log4j.Log4j;

@TopicHandler(
        topic = Topics.MultiImport.multiImport,
        priority = -6,
        deliverTo = TopicHandler.DELIVER_TO.ORG,
        recordTimeout = 60,
        group= Group.DEFAULT
)
@Log4j
public class MultiImportErrorRecordsDownloadHandler extends BaseHandler{
}

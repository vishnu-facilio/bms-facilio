package com.facilio.wmsv2.handler;

import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.bean.MailBean;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.message.Group;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

@TopicHandler(
        topic = Topics.Mail.prepareOutgoingMail + "/#",
        group = Group.PRE_SEND_MAIL_WORKER,
        priority = -5,
        recordTimeout = 180 // 3 mins
)
@Log4j
public class OutgoingPreprocessHandler extends BaseHandler {

    @Override
    public Message processOutgoingMessage(Message message) {
        Long orgId = message.getOrgId();
        try {
            if(orgId != null && orgId > 0) {
                JSONObject content = message.getContent();
                MailBean mailBean = MailConstants.getMailBean(orgId);
                mailBean.prepareAndPushMail(content);
                LOGGER.info("OG_MAIL_LOG :: Received data to OutgoingPreprocessHandler for ORGID "+ orgId + " with topic :: "+message.getTopic());
            }
        } catch (Exception e) {
            LOGGER.error("OG_MAIL_ERROR :: ERROR IN [OutgoingPreprocessHandler] for ORGID "+ orgId + " with topic :: "+message.getTopic(), e);
        }
        return null;
    }
}

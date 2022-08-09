package com.facilio.wmsv2.handler;

import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.bean.MailBean;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

@TopicHandler(
        topic = Topics.Mail.outgoingMail + "/#",
        priority = -5
)
@Log4j
public class OutgoingMailHandler extends BaseHandler {

    @Override
    public Message processOutgoingMessage(Message message) {
        try {
            if(message.getOrgId() != null && message.getOrgId() > 0) {
                JSONObject mailJson = message.getContent();
                MailBean mailBean = MailConstants.getMailBean(message.getOrgId());
                mailBean.trackAndSendMail(mailJson);
                LOGGER.info("Sending mail via wms");
            }
        } catch (Exception e) {
            LOGGER.info("ERROR IN OutgoingMailHandler", e);
        }
        return null;
    }
}

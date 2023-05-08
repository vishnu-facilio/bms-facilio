package com.facilio.wmsv2.handler;

import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.bean.MailBean;
import com.facilio.wmsv2.message.Message;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

@Log4j
public class OutgoingPreprocessHandler extends BaseHandler {

    @Override
    public void processOutgoingMessage(Message message) {
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
    }
}

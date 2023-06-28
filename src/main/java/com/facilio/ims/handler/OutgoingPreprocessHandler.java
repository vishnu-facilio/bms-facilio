package com.facilio.ims.handler;

import com.facilio.fms.message.Message;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.bean.MailBean;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

@Log4j
public class OutgoingPreprocessHandler extends ImsHandler {

    public static String KEY = "__prepare_ogmail__/org";

    @Override
    public void processMessage(Message message) {
        Long orgId = message.getOrgId();
        try {
            if(orgId != null && orgId > 0) {
                JSONObject content = message.getContent();
                MailBean mailBean = MailConstants.getMailBean(orgId);
                mailBean.prepareAndPushMail(content);
                LOGGER.info("OG_MAIL_LOG :: Received data to OutgoingPreprocessHandler for ORGID "+ orgId + " with partition key :: "+message.getKey());
            }
        } catch (Exception e) {
            LOGGER.error("OG_MAIL_ERROR :: ERROR IN [OutgoingPreprocessHandler] for ORGID "+ orgId + " with partition key :: "+message.getKey(), e);
        }
    }
}

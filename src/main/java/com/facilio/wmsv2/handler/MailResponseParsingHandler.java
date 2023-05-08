package com.facilio.wmsv2.handler;

import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.commands.ParseMailResponseCommand;
import com.facilio.mailtracking.context.AwsMailResponseContext;
import com.facilio.modules.FieldUtil;
import com.facilio.wmsv2.message.Message;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

@Log4j
public class MailResponseParsingHandler extends BaseHandler {

    @Override
    public void processOutgoingMessage(Message message) {
        AwsMailResponseContext awsMailResponse = null;
        try {
            if(message!=null && message.getContent()!=null) {
                JSONObject content = message.getContent();
                awsMailResponse = FieldUtil.getAsBeanFromJson(content, AwsMailResponseContext.class);
                awsMailResponse.setResponse(content);
                OutgoingMailAPI.logResponses(awsMailResponse);

                ParseMailResponseCommand.executeCommand(awsMailResponse);
            }
        } catch (Exception e) {
            LOGGER.info("OG_MAIL_ERROR :: ERROR IN MailResponseParsingHandler for MAPPER_ID "
                    + awsMailResponse.getMapperId(), e);
        }
    }
}

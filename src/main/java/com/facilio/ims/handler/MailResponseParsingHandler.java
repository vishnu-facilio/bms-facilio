package com.facilio.ims.handler;

import com.facilio.fms.message.Message;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.commands.ParseMailResponseCommand;
import com.facilio.mailtracking.context.AwsMailResponseContext;
import com.facilio.modules.FieldUtil;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

@Log4j
public class MailResponseParsingHandler extends ImsHandler {

    public static String KEY = "__mailresponse__";

    @Override
    public void processMessage(Message message) {
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

package com.facilio.mailtracking.actions;

import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.ims.handler.MailResponseParsingHandler;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.AwsMailResponseContext;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import com.opensymphony.xwork2.ActionSupport;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Data
@Log4j
public class OutgoingMailResponseAction extends ActionSupport {

    private static final long serialVersionUID = 1L;

    private String status;

    @Override
    public String execute() throws Exception {
        JSONObject requestJson = null;
        AwsMailResponseContext awsMailResponse = null;
        try {
            requestJson = this.parseRequestData();
            awsMailResponse = FieldUtil.getAsBeanFromJson(requestJson, AwsMailResponseContext.class);
            V3Util.throwRestException(
                    awsMailResponse == null || StringUtils.isEmpty(awsMailResponse.getEventType()),
                    ErrorCode.RESOURCE_NOT_FOUND,
                    "eventType :: not found (look for SubscribeURL in data)"
            );

            JSONObject mail = (JSONObject) requestJson.get("mail");
            V3Util.throwRestException(mail == null || mail.isEmpty(), ErrorCode.VALIDATION_ERROR, "mail can't be null");

            Map<String, Object> tags = (Map<String, Object>) mail.get("tags");
            V3Util.throwRestException(tags == null || tags.isEmpty(), ErrorCode.VALIDATION_ERROR, "tags can't be null");

            List<String> mapperIdList = (List<String>) tags.get(MailConstants.Params.MAPPER_ID);
            V3Util.throwRestException(CollectionUtils.isEmpty(mapperIdList), ErrorCode.VALIDATION_ERROR,
                    "mapperIdList can't be null");

            String mapperId = mapperIdList.get(0);
            V3Util.throwRestException(StringUtils.isEmpty(mapperId), ErrorCode.VALIDATION_ERROR, "mapperId can't be null");

            requestJson.put(MailConstants.Params.MAPPER_ID, mapperId);
            Messenger.getMessenger().sendMessage(new Message()
                            .setKey(MailResponseParsingHandler.KEY +"/"+mapperId)
                            .setContent(requestJson));
            status = "Successfully pushed outgoing  MAIL-RESPONSE for MAPPER_ID :: "+mapperId
                    + " with eventType :: "+awsMailResponse.getEventType();
            LOGGER.info("OG_MAIL_LOG :: "+status);
            return SUCCESS;
        } catch (Exception e) {
            this.logRequestJson(awsMailResponse, requestJson);
            LOGGER.error("OG_MAIL_ERROR :: OutgoingMailResponse parsing failed.. ");
            String subUrl = (String) requestJson.getOrDefault("SubscribeURL", "-1");
            if(!subUrl.equals("-1")) {
                LOGGER.error("OG_MAIL_ERROR :: SubscribeURL detected from aws. Please sign up :: "+subUrl);
                this.parserErrorMsg(e, requestJson);
                return SUCCESS;
            }
            this.parserErrorMsg(e, requestJson);
            return ERROR;
        }
    }

    private void logRequestJson(AwsMailResponseContext awsMailResponse, JSONObject requestJson) throws Exception {
        if(awsMailResponse == null) {
            awsMailResponse = new AwsMailResponseContext();
        }
        awsMailResponse.setResponse(requestJson);
        OutgoingMailAPI.logResponses(awsMailResponse);
    }

    private void parserErrorMsg(Exception e, JSONObject requestJson) {
        LOGGER.error("OG_MAIL_DATA :: failedData :: "+requestJson);
        if(e.getMessage() == null) {
            status = e + " at " + e.getStackTrace()[0];
        } else {
            status = e.getMessage() + " at "+ e.getStackTrace()[1];
        }
        LOGGER.error("OG_MAIL_ERROR :: " + status, e);
    }

    private JSONObject parseRequestData() throws Exception {
        JSONObject requestJson;
        HttpServletRequest request = ServletActionContext.getRequest();
//        ((SecurityRequestWrapper)((org.apache.struts2.dispatcher.StrutsRequestWrapper) request).getRequest()).reset();
        try(InputStreamReader data = new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8)) {
            JSONParser jsonParser = new JSONParser();
            requestJson = (JSONObject)jsonParser.parse(data);
        }
        return requestJson == null ? new JSONObject() : requestJson;
    }

}

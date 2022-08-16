package com.facilio.mailtracking.actions;

import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.AwsMailResponseContext;
import com.facilio.modules.FieldUtil;
import com.facilio.security.SecurityRequestWrapper;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import com.opensymphony.xwork2.ActionSupport;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Data
@Log4j
public class OutgoingMailResponseAction extends ActionSupport {

    private static final long serialVersionUID = 1L;

    private String status;

    @Override
    public String execute() throws Exception {
        JSONObject requestJson = null;
        try {
            requestJson = parseRequestData();
            AwsMailResponseContext awsMailResponseContext = FieldUtil.getAsBeanFromJson(requestJson, AwsMailResponseContext.class);
            V3Util.throwRestException(
                    awsMailResponseContext == null || awsMailResponseContext.getEventType() == null,
                    ErrorCode.RESOURCE_NOT_FOUND,
                    "eventType :: not found (look for SubscribeURL in data)"
            );
            String mapperId = OutgoingMailAPI.parseMailResponse(awsMailResponseContext);
            status = "Successfully parsed and updated aws mail responses for mapperId :: "+mapperId;
            LOGGER.info(status);
            return SUCCESS;
        } catch (Exception e) {
            LOGGER.error("OG_MAIL_ERROR :: OutgoingMailResponse parsing failed.. ");
            String subUrl = (String) requestJson.getOrDefault("SubscribeURL", "-1");
            if(!subUrl.equals("-1")) {
                LOGGER.error("SubscribeURL detected from aws. Please sign up :: "+subUrl);
            }
            LOGGER.error("failedData :: "+requestJson);
            parserErrorMsg(e);
            return ERROR;
        }
    }

    private void parserErrorMsg(Exception e) throws Exception {

        if(e.getMessage() == null) {
            status = e.getStackTrace()[0] + " :: " +e.toString();
        } else {
            status = e.getStackTrace()[1] + " :: "+e.getMessage();
        }
        LOGGER.error("OG_MAIL_ERROR :: ", e);
    }

    private JSONObject parseRequestData() throws Exception {
        JSONObject requestJson = null;
        HttpServletRequest request = ServletActionContext.getRequest();
        ((SecurityRequestWrapper)((org.apache.struts2.dispatcher.StrutsRequestWrapper) request).getRequest()).reset();
        try(InputStreamReader data = new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8);) {
            JSONParser jsonParser = new JSONParser();
            requestJson = (JSONObject)jsonParser.parse(data);
        }
        return requestJson;
    }

}

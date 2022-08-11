package com.facilio.mailtracking.actions;

import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.AwsMailResponseContext;
import com.facilio.security.SecurityRequestWrapper;
import com.opensymphony.xwork2.ActionSupport;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.json.JSONUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Data
@Log4j
public class OutgoingMailResponseAction extends ActionSupport {

    private static final long serialVersionUID = 1L;

    private String eventType;
    private JSONObject mail;
    private JSONObject delivery;
    private JSONObject bounce;
    private String status;

    @Override
    public String execute() throws Exception {
        try {
            AwsMailResponseContext awsMailResponseContext = new AwsMailResponseContext(eventType, mail);
            awsMailResponseContext.setDelivery(delivery);
            awsMailResponseContext.setBounce(bounce);
            String mapperId = OutgoingMailAPI.parseMailResponse(awsMailResponseContext);
            status = "Successfully parsed and updated aws mail responses for mapperId :: "+mapperId;
            LOGGER.info(status);
        } catch (Exception e) {
            parserErrorMsg(e);
            return ERROR;
        }
        return SUCCESS;
    }

    private void parserErrorMsg(Exception e) throws Exception {
        LOGGER.error("OG_MAIL_ERROR :: OutgoingMailResponse parsing failed.. ");
        if(e.getMessage() == null) {
            status = e.getStackTrace()[0] + " :: " +e.toString();
        } else {
            status = e.getStackTrace()[1] + " :: "+e.getMessage();
        }
        topicSignatureCheck();
        LOGGER.error("OG_MAIL_ERROR :: ", e);
    }

    private void topicSignatureCheck() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        ((SecurityRequestWrapper)((org.apache.struts2.dispatcher.StrutsRequestWrapper) request).getRequest()).reset();
        try(InputStreamReader data = new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8);) {
            JSONParser jsonParser = new JSONParser();
            JSONObject inputJson = (JSONObject)jsonParser.parse(data);
            if(inputJson.containsKey("SubscribeURL")) {
                LOGGER.error("SubscribeURL detected from aws. Please sign up");
            }
            LOGGER.error("failedData :: "+inputJson);
        }
    }

}

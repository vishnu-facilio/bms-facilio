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
        } catch (Exception e) {
            topicSignatureCheck();
            getErrMsg(e);
            return ERROR;
        }
        return SUCCESS;
    }

    private void getErrMsg(Exception e) {
        status = e.getMessage();
        if(status == null) {
            StringBuffer er = new StringBuffer();
            er.append(e);
            er.append(" at ");
            StackTraceElement st = e.getStackTrace()[0];
            er.append(st.getClassName());
            er.append(".");
            er.append(st.getMethodName());
            er.append("(");
            er.append(st.getFileName());
            er.append(":");
            er.append(st.getLineNumber());
            er.append(")");
            status = er.toString();
        }
    }

    private void topicSignatureCheck() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        ((SecurityRequestWrapper)((org.apache.struts2.dispatcher.StrutsRequestWrapper) request).getRequest()).reset();
        try(InputStreamReader data = new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8);) {
            Map requestMap = (Map) JSONUtil.deserialize(data);
            if(requestMap.containsKey("SubscribeURL")) {
                LOGGER.info("SubscribeURL detected from aws. Please sign up");
                LOGGER.info(requestMap);
            }
        }
    }

}

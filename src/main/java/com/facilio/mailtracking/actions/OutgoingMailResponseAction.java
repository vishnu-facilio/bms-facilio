package com.facilio.mailtracking.actions;

import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.AwsMailResponseContext;
import com.opensymphony.xwork2.ActionSupport;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

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
            status = e.getMessage();
            return ERROR;
        }
        return SUCCESS;
    }

}

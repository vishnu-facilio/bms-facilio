package com.facilio.mailtracking.bean;

import org.json.simple.JSONObject;

public interface MailBean {

    public void updateDeliveryStatus(String mapperId, JSONObject delivery, boolean delayed) throws Exception;

    public void updateBounceStatus(String mapperId, JSONObject bounce) throws Exception;

    public void trackAndSendMail(JSONObject mailJson) throws Exception;

}

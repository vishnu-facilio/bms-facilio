package com.facilio.mailtracking.bean;

import org.json.simple.JSONObject;

import java.util.Map;

public interface MailBean {

    public void updateDeliveryStatus(String mapperId, Map<String, Object> delivery) throws Exception;

    public void updateBounceStatus(String mapperId, Map<String, Object> bounce) throws Exception;

    public void prepareAndPushMail(JSONObject mailJson) throws Exception;

    public void trackAndSendMail(JSONObject mailJson) throws Exception;

}

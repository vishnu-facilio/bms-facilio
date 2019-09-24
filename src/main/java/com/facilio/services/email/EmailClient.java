package com.facilio.services.email;

import org.json.simple.JSONObject;

import java.util.Map;

public interface EmailClient {
    void sendEmail(JSONObject mailJson) throws Exception;
    void sendEmail(JSONObject mailJson, Map<String, String> files) throws Exception;
    void sendErrorMail(long orgid, long ml_id, String error);
    void logEmail(JSONObject mailJson);
}

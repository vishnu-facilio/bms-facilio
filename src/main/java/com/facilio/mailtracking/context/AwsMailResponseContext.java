package com.facilio.mailtracking.context;

import lombok.Data;
import org.json.simple.JSONObject;

@Data
public class AwsMailResponseContext {

    private String eventType;
    private JSONObject mail;
    private JSONObject delivery;
    private JSONObject bounce;

}

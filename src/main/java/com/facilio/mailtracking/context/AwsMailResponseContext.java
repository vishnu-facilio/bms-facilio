package com.facilio.mailtracking.context;

import lombok.Data;
import org.json.simple.JSONObject;

@Data
public class AwsMailResponseContext {

    private String eventType;
    private Long mapperId;

    private JSONObject response;

}

package com.facilio.analytics.v2.context;

import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.HashMap;

@Setter
@Getter
public class V2CardResponseContext {

    private JSONObject value;
    private JSONObject baseline;
    private V2TimeFilterContext timeFilter;
    private JSONObject styles;
    private JSONObject cardStyle;
}

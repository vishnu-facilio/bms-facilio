package com.facilio.analytics.v2.context;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.Map;

@Setter
@Getter
public class V2CardContextForDashboardFilter {
    private Map<Long, JSONObject> db_user_filter;
    private Map<Long, V2TimeFilterContext> timeFilter;
    private String cardType = "telemetry";
}

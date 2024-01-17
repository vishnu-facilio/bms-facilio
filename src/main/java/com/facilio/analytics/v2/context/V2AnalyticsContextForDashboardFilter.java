package com.facilio.analytics.v2.context;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
@Setter
public class V2AnalyticsContextForDashboardFilter {

    private JSONObject db_user_filter;
    private V2TimeFilterContext  timeFilter;
    private JSONObject pagination;
}

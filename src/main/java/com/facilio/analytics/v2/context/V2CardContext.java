package com.facilio.analytics.v2.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Setter
@Getter
public class V2CardContext {

    private long cardId=-1l;
    private V2AnalyticsCardWidgetContext cardParams;
    public JSONObject conditionalFormatting;
    public JSONObject cardState = new JSONObject();
    public String cardLayout = "v2_reading_card";
    private JSONObject cardDrillDown;
}

package com.facilio.timeline.context;

import java.util.Map;

public class TimelineRecordContext {

    private Map<String,Object> data;
    public Map<String, Object> getData() {
        return data;
    }
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    private String customization;
    public String getCustomization() {
        return customization;
    }
    public void setCustomization(String customization) {
        this.customization = customization;
    }
}

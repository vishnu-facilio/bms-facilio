package com.facilio.bmsconsole.templates;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.json.simple.JSONObject;

public class ControlActionTemplate extends Template {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String metric;
    public String getMetric() {
        return metric;
    }
    public void setMetric(String metric) {
        this.metric = metric;
    }

    private String resource;
    public String getResource() {
        return resource;
    }
    public void setResource(String resource) {
        this.resource = resource;
    }

    private String val;
    public String getVal() {
        return val;
    }
    public void setVal(String val) {
        this.val = val;
    }

    @Override
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public int getType() {
        return Type.CONTROL_ACTION.getIntVal();
    }
    @Override
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public Type getTypeEnum() {
        return Type.CONTROL_ACTION;
    }

    @Override
    public JSONObject getOriginalTemplate() throws Exception {
        JSONObject json = new JSONObject();
        json.put("metric", metric);
        json.put("resource", resource);
        json.put("val", val);

        return json;
    }
}

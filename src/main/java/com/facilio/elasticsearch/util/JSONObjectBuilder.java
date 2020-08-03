package com.facilio.elasticsearch.util;

import org.json.simple.JSONObject;

public class JSONObjectBuilder {
    private JSONObject jsonObject = new JSONObject();

    public JSONObjectBuilder put(String key, Object value) {
        jsonObject.put(key, value);
        return this;
    }

    public JSONObject build() {
        return jsonObject;
    }
}

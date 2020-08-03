package com.facilio.elasticsearch.util;


import org.json.simple.JSONArray;

import java.util.List;

public class JSONArrayBuilder {
    private JSONArray array = new JSONArray();

    public JSONArrayBuilder add(Object value) {
        array.add(value);
        return this;
    }

    public JSONArrayBuilder add(List<Object> values) {
        array.addAll(values);
        return this;
    }

    public JSONArray build() {
        return array;
    }
}

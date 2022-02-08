package com.facilio.weekends;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WeekendContext {
    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private String name;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    private JSONObject value;
    public String getValue() {
        return (value != null) ? value.toJSONString() : null;
    }
    public JSONObject getValueJSON() {
        return value;
    }
    public void setValue(JSONObject value) {
        this.value = value;
    }
    public void setValue(String data) throws ParseException{
        if (StringUtils.isNotEmpty(data)) {
            this.value = (JSONObject) new JSONParser().parse(data);
        }
    }

}

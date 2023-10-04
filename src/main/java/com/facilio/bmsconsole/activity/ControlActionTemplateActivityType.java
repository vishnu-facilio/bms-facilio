package com.facilio.bmsconsole.activity;

import com.facilio.activity.ActivityType;
import org.json.simple.JSONObject;

public enum ControlActionTemplateActivityType implements ActivityType {
    STATUS_UPDATE(127) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return "Changed Status To "+json.get("values");
        }
    };

    private ControlActionTemplateActivityType(int value) {
        // TODO Auto-generated constructor stub
        this.value = value;
    }
    private int value = -1;
    @Override
    public int getValue() {
        // TODO Auto-generated method stub
        return value;
    }

    @Override
    public abstract String constructMessage(JSONObject json);
}


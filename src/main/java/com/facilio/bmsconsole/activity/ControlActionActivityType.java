package com.facilio.bmsconsole.activity;

import com.facilio.activity.ActivityType;
import com.facilio.bmsconsole.actions.FacilioAction;
import org.json.simple.JSONObject;

public enum ControlActionActivityType implements ActivityType {
    STATUS_UPDATE(126) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return "Changed Status To "+json.get("values");
        }
    };

    private ControlActionActivityType(int value) {
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

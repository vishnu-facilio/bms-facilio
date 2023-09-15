package com.facilio.bmsconsole.activity;

import com.facilio.activity.ActivityType;
import org.json.simple.JSONObject;

public enum CommandActivityType implements ActivityType {

    STATUS_UPDATE(125) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return "Status Changed To "+json.get("values");
        }
    };

    private CommandActivityType(int value) {
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

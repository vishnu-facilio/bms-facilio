package com.facilio.bmsconsole.activity;

import com.facilio.activity.ActivityType;
import org.json.simple.JSONObject;

public enum CalendarActivityType implements ActivityType {
    ASSOCIATE_EVENT(123) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return "Event Associated "+json.get("values");
        }
    },
    REMOVED_EVENT(124){
        @Override
        public String constructMessage(JSONObject json){
            // TODO Auto-generated method stub
            return "Event Removed "+json.get("values");
        }
    };
    private CalendarActivityType(int value) {
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

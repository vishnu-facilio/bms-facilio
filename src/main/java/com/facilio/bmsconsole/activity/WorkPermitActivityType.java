package com.facilio.bmsconsole.activity;

import com.facilio.activity.ActivityType;
import org.json.simple.JSONObject;

public enum WorkPermitActivityType implements ActivityType {
    PREREQUISITES_VERIFIED(77) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " reviewed Prerequisites";
        }
    },
    POST_WORK_VERIFIED(78) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " updated Post-Work checklist";
        }
    };

    WorkPermitActivityType(int value) {
        // TODO Auto-generated constructor stub
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    private int value = -1;

    @Override
    public abstract String constructMessage(JSONObject json);
}

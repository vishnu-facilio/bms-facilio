package com.facilio.bmsconsole.activity;

import org.json.simple.JSONObject;

import com.facilio.activity.ActivityType;

public enum EmailConversationThreadingActivityType implements ActivityType {


    ADDED_PUBLIC_NOTE(79) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " added a public note ";
        }
    },
    ADDED_PRIVATE_NOTE(80) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " added a private note ";
        }
    },
    REPLIED(81) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " replied ";
        }
    },
    ;


	EmailConversationThreadingActivityType(int value) {
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
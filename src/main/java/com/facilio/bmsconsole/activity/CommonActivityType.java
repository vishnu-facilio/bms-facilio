package com.facilio.bmsconsole.activity;

import com.facilio.activity.ActivityType;
import org.json.simple.JSONObject;

public enum CommonActivityType implements ActivityType {


    ADD_NOTES(65) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " added a Comment ";
        }
    },
    ADD_ATTACHMENT(66) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " added the Attachment ";
        }
    },
    UPDATE_STATUS(69) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " updated ";
        }
    },
    APPROVAL(72) {
        @Override
        public String constructMessage(JSONObject json) {
            return "Approval activity";
        }
    },
    APPROVAL_ENTRY(73) {
        @Override
        public String constructMessage(JSONObject json) {
            return "Approval entry";
        }
    },
    ADD_RECORD(74) {
        @Override
        public String constructMessage(JSONObject json) {
            return "added Record";
        }
    },
    UPDATE_RECORD(75) {
        @Override
        public String constructMessage(JSONObject json) {
            return "updated Record";
        }
    },
    EMAIL_RECORD(76) {
        @Override
        public String constructMessage(JSONObject json) {
            return "emailed.";
        }
    }
    ;


    CommonActivityType(int value) {
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

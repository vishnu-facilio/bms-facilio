package com.facilio.activity;

import org.json.simple.JSONObject;


public enum AlarmActivityType implements ActivityType {

    ALARM_OCCURRENCE_CREATED(50) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return "Alarm occurrence created  ";
        }
    },
    CREATE_WORKORDER(51) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return "Work Order created ";
        }
    },
    SEVERITY_CHANGE ( 52) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return "Alarm severity changed ";
        }

    },
    ACKNOWLEDGE_ALARM ( 53) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return "Alarm acknowledged ";
        }

    },
    ADD_COMMENT(54) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " added a Comment ";
        }
    },
    CLOSE_RELATED_WO(55) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " closed associated work order  ";
        }
    },
    CLEAR_ALARM (56) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return "Alarm cleared";
        }
    },
    AUTO_CLEARED (57) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return "Alarm was auto cleared";
        }
    },
    ALARM_OCCURRENCE_UPDATED (58) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return "Alarm occurrence was updated";
        }
    }
    ;


    private AlarmActivityType(int value) {
        // TODO Auto-generated constructor stub
        this.value = value;
    }

    private int value = -1;

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public abstract String constructMessage(JSONObject json);
}

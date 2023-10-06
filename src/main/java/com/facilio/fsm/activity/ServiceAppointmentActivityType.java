package com.facilio.fsm.activity;

import com.facilio.activity.ActivityType;
import org.json.simple.JSONObject;

public enum ServiceAppointmentActivityType implements ActivityType {
    DISPATCH(142){
        @Override
        public String constructMessage(JSONObject json) {
            String fieldAgent = (String) json.get("fieldAgent");
            String doneBy = (String) json.get("doneBy");
            StringBuilder builder = new StringBuilder();
            builder.append(doneBy).append(" dispatched the appointment to ").append(fieldAgent);
            return builder.toString();
        }
    },
    RESCHEDULED(143){
        @Override
        public String constructMessage(JSONObject json) {
            String doneBy = (String) json.get("doneBy");
            StringBuilder builder = new StringBuilder();
            builder.append(doneBy).append(" rescheduled the appointment");
            return builder.toString();
        }
    },
    START_WORK(144){
        @Override
        public String constructMessage(JSONObject json) {
            String doneBy = (String) json.get("doneBy");
            StringBuilder builder = new StringBuilder();
            builder.append(doneBy).append(" started the appointment");
            return builder.toString();
        }
    },
    START_TRIP(145){
        @Override
        public String constructMessage(JSONObject json) {
            String trip = (String) json.get("trip");
            String doneBy = (String) json.get("doneBy");
            StringBuilder builder = new StringBuilder();
            builder.append(doneBy).append(" started the trip ");
            builder.append(trip);
            return builder.toString();
        }
    },
    END_TRIP(146){
        @Override
        public String constructMessage(JSONObject json) {
            String trip = (String) json.get("trip");
            String doneBy = (String) json.get("doneBy");
            StringBuilder builder = new StringBuilder();
            builder.append(doneBy).append(" ended the trip ");
            builder.append(trip);
            return builder.toString();
        }
    },
    COMPLETE_WORK(147){
        @Override
        public String constructMessage(JSONObject json) {
            String doneBy = (String) json.get("doneBy");
            StringBuilder builder = new StringBuilder();
            builder.append(doneBy).append(" completed the appointment");
            return builder.toString();
        }
    },
    CANCELLED(148){
        @Override
        public String constructMessage(JSONObject json) {
            String doneBy = (String) json.get("doneBy");
            StringBuilder builder = new StringBuilder();
            builder.append(doneBy).append(" cancelled the appointment");
            return builder.toString();
        }
    },
    TIME_SHEET_ADDED(149){
        @Override
        public String constructMessage(JSONObject json) {
            String timeSheet = (String) json.get("timeSheet");
            String doneBy = (String) json.get("doneBy");
            StringBuilder builder = new StringBuilder();
            builder.append(doneBy).append(" started the time sheet ");
            builder.append(timeSheet);
            return builder.toString();
        }
    },
    TIME_SHEET_DELETED(150){
        @Override
        public String constructMessage(JSONObject json) {
            String timeSheet = (String) json.get("timeSheet");
            String doneBy = (String) json.get("doneBy");
            StringBuilder builder = new StringBuilder();
            builder.append(doneBy).append(" deleted the time sheet ");
            builder.append(timeSheet);
            return builder.toString();
        }
    },
    TRIP_DELETED(151){
        @Override
        public String constructMessage(JSONObject json) {
            String trip = (String) json.get("trip");
            String doneBy = (String) json.get("doneBy");
            StringBuilder builder = new StringBuilder();
            builder.append(doneBy).append(" deleted the trip ");
            builder.append(trip);
            return builder.toString();
        }
    },
    ;
    ServiceAppointmentActivityType(int value) {
        this.value = value;
    }

    private int value;

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public abstract String constructMessage(JSONObject json);
}

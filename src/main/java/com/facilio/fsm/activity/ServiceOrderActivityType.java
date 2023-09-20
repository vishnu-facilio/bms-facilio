package com.facilio.fsm.activity;

import com.facilio.activity.ActivityType;
import org.json.simple.JSONObject;

public enum ServiceOrderActivityType implements ActivityType {

    ADDED_TASK(126) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            Long taskCount = (Long) json.get("taskCount");
            StringBuilder builder = new StringBuilder();
            builder.append(" added ")
                    .append(taskCount);
            if(taskCount == 1){
                return builder.append(" task").toString();
            }
            return builder.append(" tasks").toString();
        }
    },
    DELETED_TASK(127) {
        @Override
        public String constructMessage(JSONObject json) {
            Long taskCount = (Long) json.get("taskCount");
            StringBuilder builder = new StringBuilder();
            builder.append(" deleted ")
                    .append(taskCount);
            if(taskCount == 1){
                return builder.append(" task").toString();
            }
            return builder.append(" tasks").toString();
        }
    },
    ADDED_SERVICE_APPOINTMENT(128) {
        @Override
        public String constructMessage(JSONObject json) {
            String serviceAppointmentName = (String) json.get("serviceAppointmentName");
            StringBuilder builder = new StringBuilder();
            builder.append(" added Service Appointment - ")
                    .append(serviceAppointmentName);
            return builder.toString();
        }
    },
    DELETED_SERVICE_APPOINTMENT(129) {
        @Override
        public String constructMessage(JSONObject json) {
            String serviceAppointmentName = (String) json.get("serviceAppointmentName");
            StringBuilder builder = new StringBuilder();
            builder.append(" deleted Service Appointment - ")
                    .append(serviceAppointmentName);
            return builder.toString();
        }
    },
    ADDED_QUOTE(130) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " added a quote";
        }
    },
    DELETED_QUOTE(131) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " deleted a quote";
        }
    },
    ADDED_INVOICE(132) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " added an Invoice ";
        }
    },
    DELETED_INVOICE(133) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " deleted an Invoice ";
        }
    },
    ADDED_INVENTORY_REQUEST(134) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " added an Inventory Request ";
        }
    },
    DELETED_INVENTORY_REQUEST(135) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " deleted an Inventory Request ";
        }
    },
    COMPLETED_SERVICE_ORDER(136) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " Completed the Service Order ";
        }
    },
    CLOSED_SERVICE_ORDER(137) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " Closed the Service Order ";
        }
    },
    CANCELLED_SERVICE_ORDER(138) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " Cancelled the Service Order ";
        }
    },
    ADD(139){
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " Added the record ";
        }
    };


    ServiceOrderActivityType(int value) {
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

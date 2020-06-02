package com.facilio.bmsconsole.activity;

import com.facilio.activity.ActivityType;
import com.facilio.constants.FacilioConstants;
import org.json.simple.JSONObject;

public enum QuotationActivityType implements ActivityType {
    ADD(63) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " Created quotation  for " + json.get(FacilioConstants.ContextNames.TOTAL_COST);
        }
    },
    UPDATE(64) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            if (json.containsKey(FacilioConstants.ContextNames.TOTAL_COST)) {
                return " Updated Quotation. Amount changed to " + json.get(FacilioConstants.ContextNames.TOTAL_COST);
            } else {
                return " Updated Quotation.";
            }
        }
    },
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
    };


    QuotationActivityType(int value) {
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

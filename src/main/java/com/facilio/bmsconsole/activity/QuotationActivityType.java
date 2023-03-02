package com.facilio.bmsconsole.activity;

import com.facilio.activity.ActivityType;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.constants.FacilioConstants;
import org.json.simple.JSONObject;


public enum QuotationActivityType implements ActivityType {
    ADD(63) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            if (json.containsKey(FacilioConstants.ContextNames.TOTAL_COST) && json.get(FacilioConstants.ContextNames.TOTAL_COST)!= null) {
                return " Created quotation for " + QuotationAPI.formatDecimal(Math.round((Double) json.get(FacilioConstants.ContextNames.TOTAL_COST) * 100.0) / 100.0);
            } else {
                return " Created quotation.";
            }
        }
    },
    UPDATE(64) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            if (json.containsKey(FacilioConstants.ContextNames.TOTAL_COST ) && json.get(FacilioConstants.ContextNames.TOTAL_COST)!= null) {
                return " Updated Quotation. Amount changed to " +  QuotationAPI.formatDecimal(Math.round((Double)json.get(FacilioConstants.ContextNames.TOTAL_COST)*100.0)/100.0);
            } else {
                return " Updated Quotation.";
            }
        }
    },
    EMAIL_QUOTATION(67) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " emailed Quotation to  " + json.get("to");
        }
    },
    REVISE_QUOTATION(68) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " revised quotation.";
        }
    },
    ASSOCIATE_TERMS(70) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " Associated Term " + json.get(FacilioConstants.ContextNames.TERMS_NAME);
        }
    },
    DISASSOCIATE_TERMS(71) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " Disassociated Term " + json.get(FacilioConstants.ContextNames.TERMS_NAME);
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

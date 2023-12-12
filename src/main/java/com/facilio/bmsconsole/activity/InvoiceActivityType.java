package com.facilio.bmsconsole.activity;

import com.facilio.activity.ActivityType;
import com.facilio.bmsconsoleV3.util.InvoiceAPI;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.constants.FacilioConstants;
import org.json.simple.JSONObject;


public enum InvoiceActivityType implements ActivityType {
    ADD(155) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            if (json.containsKey(FacilioConstants.ContextNames.TOTAL_COST) && json.get(FacilioConstants.ContextNames.TOTAL_COST)!= null) {
                return " Created invoice for " + QuotationAPI.formatDecimal(Math.round((Double) json.get(FacilioConstants.ContextNames.TOTAL_COST) * 100.0) / 100.0);
            } else {
                return " Created invoice";
            }
        }
    },
    UPDATE(156) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            if (json.containsKey(FacilioConstants.ContextNames.TOTAL_COST ) && json.get(FacilioConstants.ContextNames.TOTAL_COST)!= null) {
                return " Updated Invoice Amount changed to " +  InvoiceAPI.formatDecimal(Math.round((Double)json.get(FacilioConstants.ContextNames.TOTAL_COST)*100.0)/100.0);
            } else {
                return " Updated Invoice";
            }
        }
    },
    EMAIL_INVOICE(157) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " Emailed Invoice to  " + json.get("to");
        }
    },
    REVISE_INVOICE(158) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " Revised Invoice";
        }
    },
    ASSOCIATE_TERMS(159) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " Associated Term " + json.get(FacilioConstants.ContextNames.TERMS_NAME);
        }
    },
    DISASSOCIATE_TERMS(160) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " Disassociated Term " + json.get(FacilioConstants.ContextNames.TERMS_NAME);
        }
    },
    CLONE_INVOICE(161) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return " Cloned Invoice";
        }
    };




    InvoiceActivityType(int value) {
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

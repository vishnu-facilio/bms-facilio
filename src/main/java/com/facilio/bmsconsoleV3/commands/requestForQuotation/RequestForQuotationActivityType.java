package com.facilio.bmsconsoleV3.commands.requestForQuotation;

import com.facilio.activity.ActivityType;
import com.facilio.constants.FacilioConstants;
import org.json.simple.JSONObject;

public enum RequestForQuotationActivityType implements ActivityType {
    QUOTE_AWARDED(114) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return  "awarded quotes";
        }
    },
    PO_CREATED(115) {
        @Override
        public String constructMessage(JSONObject json) {
            // TODO Auto-generated method stub
            return  "created Purchase Order(s)";
        }
    };
    RequestForQuotationActivityType(int value) {
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

package com.facilio.bmsconsole.activity;

import com.facilio.activity.ActivityType;
import com.facilio.constants.FacilioConstants;
import org.json.simple.JSONObject;

public enum MultiCurrencyActivityType implements ActivityType {
    CHANGED_TO(122){
        @Override
        public String constructMessage(JSONObject json) {
            String oldCurrencyCode = (String) json.get(FacilioConstants.ContextNames.OLD_CURRENCY_CODE);
            String newCurrencyCode = (String) json.get(FacilioConstants.ContextNames.NEW_CURRENCY_CODE);
            return "changed Currency Code from "+ oldCurrencyCode+" to "+ newCurrencyCode;
        }
    };
    private int value = -1;
    MultiCurrencyActivityType(int value) {
        this.value = value;
    }
    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

   @Override
   public abstract String constructMessage(JSONObject json);
}

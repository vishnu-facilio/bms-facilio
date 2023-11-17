package com.facilio.bmsconsole.activity;

import com.facilio.activity.ActivityType;
import com.facilio.constants.FacilioConstants;
import org.json.simple.JSONObject;

public enum ImportActivityType implements ActivityType {
    ADD_RECORD(123){
        @Override
        public String constructMessage(JSONObject json) {
            Long importId = (Long) json.get(FacilioConstants.ContextNames.IMPORT_ID);
            return "added the record via import #"+importId;
        }
    },
    UPDATE_RECORD(124){
        @Override
        public String constructMessage(JSONObject json) {
            Long importId = (Long) json.get(FacilioConstants.ContextNames.IMPORT_ID);
            return "updated the record via import #"+ importId;
        }
    };
    private int value = -1;

    ImportActivityType(int i) {
        this.value = i;
    }
    @Override
    public int getValue() {
        return value;
    }

    @Override
    public String constructMessage(JSONObject json) {
        return null;
    }
}

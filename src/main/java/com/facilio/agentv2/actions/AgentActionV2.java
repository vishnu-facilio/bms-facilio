package com.facilio.agentv2.actions;

import com.facilio.bmsconsole.actions.FacilioAction;
import org.json.simple.JSONObject;

public class AgentActionV2 extends FacilioAction
{
    public boolean notNull(Object object) {
        return object != null;
    }

    boolean checkValue(Long value){
        return (value != null) && (value >  0);
    }

    public boolean containsValueCheck(String key, JSONObject jsonObject){
        if(notNull(key)&& notNull(jsonObject) && jsonObject.containsKey(key) && ( jsonObject.get(key) != null) ){
            return true;
        }
        return false;
    }

}

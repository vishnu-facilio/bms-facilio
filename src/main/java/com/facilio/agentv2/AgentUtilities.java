package com.facilio.agentv2;

import java.util.Map;

public class AgentUtilities
{
    public static boolean notNull(Object object) {
        return object != null;
    }

    public static boolean checkValue(Long value){
        return (value != null) && (value >  0);
    }

    public static boolean containsValueCheck(String key, Map<String,Object> map){
        if(notNull(key) && notNull(map) && map.containsKey(key) && ( map.get(key) != null) ){
            return true;
        }
        return false;
    }
}

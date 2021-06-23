package com.facilio.agentv2.commands;

import com.facilio.command.FacilioCommand;

import java.util.Map;

public abstract class AgentV2Command extends FacilioCommand {

    public boolean containsCheck(String key, Map map){
        if( (key != null) && ( ! key.isEmpty()) && ( map != null ) && ( ! map.isEmpty() ) && (map.containsKey(key)) && (map.get(key) != null) ){
            return true;
        }
        return false;
    }

    public boolean checkNumber(Number number){
        return (number.intValue() > 0);
    }
}

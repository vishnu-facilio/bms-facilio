package com.facilio.flowLog.moduleFlowLog.enums;

import com.facilio.modules.FacilioIntEnum;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
public enum FlowLogType implements FacilioIntEnum {
    SYSTEM,
    CUSTOM;
    private static Map<Integer,FlowLogType> map = Collections.unmodifiableMap(initMap());
    private static Map<Integer,FlowLogType> initMap(){
        Map<Integer,FlowLogType> map = new HashMap<>();
        for(FlowLogType flowLogType:FlowLogType.values()){
            map.put(flowLogType.getIndex(), flowLogType);
        }
        return map;
    }
    public static FlowLogType valueOf(Integer logType){
        return map.get(logType);
    }
}

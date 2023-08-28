package com.facilio.flowLog.moduleFlowLog.enums;

import com.facilio.modules.FacilioIntEnum;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
public enum FlowStatus implements FacilioIntEnum {
    SUCCESS,
    FAILURE;
    private static Map<Integer,FlowStatus> map = Collections.unmodifiableMap(initMap());
    private static Map<Integer,FlowStatus> initMap(){
        Map<Integer,FlowStatus> map = new HashMap<>();
        for(FlowStatus flowStatus:FlowStatus.values()){
            map.put(flowStatus.getIndex(), flowStatus);
        }
        return map;
    }
    public static FlowStatus valueOf(Integer status){
        return map.get(status);
    }
}

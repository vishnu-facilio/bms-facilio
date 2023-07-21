package com.facilio.fsm.util;

import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.TimeOffContext;
import com.facilio.fsm.context.TimeOffTypeContext;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class TimeOffUtil {
    public static Map<Long,String> getTimeOffColorMap() throws Exception {
        Map<Long,String> typeMap = new HashMap<>();
        SelectRecordsBuilder<TimeOffTypeContext> builder = new SelectRecordsBuilder<TimeOffTypeContext>()
                .select(Constants.getModBean().getAllFields(FacilioConstants.TimeOff.TIME_OFF_TYPE))
                .module(Constants.getModBean().getModule(FacilioConstants.TimeOff.TIME_OFF_TYPE))
                .beanClass(TimeOffTypeContext.class);
        List<TimeOffTypeContext> timeOffType = builder.get();
        if(CollectionUtils.isNotEmpty(timeOffType)){
            for(TimeOffTypeContext type : timeOffType){
                typeMap.put(type.getId(),type.getColor());
            }
        }
       return typeMap;
    }
}

package com.facilio.bmsconsoleV3.context.calendar;


import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@Setter
public class V3EventTimeSlotContext extends V3Context {
    private V3EventContext event;
    private Integer startMin;
    private Integer endMin;
    private Long startMinMilliSecond;
    private Long endMinMilliSecond;
//    public Integer getStartMin(){
//        return convertMillisecondsToMinute(getStartMinMilliSecond());
//    }
//    public Integer getEndMin(){
//        return convertMillisecondsToMinute(getEndMinMilliSecond());
//    }
    public Integer convertMillisecondsToMinute(Long milliSecond){
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(milliSecond), ZoneId.systemDefault());
        return dateTime.getHour()*60+dateTime.getMinute();
    }
}

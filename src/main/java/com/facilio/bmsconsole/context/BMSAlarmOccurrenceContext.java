package com.facilio.bmsconsole.context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BMSAlarmOccurrenceContext extends AlarmOccurrenceContext{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String condition;

    private String source;

    private long controller;

    private String alarmClass;

    @Override
    public Type getTypeEnum() {
        return Type.BMS;
    }
}
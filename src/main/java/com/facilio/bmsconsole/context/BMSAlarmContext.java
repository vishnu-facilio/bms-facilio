package com.facilio.bmsconsole.context;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BMSAlarmContext extends BaseAlarmContext {

    private static final long serialVersionUID = 1L;
    private String condition;
    private String source;
    private long controller;
    private String alarmClass;
}
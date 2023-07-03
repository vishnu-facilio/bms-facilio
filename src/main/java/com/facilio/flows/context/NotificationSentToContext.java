package com.facilio.flows.context;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter@Setter
public class NotificationSentToContext implements Serializable {
    private long userId=-1l;
    private String placeHolder;
    private long notificationBlockId=-1l;
}

package com.facilio.backgroundactivity.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BackgroundActivity extends V3Context {
    private String name;
    private Long recordId;
    private String recordType;
    private Long startTime;
    private Long completedTime;
    private Long initiatedBy;
    private String systemStatus;
    private Integer percentage;
    private String message;
    private Long parentActivity;
    private String statusColorCode;

}
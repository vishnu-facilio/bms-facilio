package com.facilio.backgroundactivity.context;

import com.facilio.backgroundactivity.util.BackgroundActivityInterface;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class BackgroundActivityLiveMessageContext implements Serializable {
    private String message;
    private long activityId;
    private Long totalRecords;
    private Long processedRecords;
    private Integer percentage;
    private boolean refresh;
    private BackgroundActivity activity;
    private List<BackgroundActivity> activityList;

}

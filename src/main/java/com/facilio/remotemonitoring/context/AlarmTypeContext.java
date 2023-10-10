package com.facilio.remotemonitoring.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmTypeContext extends V3Context {
    private String name;
    private String description;
    private Boolean uncategorisedAlarm;
    private String linkName;

    public boolean isUncategorisedAlarm() {
        if(uncategorisedAlarm == null) {
            return false;
        }
        return uncategorisedAlarm;
    }
}
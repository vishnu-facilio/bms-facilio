package com.facilio.fsm.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServiceTaskStatusContext  extends V3Context {
    private String name;
    private String displayName;
    private String statusType;
    private Boolean recordLocked;
    private Boolean deleteLocked;
}

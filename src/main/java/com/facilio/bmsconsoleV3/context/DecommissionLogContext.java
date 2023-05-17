package com.facilio.bmsconsoleV3.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class DecommissionLogContext extends V3Context {
    private long id;
    private long orgId;
    private long resourceId;
    private Boolean decommission;
    private long commissionedTime;
    private String logValue;
    private String remarks;
}

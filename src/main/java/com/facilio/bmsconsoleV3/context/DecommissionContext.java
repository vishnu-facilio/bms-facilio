package com.facilio.bmsconsoleV3.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DecommissionContext extends V3Context {
    private Long resourceId;

    private Boolean decommission;
    private String moduleName;

    private String remarkValue;

}

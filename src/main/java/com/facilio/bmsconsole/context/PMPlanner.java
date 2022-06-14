package com.facilio.bmsconsole.context;

import com.facilio.v3.context.V3Context;
import lombok.Data;

@Data
public class PMPlanner extends V3Context {
    private long pmId;
    private String name;
}

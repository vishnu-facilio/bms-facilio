package com.facilio.remotemonitoring.context;

import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AlarmDefinitionContext extends V3Context {
    private String name;
    private String description;
    private V3ClientContext client;

}

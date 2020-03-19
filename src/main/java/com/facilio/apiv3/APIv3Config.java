package com.facilio.apiv3;

import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;

@Config
public class APIv3Config {

    @Module("workorder")
    public static V3Config workorder() {
        return new V3Config(WorkOrderContext.class);
    }

}

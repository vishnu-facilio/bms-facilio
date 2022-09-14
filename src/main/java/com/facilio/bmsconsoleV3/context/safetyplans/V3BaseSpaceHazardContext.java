package com.facilio.bmsconsoleV3.context.safetyplans;

import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.v3.context.V3Context;

public class V3BaseSpaceHazardContext extends V3Context {
    public V3BaseSpaceContext getSpace() {
        return space;
    }

    public void setSpace(V3BaseSpaceContext space) {
        this.space = space;
    }

    private V3BaseSpaceContext space;

    public V3HazardContext getHazard() {
        return hazard;
    }

    public void setHazard(V3HazardContext hazard) {
        this.hazard = hazard;
    }

    private V3HazardContext hazard;


}

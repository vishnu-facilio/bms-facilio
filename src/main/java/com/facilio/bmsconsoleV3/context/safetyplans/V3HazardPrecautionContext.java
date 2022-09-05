package com.facilio.bmsconsoleV3.context.safetyplans;

import com.facilio.v3.context.V3Context;

public class V3HazardPrecautionContext extends V3Context {
    private V3PrecautionContext precaution;
    private V3HazardContext hazard;
    public V3HazardContext getHazard() {
        return hazard;
    }
    public void setHazard(V3HazardContext hazard) {
        this.hazard = hazard;
    }
    public V3PrecautionContext getPrecaution() {
        return precaution;
    }
    public void setPrecaution(V3PrecautionContext precaution) {
        this.precaution = precaution;
    }
}

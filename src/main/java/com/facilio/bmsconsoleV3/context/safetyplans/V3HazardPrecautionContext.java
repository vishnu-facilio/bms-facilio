package com.facilio.bmsconsoleV3.context.safetyplans;

import com.facilio.bmsconsole.context.HazardContext;
import com.facilio.bmsconsole.context.PrecautionContext;
import com.facilio.v3.context.V3Context;

public class V3HazardPrecautionContext extends V3Context {
    private PrecautionContext precaution;
    private HazardContext hazard;
    public HazardContext getHazard() {
        return hazard;
    }
    public void setHazard(HazardContext hazard) {
        this.hazard = hazard;
    }
    public PrecautionContext getPrecaution() {
        return precaution;
    }
    public void setPrecaution(PrecautionContext precaution) {
        this.precaution = precaution;
    }
}

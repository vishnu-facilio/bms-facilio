package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsoleV3.context.safetyplans.V3PrecautionContext;
import com.facilio.v3.context.V3Context;

public class V3WorkorderHazardPrecautionContext extends V3Context {
    private V3WorkOrderContext workorder;

    public V3WorkorderHazardContext getWorkorderHazard() {
        return workorderHazard;
    }

    public void setWorkorderHazard(V3WorkorderHazardContext workorderHazard) {
        this.workorderHazard = workorderHazard;
    }

    private V3WorkorderHazardContext workorderHazard;
    public V3WorkOrderContext getWorkorder() {
        return workorder;
    }
    public void setWorkorder(V3WorkOrderContext workorder) {
        this.workorder = workorder;
    }

    public V3PrecautionContext getPrecaution() {
        return precaution;
    }

    public void setPrecaution(V3PrecautionContext precaution) {
        this.precaution = precaution;
    }

    private V3PrecautionContext precaution;
}

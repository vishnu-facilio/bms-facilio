package com.facilio.bmsconsoleV3.context;


import com.facilio.bmsconsoleV3.context.safetyplans.V3HazardContext;
import com.facilio.v3.context.V3Context;

public class V3WorkorderHazardContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private V3WorkOrderContext workorder;
    private V3HazardContext hazard;
    public V3HazardContext getHazard() {
        return hazard;
    }
    public void setHazard(V3HazardContext hazard) {
        this.hazard = hazard;
    }
    public V3WorkOrderContext getWorkorder() {
        return workorder;
    }
    public void setWorkorder(V3WorkOrderContext workorder) {
        this.workorder = workorder;
    }
}

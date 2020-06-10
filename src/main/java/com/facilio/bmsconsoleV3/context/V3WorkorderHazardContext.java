package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.HazardContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class V3WorkorderHazardContext extends ModuleBaseWithCustomFields {
    private static final long serialVersionUID = 1L;

    private V3WorkOrderContext workorder;
    private HazardContext hazard;
    public HazardContext getHazard() {
        return hazard;
    }
    public void setHazard(HazardContext hazard) {
        this.hazard = hazard;
    }
    public V3WorkOrderContext getWorkorder() {
        return workorder;
    }
    public void setWorkorder(V3WorkOrderContext workorder) {
        this.workorder = workorder;
    }
}

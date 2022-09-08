package com.facilio.bmsconsoleV3.context.safetyplans;


import com.facilio.v3.context.V3Context;

public class V3SafetyPlanHazardContext extends V3Context {
    private V3SafetyPlanContext safetyPlan;
    private V3HazardContext hazard;
    public V3SafetyPlanContext getSafetyPlan() {
        return safetyPlan;
    }
    public void setSafetyPlan(V3SafetyPlanContext safetyPlan) {
        this.safetyPlan = safetyPlan;
    }
    public V3HazardContext getHazard() {
        return hazard;
    }
    public void setHazard(V3HazardContext hazard) {
        this.hazard = hazard;
    }
}

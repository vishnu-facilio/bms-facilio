package com.facilio.bmsconsoleV3.context.safetyplans;

import com.facilio.v3.context.V3Context;

import java.util.List;

public class V3SafetyPlanContext extends V3Context {
    private String name;
    private String description;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    private List<Long> hazardIds;
    public List<Long> getHazardIds() {
        return hazardIds;
    }
    public void setHazardIds(List<Long> hazardIds) {
        this.hazardIds = hazardIds;
    }
}

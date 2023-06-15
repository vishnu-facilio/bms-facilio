package com.facilio.fsm.context;

import com.facilio.v3.context.V3Context;

public class ServiceSkillsContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private Double ratePerHour;

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

    public Double getRatePerHour() {
        return ratePerHour;
    }

    public void setRatePerHour(Double ratePerHour) {
        this.ratePerHour = ratePerHour;
    }
}

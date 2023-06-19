package com.facilio.fsm.context;

import com.facilio.v3.context.V3Context;

import java.util.List;

public class WorkTypeContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private Double estimatedDuration;
    private List<WorkTypeSkillsContext> skills;

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

    public Double getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(Double estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public List<WorkTypeSkillsContext> getSkills() {
        return skills;
    }

    public void setSkills(List<WorkTypeSkillsContext> skills) {
        this.skills = skills;
    }
}

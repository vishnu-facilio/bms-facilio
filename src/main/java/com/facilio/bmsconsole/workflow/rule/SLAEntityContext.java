package com.facilio.bmsconsole.workflow.rule;

import java.io.Serializable;

public class SLAEntityContext implements Serializable {

    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    private long moduleId = -1;
    public long getModuleId() {
        return moduleId;
    }
    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    private long baseFieldId = -1;
    public long getBaseFieldId() {
        return baseFieldId;
    }
    public void setBaseFieldId(long baseFieldId) {
        this.baseFieldId = baseFieldId;
    }

    private long dueFieldId = -1;
    public long getDueFieldId() {
        return dueFieldId;
    }
    public void setDueFieldId(long dueFieldId) {
        this.dueFieldId = dueFieldId;
    }

    private long compareFieldId = -1;
    public long getCompareFieldId() {
        return compareFieldId;
    }
    public void setCompareFieldId(long compareFieldId) {
        this.compareFieldId = compareFieldId;
    }
}

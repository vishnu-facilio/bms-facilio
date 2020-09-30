package com.facilio.bmsconsoleV3.context.facilitybooking;

import com.facilio.v3.context.V3Context;

public class AmenitiesContext extends V3Context {

    private String name;
    private Integer category;
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

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }
}

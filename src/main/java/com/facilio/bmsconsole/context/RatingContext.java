package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class RatingContext extends ModuleBaseWithCustomFields {

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

    private int ratingValue = -1;
    public int getRatingValue() {
        return ratingValue;
    }
    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }

    private ModuleBaseWithCustomFields parent;
    public ModuleBaseWithCustomFields getParent() {
        return parent;
    }
    public void setParent(ModuleBaseWithCustomFields parent) {
        this.parent = parent;
    }
}

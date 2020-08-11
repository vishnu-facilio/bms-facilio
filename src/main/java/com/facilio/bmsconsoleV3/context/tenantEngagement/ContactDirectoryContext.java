package com.facilio.bmsconsoleV3.context.tenantEngagement;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.v3.context.V3Context;

public class ContactDirectoryContext extends V3Context {

    private V3PeopleContext people;
    private String description;
    private Integer category;

    public V3PeopleContext getPeople() {
        return people;
    }

    public void setPeople(V3PeopleContext people) {
        this.people = people;
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

package com.facilio.bmsconsoleV3.context.tenantEngagement;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class ContactDirectoryContext extends V3Context {

    private V3PeopleContext people;
    private String description;
    private Integer category;
    private List<ContactDirectorySharingContext> contactdirectorysharing;


    public List<ContactDirectorySharingContext> getContactdirectorysharing() {
        return contactdirectorysharing;
    }

    public void setContactdirectorysharing(List<ContactDirectorySharingContext> contactdirectorysharing) {
        this.contactdirectorysharing = contactdirectorysharing;
    }

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

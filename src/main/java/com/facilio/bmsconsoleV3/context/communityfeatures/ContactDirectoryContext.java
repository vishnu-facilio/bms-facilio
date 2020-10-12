package com.facilio.bmsconsoleV3.context.communityfeatures;

import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class ContactDirectoryContext extends V3Context {

    private V3PeopleContext people;
    private String description;
    private Integer category;
    private List<CommunitySharingInfoContext> contactdirectorysharing;


    public List<CommunitySharingInfoContext> getContactdirectorysharing() {
        return contactdirectorysharing;
    }

    public void setContactdirectorysharing(List<CommunitySharingInfoContext> contactdirectorysharing) {
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

    private AudienceContext audience;

    public AudienceContext getAudience() {
        return audience;
    }

    public void setAudience(AudienceContext audience) {
        this.audience = audience;
    }

    private String contactName;
    private String contactEmail;
    private String contactPhone;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
}

package com.facilio.bmsconsoleV3.context.communityfeatures;

import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactDirectoryContext extends V3Context {

    private V3PeopleContext people;
    private String description;
    private Category category;
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
        return category == null ? null : category.getIndex();
    }

    public void setCategory(Integer categoryIndex) {
        this.category = categoryIndex == null ? null : Category.categoryMap.get(categoryIndex);
    }

    public String getCategoryEnum(){
        return category == null ? null : category.getStringVal();
    }

    private List<AudienceContext> audience;

    public List<AudienceContext> getAudience() {
        return audience;
    }

    public void setAudience(List<AudienceContext> audience) {
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

    public static enum Category implements FacilioIntEnum {
        TECHNICIAN(1,"Technician"),
        BUILDING_SUPERVISOR(2,"Building Supervisor"),
        SECURITY(3,"Security"),
        TENANT_REPRESENTATIVE(4,"Tenant Representative"),
        OTHERS(5,"Others"),
        ;
        private int intVal;
        private String strVal;

        private Category(int intVal, String strVal) {
            this.intVal = intVal;
            this.strVal = strVal;
        }

        public int getIntVal() {
            return intVal;
        }
        public String getStringVal() {
            return strVal;
        }

        private static final Map<Integer, Category> categoryMap = Collections.unmodifiableMap(initCategoryMap());

        private static Map<Integer, Category> initCategoryMap() {
            Map<Integer, Category> categoryMap = new HashMap<>();

            for(Category category : values()) {
                categoryMap.put(category.getIndex(), category);
            }
            return categoryMap;
        }
        public Map<Integer, Category> getAllcategory() { return categoryMap; }

    }
}

package com.facilio.bmsconsoleV3.context.budget;

import com.facilio.bmsconsoleV3.enums.Group;
import com.facilio.v3.context.V3Context;

public class AccountTypeContext extends V3Context {

    private String name;
    private Group group;


    public void setGroup(Integer group) {
        if (group != null) {
            this.group = Group.valueOf(group);
        }
    }

    public Group getGroupEnum() {
        return group;
    }
    public Integer getGroup() {
        if (group != null) {
            return group.getIndex();
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

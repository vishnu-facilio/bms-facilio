package com.facilio.bmsconsoleV3.context.budget;

import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;

public class AccountTypeContext extends V3Context {

    private String name;
    private Group group;


    public enum Group implements FacilioEnum {
        ASSET("Asset"),
        LIABILITY("Liability"),
        EQUITY("Equity"),
        INCOME("Income"),
        EXPENSE("Expense");
        ;
        private String name;

        Group(String name) {
            this.name = name;
        }

        public static Group valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

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

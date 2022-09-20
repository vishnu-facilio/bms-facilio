package com.facilio.bmsconsoleV3.context.safetyplans;

import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import org.apache.commons.lang3.StringUtils;

public class V3HazardContext extends V3Context {
    private String name;
    private String description;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = StringUtils.upperCase(name);
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }


    private Type type;
    public int getType() {
        if (type != null) {
            return type.getIndex();
        }
        return -1;
    }
    public void setType(int type) {
        this.type = Type.valueOf(type);
    }
    public Type getTypeEnum() {
        return type;
    }
    public static enum Type implements FacilioIntEnum {
        ELECTRICAL, HEALTH, MECHANICAL, PROPERTY;

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name();
        }

        public static Type valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
}

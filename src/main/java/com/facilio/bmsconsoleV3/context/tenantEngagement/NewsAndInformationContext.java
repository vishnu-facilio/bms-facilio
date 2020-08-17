package com.facilio.bmsconsoleV3.context.tenantEngagement;

import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;

public class NewsAndInformationContext extends V3Context {

    private static final long serialVersionUID = 1L;

    private String title;
    private String description;
    private Type type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public enum Type implements FacilioEnum {
        CONVERSATION("Conversation")
        ;

        private String name;

        Type(String name) {
            this.name = name;
        }

        public static Type valueOf(int value) {
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


    public void setType(Integer type) {
        if (type != null) {
            this.type = Type.valueOf(type);
        }
    }

    public String getTypeEnum() {
        if (type != null) {
            return type.getValue();
        }
        return null;
    }
    public Integer getType() {
        if (type != null) {
            return type.getIndex();
        }
        return null;
    }


}

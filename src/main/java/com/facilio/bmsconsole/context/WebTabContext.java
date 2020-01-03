package com.facilio.bmsconsole.context;

import com.facilio.agentv2.point.PointEnum;
import com.facilio.modules.FacilioEnum;
import org.json.simple.JSONObject;

import java.io.Serializable;

public class WebTabContext implements Serializable {

    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private WebTabGroupContext group;
    public WebTabGroupContext getGroup() {
        return group;
    }
    public void setGroup(WebTabGroupContext group) {
        this.group = group;
    }

    private String route;
    public String getRoute() {
        return route;
    }
    public void setRoute(String route) {
        this.route = route;
    }

    private Type type;
    public int getType() {
        if (type != null) {
            return type.getIndex();
        }
        return -1;
    }
    public void setType(int typeInt) {
        this.type = Type.valueOf(typeInt);
    }
    public Type getTypeEnum() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

    private int order;
    public int getOrder() {
        return order;
    }
    public void setOrder(int order) {
        this.order = order;
    }

    private JSONObject config;
    public JSONObject getConfig() {
        return config;
    }
    public void setConfig(JSONObject config) {
        this.config = config;
    }

    public void validateConfig() {
        if (config == null) {
            throw new IllegalArgumentException("Invalid config");
        }

        switch (type) {
            case MODULE:
                break;

            default:
                throw new IllegalArgumentException("Invalid type in validating");
        }
    }

    public enum Type implements FacilioEnum {
        MODULE ("Module"),
        DASHBOARD ("Dashboard"),
        REPORT ("Report"),
        CALENDAR ("Calendar")
        ;

        private String name;
        public String getName() {
            return name;
        }

        Type(String name) {
            this.name = name;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        public static Type valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public String getValue() {
            return getName();
        }
    }
}

package com.facilio.bmsconsole.automation.context;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FieldType;
import com.facilio.v3.context.V3Context;

public class GlobalVariableContext extends V3Context {

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private String linkName;
    public String getLinkName() {
        return linkName;
    }
    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    private String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    private Type type;
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

    private String valueString;
    public String getValueString() {
        return valueString;
    }
    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    private Object value;
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }

    private long createdTime = -1l;
    public long getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    private long modifiedTime = -1l;
    public long getModifiedTime() {
        return modifiedTime;
    }
    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public enum Type implements FacilioIntEnum {
        NUMBER,
        STRING,
        DECIMAL,
        BOOLEAN,
        DATE,
        DATE_TIME;
    }
}

package com.facilio.bmsconsole.automation.context;

import com.facilio.accounts.dto.User;
import com.facilio.modules.FacilioIntEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.struts2.json.annotations.JSON;

import java.io.Serializable;

public class GlobalVariableContext implements Serializable {

    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private long orgId = -1;
    public long getOrgId() {
        return orgId;
    }
    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

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
    public int getType() {
        if (type == null) {
            return -1;
        }
        return type.getIndex();
    }
    public void setType(int type) {
        this.type = Type.valueOf(type);
    }
    public Type getTypeEnum() {
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

    @JsonIgnore
    private Object value;
    @JSON(serialize = false)
    public Object getValue() {
        if (value == null) {
            switch (type) {
                case NUMBER:
                    try {
                        value = Integer.parseInt(valueString);
                    } catch (NumberFormatException ex) {}
                    break;

                case DECIMAL:
                    try {
                        value = Double.parseDouble(valueString);
                    } catch (NumberFormatException ex) {}
                    break;

                case STRING:
                    value = valueString;
                    break;

                case BOOLEAN:
                    value = Boolean.parseBoolean(valueString);
                    break;

                case DATE:
                case DATE_TIME:
                    try {
                        value = Long.parseLong(valueString);
                    } catch (NumberFormatException ex) {}
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported variable type");
            }
        }
        return value;
    }
    @JSON(deserialize = false)
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

    private long createdBy = -1;
    public long getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    private User createdByUser;
    public User getCreatedByUser() {
        return createdByUser;
    }
    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }

    private long modifiedTime = -1l;
    public long getModifiedTime() {
        return modifiedTime;
    }
    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    private long modifiedBy = -1;
    public long getModifiedBy() {
        return modifiedBy;
    }
    public void setModifiedBy(long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    private User modifiedByUser;
    public User getModifiedByUser() {
        return modifiedByUser;
    }
    public void setModifiedByUser(User modifiedByUser) {
        this.modifiedByUser = modifiedByUser;
    }

    public enum Type implements FacilioIntEnum {
        NUMBER,
        STRING,
        DECIMAL,
        BOOLEAN,
        DATE,
        DATE_TIME;

        public static Type valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
}

package com.facilio.bmsconsoleV3.context.labour;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsoleV3.context.location.LocationContextV3;
import com.facilio.v3.context.V3Context;

public class LabourContextV3 extends V3Context {
    private static final Long serialVersionUID = 1L;

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    private Boolean availability = false;

    public Boolean getAvailability() {
        return availability;
    }
    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public Double getCost() {
        return cost;
    }
    public void setCost(Double cost) {
        this.cost = cost;
    }
    public static Long getSerialversionuid() {
        return serialVersionUID;
    }
    private LabourUnitType unitType;

    public int getUnitType() {
        if (unitType != null) {
            return unitType.getIntValForDB();
        }
        return -1;
    }

    public LabourUnitType getUnitTypeEnum() {
        return unitType;
    }

    public void setUnitType(LabourUnitType unitType) {
        this.unitType = unitType;
    }

    public void setUnitType(int unitType) {
        this.unitType = LabourUnitType.UNIT_MAP.get(unitType);
    }

    public int getUnitTypeInt() {
        if (unitType != null) {
            return unitType.getIntValForDB();
        }
        return -1;
    }
    private User user;
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    private String email;
    private String phone;
    private double cost = 0;

    public enum LabourUnitType {
        HOURLY(1),
        DAILY(2),
        WEEKLY(3),
        MONTHLY(4),
        ;

        private int intVal;
        private LabourUnitType(int intVal) {
            this.intVal = intVal;
        }

        public int getIntValForDB() {
            return intVal;
        }

        public static final Map<Integer, LabourUnitType> UNIT_MAP = Collections.unmodifiableMap(initTypeMap());
        private static Map<Integer, LabourUnitType> initTypeMap() {
            Map<Integer, LabourUnitType> typeMap = new HashMap<>();
            for(LabourUnitType type : values()) {
                typeMap.put(type.getIntValForDB(), type);
            }
            return typeMap;
        }
    }

    private LocationContextV3 location;

    public LocationContextV3 getLocation() {
        return location;
    }

    public void setLocation(LocationContextV3 location) {
        this.location = location;
    }

    public Long getLocationId() {
        // TODO Auto-generated method stub
        if (location != null) {
            return location.getId();
        }
        return -1L;
    }
}

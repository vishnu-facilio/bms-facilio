package com.facilio.bmsconsoleV3.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.LocationContext;

public class V3BuildingContext extends V3BaseSpaceContext {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private LocationContext location;
    public LocationContext getLocation() {
        return location;
    }
    public void setLocation(LocationContext location) {
        this.location = location;
    }

    private User managedBy;
    public User getManagedBy() {
        return managedBy;
    }
    public void setManagedBy(User managedBy) {
        this.managedBy = managedBy;
    }

    private Integer noOfFloors;
    public Integer getNoOfFloors() {
        return noOfFloors;
    }
    public void setNoOfFloors(Integer noOfFloors) {
        this.noOfFloors = noOfFloors;
    }

    private Long noOfIndependentSpaces;

    public Long getNoOfIndependentSpaces() {
        return noOfIndependentSpaces;
    }

    public void setNoOfIndependentSpaces(Long noOfIndependentSpaces) {
        this.noOfIndependentSpaces = noOfIndependentSpaces;
    }

    private Double grossFloorArea;
    public Double getGrossFloorArea() {
        return grossFloorArea;
    }
    public void setGrossFloorArea(Double grossFloorArea) {
        this.grossFloorArea = grossFloorArea;
    }
}

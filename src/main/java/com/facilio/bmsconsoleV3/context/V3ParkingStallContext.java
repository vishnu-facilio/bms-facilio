package com.facilio.bmsconsoleV3.context;

import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import lombok.Setter;

public class V3ParkingStallContext  extends V3SpaceContext {
	
	private static final long serialVersionUID = 1L;
	
	private Integer parkingType;

    private Integer parkingMode;

    @Getter @Setter
    private V3EmployeeContext employee;

    public Integer getParkingType() {
        return parkingType;
    }

    public void setParkingType(Integer parkingType) {
        this.parkingType = parkingType;
    }

    public Integer getParkingMode() {
        return parkingMode;
    }

    public void setParkingMode(Integer parkingMode) {
        this.parkingMode = parkingMode;
    }
}
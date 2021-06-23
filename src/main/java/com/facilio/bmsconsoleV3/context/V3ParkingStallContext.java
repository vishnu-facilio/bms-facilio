package com.facilio.bmsconsoleV3.context;

import com.facilio.modules.FacilioIntEnum;

public class V3ParkingStallContext  extends V3SpaceContext {
	
	private static final long serialVersionUID = 1L;
	
	private Integer parkingType;
	
	public Integer getParkingType() {
        return parkingType;
    }

    public void setParkingType(Integer parkingType) {
        this.parkingType = parkingType;
    }

}
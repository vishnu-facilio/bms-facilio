package com.facilio.bmsconsoleV3.context;

import com.facilio.modules.FacilioIntEnum;

public class V3ParkingStallContext  extends V3SpaceContext {
	
	private static final long serialVersionUID = 1L;
	
	private ParkingType parkingType;
	public int getParkingType() {
		if (parkingType != null) {
			return parkingType.getIndex();
		}
		return -1;
	}
	public void setParkingType(int parkingType) {
		this.parkingType = ParkingType.valueOf(parkingType);
	}
	public ParkingType getParkingTypeEnum() {
		return parkingType;
	}
	public void setParkingType(ParkingType parkingType) {
		this.parkingType = parkingType;
	}

	public static enum ParkingType implements FacilioIntEnum {
		PARKING_STALL("Parking Stall"),
		HANDICAP ("Handicap"),
		CARPOOL("Carpool");
		
		private String name;

		ParkingType() {
	    }
		
		ParkingType(String name) {
	        this.name = name;
	    }

		@Override
		public Integer getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name();
		}

		public static ParkingType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}

}
package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;

public class BuildingContext extends BaseSpaceContext {

	/**
	 *
	 */
	
	public BuildingContext(long id) {
		setId(id);
	}
	public BuildingContext() {
		
	}
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

	private int noOfFloors = -1;
	public int getNoOfFloors() {
		return noOfFloors;
	}
	public void setNoOfFloors(int noOfFloors) {
		this.noOfFloors = noOfFloors;
	}

	private long noOfIndependentSpaces = -1;
	
	public long getNoOfIndependentSpaces() {
		return noOfIndependentSpaces;
	}

	public void setNoOfIndependentSpaces(long noOfIndependentSpaces) {
		this.noOfIndependentSpaces = noOfIndependentSpaces;
	}
	
	private double grossFloorArea = -1;
	public double getGrossFloorArea() {
		return grossFloorArea;
	}
	public void setGrossFloorArea(double grossFloorArea) {
		this.grossFloorArea = grossFloorArea;
	}
}

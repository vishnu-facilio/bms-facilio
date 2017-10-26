package com.facilio.bmsconsole.context;

public class BuildingContext extends BaseSpaceContext {
	
	private LocationContext location;
	public LocationContext getLocation() {
		return location;
	}
	public void setLocation(LocationContext location) {
		this.location = location;
	}
	
	private UserContext managedBy;
	public UserContext getManagedBy() {
		return managedBy;
	}
	public void setManagedBy(UserContext managedBy) {
		this.managedBy = managedBy;
	}
	
	private int noOfFloors = -1;
	public int getNoOfFloors() {
		return noOfFloors;
	}
	public void setNoOfFloors(int noOfFloors) {
		this.noOfFloors = noOfFloors;
	}
	
	private double grossFloorArea = -1;
	public double getGrossFloorArea() {
		return grossFloorArea;
	}
	public void setGrossFloorArea(double grossFloorArea) {
		this.grossFloorArea = grossFloorArea;
	}
	
	private SiteContext site;
	public SiteContext getSite() {
		return site;
	}
	public void setSite(SiteContext site) {
		this.site = site;
		if(site != null) {
			super.setSiteId(site.getId());
		}
	}
	
	@Override
	public long getSiteId() {
		if(site != null) {
			return site.getId();
		}
		return super.getSiteId();
	}
}

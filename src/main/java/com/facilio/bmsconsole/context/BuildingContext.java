package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;

public class BuildingContext extends BaseSpaceContext {
	
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
		if ((site == null || site.getId() == -1) && super.getSiteId() != -1) {
			site = new SiteContext();
			site.setId(super.getSiteId());
		}
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

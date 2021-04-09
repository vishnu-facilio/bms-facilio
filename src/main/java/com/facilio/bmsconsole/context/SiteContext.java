package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;

public class SiteContext extends BaseSpaceContext {

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

	private int noOfBuildings = -1;

	public int getNoOfBuildings() {
		return noOfBuildings;
	}

	public void setNoOfBuildings(int noOfBuildings) {
		this.noOfBuildings = noOfBuildings;
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

	private int siteType = -1;

	public void setSiteType(int siteType) {
		this.siteType = siteType;
	}

	public int getSiteType() {
		return siteType;
	}

	private Long weatherStation;

	public Long getWeatherStation() {
		return weatherStation;
	}

	public void setWeatherStation(Long weatherStation) {
		this.weatherStation = weatherStation;
	}

	private double cddBaseTemperature = -1;

	public double getCddBaseTemperature() {
		return cddBaseTemperature;
	}

	public void setCddBaseTemperature(double cddBaseTemperature) {
		this.cddBaseTemperature = cddBaseTemperature;
	}

	private double hddBaseTemperature = -1;

	public double getHddBaseTemperature() {
		return hddBaseTemperature;
	}

	public void setHddBaseTemperature(double hddBaseTemperature) {
		this.hddBaseTemperature = hddBaseTemperature;
	}

	private double wddBaseTemperature = -1;

	public double getWddBaseTemperature() {
		return wddBaseTemperature;
	}

	public void setWddBaseTemperature(double wddBaseTemperature) {
		this.wddBaseTemperature = wddBaseTemperature;
	}

	public String timeZone;

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
	private int boundaryRadius = -1;
	public int getBoundaryRadius() {
		return boundaryRadius;
	}
	public void setBoundaryRadius(int boundaryRadius) {
		this.boundaryRadius = boundaryRadius;
	}

	private ClientContext client;
	public ClientContext getClient() {
		return client;
	}
	public void setClient(ClientContext client) {
		this.client = client;
	}
}

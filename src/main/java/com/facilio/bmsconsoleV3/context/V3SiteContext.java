package com.facilio.bmsconsoleV3.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.v3.context.V3Context;

public class V3SiteContext extends V3Context {
	
	private static final long serialVersionUID = 1L;
	private LocationContext location;
	private User managedBy;
	private Integer noOfBuildings;
	private Long noOfIndependentSpaces;
	private Double grossFloorArea;
	private Integer siteType;
	private Long weatherStation;
	private Double cddBaseTemperature;
	private Double hddBaseTemperature;
	private Double wddBaseTemperature;
	private Integer boundaryRadius;
	private V3ClientContext client;

	public LocationContext getLocation() {
		return location;
	}

	public void setLocation(LocationContext location) {
		this.location = location;
	}

	public User getManagedBy() {
		return managedBy;
	}

	public void setManagedBy(User managedBy) {
		this.managedBy = managedBy;
	}

	public Integer getSiteType() {
		return siteType;
	}

	public void setSiteType(Integer siteType) {
		this.siteType = siteType;
	}

	public Long getWeatherStation() {
		return weatherStation;
	}

	public void setWeatherStation(Long weatherStation) {
		this.weatherStation = weatherStation;
	}

	public String timeZone;

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

		
	public V3ClientContext getClient() {
		return client;
	}

	public void setClient(V3ClientContext client) {
		this.client = client;
	}

	public Integer getNoOfBuildings() {
		return noOfBuildings;
	}

	public void setNoOfBuildings(Integer noOfBuildings) {
		this.noOfBuildings = noOfBuildings;
	}

	public Long getNoOfIndependentSpaces() {
		return noOfIndependentSpaces;
	}

	public void setNoOfIndependentSpaces(Long noOfIndependentSpaces) {
		this.noOfIndependentSpaces = noOfIndependentSpaces;
	}

	public Double getGrossFloorArea() {
		return grossFloorArea;
	}

	public void setGrossFloorArea(Double grossFloorArea) {
		this.grossFloorArea = grossFloorArea;
	}

	public Double getCddBaseTemperature() {
		return cddBaseTemperature;
	}

	public void setCddBaseTemperature(Double cddBaseTemperature) {
		this.cddBaseTemperature = cddBaseTemperature;
	}

	public Double getHddBaseTemperature() {
		return hddBaseTemperature;
	}

	public void setHddBaseTemperature(Double hddBaseTemperature) {
		this.hddBaseTemperature = hddBaseTemperature;
	}

	public Double getWddBaseTemperature() {
		return wddBaseTemperature;
	}

	public void setWddBaseTemperature(Double wddBaseTemperature) {
		this.wddBaseTemperature = wddBaseTemperature;
	}

	public Integer getBoundaryRadius() {
		return boundaryRadius;
	}

	public void setBoundaryRadius(Integer boundaryRadius) {
		this.boundaryRadius = boundaryRadius;
	}
}

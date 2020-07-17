package com.facilio.bmsconsoleV3.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.ClientContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.SiteContext.SiteType;
import com.facilio.v3.context.V3Context;

public class V3SiteContext extends V3Context {
	
	private static final long serialVersionUID = 1L;
	private LocationContext location;
	private User managedBy;
	private Integer noOfBuildings;
	private Long noOfIndependentSpaces;
	private Double grossFloorArea;
	private SiteType siteType;
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

	public int getSiteType() {
		if (siteType != null) {
			return siteType.getIntVal();
		}
		return -1;
	}

	public void setSiteType(int siteType) {
		this.siteType = SiteType.typeMap.get(siteType);
	}

	public void setSiteType(SiteType siteType) {
		this.siteType = siteType;
	}

	public String getSiteTypeVal() {
		if (siteType != null) {
			return siteType.getStringVal();
		}
		return null;
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

	public enum SiteType {
		COMMON(1, "Common"), 
		HOSPITAL(2, "Hospital"), 
		RESIDENTIAL(3, "Residential"), 
		OFFICE(4, "Office"),
		COMMERCIAL(5, "Commercial"), 
		COMPOUND(6, "Compound"), 
		UNIVERSITY(7, "University"), 
		RETAIL(8, "Retail"),
		RESIDENTIALNCOMMERICAL(9, "Residential & Commercial");

		private int intVal;
		private String strVal;

		private SiteType(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}

		public int getIntVal() {
			return intVal;
		}

		public String getStringVal() {
			return strVal;
		}

		public static SiteType getType(int val) {
			return typeMap.get(val);
		}

		private static final Map<Integer, SiteType> typeMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, SiteType> initTypeMap() {
			Map<Integer, SiteType> typeMap = new HashMap<>();

			for (SiteType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public Map<Integer, SiteType> getAllTypes() {
			return typeMap;
		}
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

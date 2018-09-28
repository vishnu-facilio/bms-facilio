package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.accounts.dto.User;

public class SiteContext extends BaseSpaceContext {

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
	
	private double grossFloorArea = -1;
	public double getGrossFloorArea() {
		return grossFloorArea;
	}
	public void setGrossFloorArea(double grossFloorArea) {
		this.grossFloorArea = grossFloorArea;
	}
	
	private SiteType siteType;
	public int getSiteType() {
		if(siteType != null) {
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
		if(siteType != null) {
			return siteType.getStringVal();
		}
		return null;
	}
	
	private String weatherStation;
	public String getWeatherStation() {
		return weatherStation;
	}
	public void setWeatherStation(String weatherStation) {
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
	
	
	public enum SiteType {
		COMMON(1, "Common"),
		HOSPITAL(2, "Hospital"),
		RESIDENTIAL(3, "Residential"),
		OFFICE(4, "Office"),
		COMMERCIAL(5, "Commercial"),
		COMPOUND(6, "Compound")
		;
		
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
			
			for(SiteType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, SiteType> getAllTypes() {
			return typeMap;
		}
	}
}

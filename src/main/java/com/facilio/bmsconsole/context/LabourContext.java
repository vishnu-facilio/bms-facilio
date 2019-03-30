package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LabourContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private boolean availability = false;
	
	public boolean getAvailability() {
		return availability;
	}
	public void setAvailability(boolean availability) {
		this.availability = availability;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
   private LabourUnitType unitType;
	
	public int getUnitType() {
		if (unitType != null) {
			return unitType.getIntValForDB();
		}
		return -1;
	}
	
	public LabourUnitType getUnitTypeEnum() {
		return unitType;
	}
	
	public void setUnitType(LabourUnitType unitType) {
		this.unitType = unitType;
	}
	
	public void setUnitType(int unitType) {
		this.unitType = LabourUnitType.UNIT_MAP.get(unitType);
	}
	
	public int getUnitTypeInt() {
		if (unitType != null) {
			return unitType.getIntValForDB();
		}
		return -1;
	}
	private User user;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	private String email;
	private String address;
	private String phone;
	private double cost = -1;
	
	public enum LabourUnitType {
		HOURLY(1),
		DAILY(2), 
		WEEKLY(3), 
		MONTHLY(4), 
		;
		
		private int intVal;
		private LabourUnitType(int intVal) {
			this.intVal = intVal;
		}
		
		public int getIntValForDB() {
			return intVal;
		}
		
		public static final Map<Integer, LabourUnitType> UNIT_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, LabourUnitType> initTypeMap() {
			Map<Integer, LabourUnitType> typeMap = new HashMap<>();
			for(LabourUnitType type : values()) {
				typeMap.put(type.getIntValForDB(), type);
			}
			return typeMap;
		}
	}

	private LocationContext location;

	public LocationContext getLocation() {
		return location;
	}

	public void setLocation(LocationContext location) {
		this.location = location;
	}

	public long getLocationId() {
		// TODO Auto-generated method stub
		if (location != null) {
			return location.getId();
		}
		return -1;
	}

	
	
	
}

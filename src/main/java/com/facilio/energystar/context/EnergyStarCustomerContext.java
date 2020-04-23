package com.facilio.energystar.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.json.annotations.JSON;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class EnergyStarCustomerContext {
	
	long id;
	long orgId;
	long createdTime;
	long createdBy;
	String energyStarCustomerId;
	String userName;
	String password;
	long dataExchangeMode = -1;
	List<Data_Exchange_Mode> availableDataExchangeModes;
	
	
	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public long getOrgId() {
		return orgId;
	}


	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}


	public long getCreatedTime() {
		return createdTime;
	}


	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}


	public long getCreatedBy() {
		return createdBy;
	}


	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	@JSON(serialize=false)
	public String getEnergyStarCustomerId() {
		return energyStarCustomerId;
	}


	public void setEnergyStarCustomerId(String energyStarCustomerId) {
		this.energyStarCustomerId = energyStarCustomerId;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}

	@JSON(serialize=false)
	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public long getDataExchangeMode() {
		return dataExchangeMode;
	}


	public void setDataExchangeMode(long dataExchangeMode) {
		this.dataExchangeMode = dataExchangeMode;
	}


	public List<Data_Exchange_Mode> getAvailableDataExchangeModes() {
		return availableDataExchangeModes;
	}


	public void setAvailableDataExchangeModes(List<Data_Exchange_Mode> availableDataExchangeModes) {
		this.availableDataExchangeModes = availableDataExchangeModes;
	}


	public enum Data_Exchange_Mode {
		ELECTRIC(1, "Electric",1,"energydata","kWh (thousand Watt-hours)"),
		NATURAL_GAS(2, "Natural Gas",2,null,null),
		PROPANE(3,"Propane",4,null,null);
		;

		int intVal;
		String name;
		long license;
		String moduleName;
		String unitOfMessure;
		
		public String getUnitOfMessure() {
			return unitOfMessure;
		}

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}

		public long getLicence() {
			return license;
		}
		private Data_Exchange_Mode(int intVal, String name,long licenceId,String moduleName,String unitOfMeasure) {
			this.intVal = intVal;
			this.name = name;
			this.license = licenceId;
			this.moduleName = moduleName;
			this.unitOfMessure = unitOfMeasure;
		}

		private static final Map<Integer, Data_Exchange_Mode> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, Data_Exchange_Mode> initTypeMap() {
			Map<Integer, Data_Exchange_Mode> typeMap = new HashMap<>();

			for (Data_Exchange_Mode type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, Data_Exchange_Mode> getAllAppTypes() {
			return optionMap;
		}
	}
}

package com.facilio.energystar.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
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
	Type type;
	String shareKey;
	Share_Status shareStatus;
	
	public int getShareStatus() {
		if(shareStatus != null) {
			return shareStatus.getIntVal();
		}
		return -1;
	}

	public void setShareStatus(int shareStatus) {
		this.shareStatus = Share_Status.getAllAppTypes().get(shareStatus);
	}

	public String getShareKey() {
		return shareKey;
	}

	public void setShareKey(String shareKey) {
		this.shareKey = shareKey;
	}

	public int getType() {
		if(type != null) {
			return type.getIntVal();
		}
		return -1;
	}

	public void setType(int type) {
		this.type = Type.getAllAppTypes().get(type);
	}

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
		ELECTRIC(1, "Electric",1,"kWh (thousand Watt-hours)","energydata","totalEnergyConsumptionDelta",NumberAggregateOperator.SUM),
		NATURAL_GAS(2, "Natural Gas",2,null,null,null,null),
		PROPANE(3,"Propane",4,null,null,null,null);
		;

		int intVal;
		String name;
		long license;
		String moduleName;
		String unitOfMessure;
		String fieldName;
		NumberAggregateOperator aggr;
		
		public String getModule() {
			return moduleName;
		}
		public String getField() {
			return fieldName;
		}
		public NumberAggregateOperator getAggr() {
			return aggr;
		}
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
		private Data_Exchange_Mode(int intVal, String name,long licenceId,String unitOfMeasure,String moduleName,String fieldName,NumberAggregateOperator aggr) {
			this.intVal = intVal;
			this.name = name;
			this.license = licenceId;
			this.moduleName = moduleName;
			this.unitOfMessure = unitOfMeasure;
			this.fieldName = fieldName;
			this.aggr = aggr;
		}

		private static final Map<Integer, Data_Exchange_Mode> optionMap = Collections.unmodifiableMap(initTypeMap());
		
		private static final Map<String, Data_Exchange_Mode> TypeStringMap = Collections.unmodifiableMap(initTypeStringMap());

		private static Map<Integer, Data_Exchange_Mode> initTypeMap() {
			Map<Integer, Data_Exchange_Mode> typeMap = new HashMap<>();

			for (Data_Exchange_Mode type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		
		private static Map<String, Data_Exchange_Mode> initTypeStringMap() {
			Map<String, Data_Exchange_Mode> typeMap = new HashMap<>();

			for (Data_Exchange_Mode type : values()) {
				typeMap.put(type.getName(), type);
			}
			return typeMap;
		}

		public static Map<Integer, Data_Exchange_Mode> getAllAppTypes() {
			return optionMap;
		}
		public static Map<String, Data_Exchange_Mode> getAllTypes() {
			return TypeStringMap;
		}
	}
	
	public enum Type {
		CREATED(1, "Created"),
		SHARED(2, "Shared"),
		;

		int intVal;
		String name;
		

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}

		private Type(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}

		private static final Map<Integer, Type> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, Type> initTypeMap() {
			Map<Integer, Type> typeMap = new HashMap<>();

			for (Type type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, Type> getAllAppTypes() {
			return optionMap;
		}
	}
	
	public enum Share_Status {
		CREATED(1, "Created"),
		SHARED(2, "Shared"),
		REJECTED(3, "Rejected"),
		;

		int intVal;
		String name;
		

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}

		private Share_Status(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}

		private static final Map<Integer, Share_Status> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, Share_Status> initTypeMap() {
			Map<Integer, Share_Status> typeMap = new HashMap<>();

			for (Share_Status type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, Share_Status> getAllAppTypes() {
			return optionMap;
		}
	}
}

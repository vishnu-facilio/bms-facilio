package com.facilio.cb.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.modules.FieldType;

public class ChatBotIntentParam {

	long id = -1;
	long orgId = -1;
	long intentId = -1;
	String name;
	String askAs;
	FieldType dataType;
	ML_Type mlType;
	int localId;
	
	public int getLocalId() {
		return localId;
	}


	public void setLocalId(int localId) {
		this.localId = localId;
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


	public long getIntentId() {
		return intentId;
	}


	public void setIntentId(long intentId) {
		this.intentId = intentId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getAskAs() {
		return askAs;
	}


	public void setAskAs(String askAs) {
		this.askAs = askAs;
	}


	public int getDataType() {
		if(dataType != null) {
			return dataType.getTypeAsInt();
		}
		return -1;
	}


	public void setDataType(int dataType) {
		this.dataType = FieldType.getCFType(dataType);
	}


	public int getMlType() {
		if(mlType != null) {
			return mlType.getIntVal();
		}
		return -1;
	}


	public void setMlType(int mlType) {
		this.mlType = ML_Type.getAllMLTypes().get(mlType);
	}


	public enum ML_Type {
		
		PERSON(1, "Person"),
		TIME(2, "Time"),
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}

		private ML_Type(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}

		private static final Map<Integer, ML_Type> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, ML_Type> initTypeMap() {
			Map<Integer, ML_Type> typeMap = new HashMap<>();

			for (ML_Type type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, ML_Type> getAllMLTypes() {
			return optionMap;
		}
	}
}

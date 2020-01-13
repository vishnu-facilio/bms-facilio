package com.facilio.cb.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.accounts.util.AccountConstants;

public class ChatBotModel {

	long id = -1;
	long orgId = -1;
	App_Type type;
	String mlModel;
	
	ChatBotModelVersion chatBotModelVersion;
	
	
	public ChatBotModelVersion getChatBotModelVersion() {
		return chatBotModelVersion;
	}
	public void setChatBotModelVersion(ChatBotModelVersion chatBotModelVersion) {
		this.chatBotModelVersion = chatBotModelVersion;
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

	public int getType() {
		if(type != null) {
			return type.getIntVal();
		}
		return -1;
	}

	public void setType(int type) {
		this.type = App_Type.getAllAppTypes().get(type);
	}

	public String getMlModel() {
		return mlModel;
	}

	public void setMlModel(String mlModel) {
		this.mlModel = mlModel;
	}

	public enum App_Type {
		
		APP(1, "Facilio App",AccountConstants.UserType.USER.getValue()),
		TENENT(2, "Tenent App",AccountConstants.UserType.REQUESTER.getValue()),
		//VENDOR(3, "Vendor App"),
		;

		int intVal;
		String name;
		int userType;

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}
		
		public int getUserType() {
			return userType;
		}

		private App_Type(int intVal, String name,int userType) {
			this.intVal = intVal;
			this.name = name;
			this.userType = userType;
		}

		private static final Map<Integer, App_Type> optionMap = Collections.unmodifiableMap(initTypeMap());
		
		private static final Map<Integer, App_Type> userMap = Collections.unmodifiableMap(initUserMap());

		private static Map<Integer, App_Type> initTypeMap() {
			
			Map<Integer, App_Type> typeMap = new HashMap<>();

			for (App_Type type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		
		private static Map<Integer, App_Type> initUserMap() {
			
			Map<Integer, App_Type> typeMap = new HashMap<>();

			for (App_Type type : values()) {
				typeMap.put(type.getUserType(), type);
			}
			return typeMap;
		}

		public static Map<Integer, App_Type> getAllAppTypes() {
			return optionMap;
		}
		
		public static Map<Integer, App_Type> getAllAppTypesUsers() {
			return userMap;
		}
	}
}

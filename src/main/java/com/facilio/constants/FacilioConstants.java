package com.facilio.constants;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class FacilioConstants {
	
	public static class CognitoUserPool {
		
		public static String getAWSAccountId() {
			return "665371858763";
		}
		
		public static String getUserPoolId() {
			return "us-west-2_kzN5KrMZU";
		}
		
		public static String getIdentityPoolId() {
			return "us-west-2:ba15c3b0-a6d9-4f33-8841-5b813d55170e";
		}
		
		public static String getClientId() {
			return "74d026sk7dde4vdsgpkhjhj17m";
		}
		
		public String toString() {
			JSONObject userPool = new JSONObject();
			userPool.put("UserPoolId", getUserPoolId());
			userPool.put("ClientId", getClientId());
			return userPool.toJSONString();
		}
	}
	
	public static class Role {
		
		public static final int ADMIN = 0;
		
		public static final int MANAGER = 1;
		
		public static final int AGENT = 2;
		
		public static final int REQUESTER = 3;
		
		public static final HashMap<Integer, String> ALL_ROLES = new HashMap<Integer, String>();
		
		static {
			ALL_ROLES.put(ADMIN, "Administrator");
			ALL_ROLES.put(MANAGER, "Manager");
			ALL_ROLES.put(AGENT, "Agent");
			ALL_ROLES.put(REQUESTER, "Requester");
		}
	}
	
	public static class ContextNames {
		public static final String TASK = "task";
		public static final String TASK_LIST = "tasks";
		
		public static final String SCHEDULE_OBJECT = "scheduleObject";
		
		public static final String NOTE = "note";
		public static final String NOTE_LIST = "notes";
		
		public static final String ACTION_FORM = "actionForm";
		
		public static final String MODULE_NAME = "moduleName";
		
		public static final String CUSTOM_FIELDS = "customFields";
	}
}
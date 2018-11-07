package com.facilio.workflows.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;

public enum FacilioSystemFunctions implements FacilioWorkflowFunctionInterface {
	
	/*
	 * @param roleId Role ID
	 * @param resourceId Resource ID
	 * @return Comma separated EMails of Role Members 
	 */
	ROLE_EMAILS (1, "getRoleEmails", 2) {
		@Override
		public Object execute(Object... objects) throws Exception {
			// TODO Auto-generated method stub
			ROLE_EMAILS.checkParams(objects);
			List<User> users = getUsersFromRoleAndResource(objects);
			
			if (users != null && !users.isEmpty()) {
				return users.stream()
							.filter(u -> u.getEmail() != null && !u.getEmail().isEmpty())
							.map(User::getEmail)
							.collect(Collectors.joining(","));
			}
			
			return "";
		}
	},
	
	/*
	 * @param roleId Role ID
	 * @param resourceId Resource ID
	 * @return Comma separated Phone numbers of Role Members 
	 */
	ROLE_PHONE (2, "getRolePhone", 2) {
		@Override
		public Object execute(Object... objects) throws Exception {
			// TODO Auto-generated method stub
			ROLE_PHONE.checkParams(objects);
			List<User> users = getUsersFromRoleAndResource(objects);
			
			if (users != null && !users.isEmpty()) {
				StringJoiner joiner = new StringJoiner(",");
				for (User user : users) {
					if (user.getMobile() != null && !user.getMobile().isEmpty()) {
						joiner.add(user.getMobile());
					}
					else if (user.getPhone() != null && !user.getPhone().isEmpty()) {
						joiner.add(user.getPhone());
					}
				}
				return joiner.toString();
			}
			
			return "";
		}
	},
	
	/*
	 * @param roleId Role ID
	 * @param resourceId Resource ID
	 * @return Comma separated OUIDs of Role Members 
	 */
	ROLE_OUID (3, "getRoleOuids", 2) {
		@Override
		public Object execute(Object... objects) throws Exception {
			// TODO Auto-generated method stub
			ROLE_OUID.checkParams(objects);
			List<User> users = getUsersFromRoleAndResource(objects);
			
			if (users != null && !users.isEmpty()) {
				return users.stream()
							.map(u -> String.valueOf(u.getOuid()))
							.collect(Collectors.joining(","));
			}
			
			return "";
		}
	}
	;
	
	private int value, requiredParams;
	private String functionName;
	private String namespace = "system";
	private FacilioFunctionNameSpace nameSpaceEnum = FacilioFunctionNameSpace.SYSTEM;
	
	FacilioSystemFunctions(int value,String functionName, int requiredParams) {
		this.value = value;
		this.functionName = functionName;
		this.requiredParams = requiredParams;
	}
	
	private void checkParams (Object... objects) {
		if (requiredParams > 0 && (objects == null || objects.length < requiredParams)) {
			throw new IllegalArgumentException("Required objects are null for function : "+namespace+"."+functionName);
		}
		
		for (int i = 0; i < requiredParams; i++) {
			if (objects[i] == null) {
				throw new IllegalArgumentException("Required objects are null for function : "+namespace+"."+functionName);
			}
		}
	}

	@Override
	public abstract Object execute(Object... objects) throws Exception;
	
	public static Map<String, FacilioSystemFunctions> getAllFunctions() {
		return SYSTEM_FUNCTIONS;
	}
	public static FacilioSystemFunctions getFacilioSystemFunction(String functionName) {
		return SYSTEM_FUNCTIONS.get(functionName);
	}
	private static final Map<String, FacilioSystemFunctions> SYSTEM_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	private static Map<String, FacilioSystemFunctions> initTypeMap() {
		Map<String, FacilioSystemFunctions> typeMap = new HashMap<>();
		for(FacilioSystemFunctions type : values()) {
			typeMap.put(type.functionName, type);
		}
		return typeMap;
	}
	
	private static List<User> getUsersFromRoleAndResource (Object... objects) throws Exception {
		long roleId = Long.parseLong(objects[0].toString());
		long resourceId = Long.parseLong(objects[1].toString());
		
		ResourceContext resource = ResourceAPI.getResource(resourceId);
		if (resource != null) {
			long spaceId = resource.getSpaceId();
			if (spaceId != -1) {
				return AccountUtil.getUserBean().getUsersWithRoleAndAccessibleSpace(roleId, spaceId);
			}
		}
		return null;
	}
}

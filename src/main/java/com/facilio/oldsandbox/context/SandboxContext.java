package com.facilio.oldsandbox.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SandboxContext {

	long id = -1l;
	long orgId = -1l;
	String name;
	String domain;
	long sandboxOrgId = -1l;
	SandboxStatus status;
	List<SandboxSharingContext> sharing;
	long createdBy = -1l;
	long createdTime = -1l;
	long modifiedTime = -1l;
	
	public void setStatus(int statusInt) {
		status = SandboxStatus.getStatus(statusInt);
	}
	
	public SandboxStatus getStatusEnum() {
		return status;
	}
	
	public void setStatusEnum(SandboxStatus status) {
		this.status = status;
	}
	
	public int getStatus() {
		if(status != null) {
			return status.getIntVal();
		}
		return -1;
	}
	
	public static enum SandboxStatus {
		
		ACTIVE(1, "Active"),
		INACTIVE(2, "In-Active"),
		;
		
		private int intVal;
		private String strVal;
		
		private SandboxStatus(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		public static SandboxStatus getStatus(int val) {
			return classificationType.get(val);
		}
		
		private static final Map<Integer, SandboxStatus> classificationType = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, SandboxStatus> initTypeMap() {
			Map<Integer, SandboxStatus> classificationType = new HashMap<>();
			
			for(SandboxStatus type : values()) {
				classificationType.put(type.getIntVal(), type);
			}
			return classificationType;
		}
		public Map<Integer, SandboxStatus> getAllClassification() {
			return classificationType;
		}
	}
	
	
	
}

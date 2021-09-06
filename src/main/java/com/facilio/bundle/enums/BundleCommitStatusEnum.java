package com.facilio.bundle.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public enum BundleCommitStatusEnum {
	
	COMMITED(1,"Commited"),
	NOT_YET_COMMITED(2,"Not Yet Commited"),
	;
	
	int value;
	String name;

	BundleCommitStatusEnum(int value, String name) {
		this.value = value;
		this.name = name;
	}

	
	private static final Map<Integer,BundleCommitStatusEnum> COMMIT_TYPES = BundleCommitStatusEnum.initValues();
	
	public static Map<Integer,BundleCommitStatusEnum> initValues() {
		
		Map<Integer,BundleCommitStatusEnum> COMMIT_TYPES = new HashMap<Integer, BundleCommitStatusEnum>();
		
		for(BundleCommitStatusEnum mode : BundleCommitStatusEnum.values()) {
			COMMIT_TYPES.put(mode.getValue(), mode);
		}
		return COMMIT_TYPES;
	}
	
	public static BundleCommitStatusEnum valueOf(Integer value) {
		
		return COMMIT_TYPES.get(value);
	}
}

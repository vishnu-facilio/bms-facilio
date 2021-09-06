package com.facilio.bundle.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public enum BundleModeEnum {
	
	DUMMY(0,"Dummy"),
	ADD(1,"Module"),
	UPDATE(2,"Field"),
	DELETE(3,"Function"),
	;
	
	int value;
	String name;

	BundleModeEnum(int value, String name) {
		this.value = value;
		this.name = name;
	}

	
	private static final Map<Integer,BundleModeEnum> CREATION_TYPES = BundleModeEnum.initValues();
	
	public static Map<Integer,BundleModeEnum> initValues() {
		
		Map<Integer,BundleModeEnum> CREATION_TYPES = new HashMap<Integer, BundleModeEnum>();
		
		for(BundleModeEnum mode : BundleModeEnum.values()) {
			CREATION_TYPES.put(mode.getValue(), mode);
		}
		return CREATION_TYPES;
	}
	
	public static BundleModeEnum valueOf(Integer value) {
		
		return CREATION_TYPES.get(value);
	}
}

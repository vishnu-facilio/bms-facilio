package com.facilio.bundle.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public enum BundleModeEnum {
	
	ADD(1,"Add"),
	UPDATE(2,"Update"),
	DELETE(3,"Delete"),
	;
	
	int value;
	String name;

	BundleModeEnum(int value, String name) {
		this.value = value;
		this.name = name;
	}

	
	private static final Map<Integer,BundleModeEnum> CREATION_TYPES = BundleModeEnum.initValues();
	
	private static final Map<String,BundleModeEnum> MODE_WITHNAMES = BundleModeEnum.initValuesWithNames();
	
	public static Map<Integer,BundleModeEnum> initValues() {
		
		Map<Integer,BundleModeEnum> CREATION_TYPES = new HashMap<Integer, BundleModeEnum>();
		
		for(BundleModeEnum mode : BundleModeEnum.values()) {
			CREATION_TYPES.put(mode.getValue(), mode);
		}
		return CREATION_TYPES;
	}
	
	public static Map<String,BundleModeEnum> initValuesWithNames() {
		
		Map<String,BundleModeEnum> CREATION_TYPES = new HashMap<String, BundleModeEnum>();
		
		for(BundleModeEnum mode : BundleModeEnum.values()) {
			CREATION_TYPES.put(mode.getName(), mode);
		}
		return CREATION_TYPES;
	}
	
	public static BundleModeEnum valueOf(Integer value) {
		
		return CREATION_TYPES.get(value);
	}
	
	public static BundleModeEnum valueOfName(String name) {
		
		return MODE_WITHNAMES.get(name);
	}
}

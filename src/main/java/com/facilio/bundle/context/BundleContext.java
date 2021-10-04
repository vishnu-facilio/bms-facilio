package com.facilio.bundle.context;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BundleContext {

	Long id;
	Long orgId;
	String bundleName;
	String bundleGlobalName;
	String version;
	Long parentBundleId;
	Long bundleFileId;
	BundleTypeEnum type;
	Long createdTime;
	Long modifiedTime;
	
	public int getType() {
		if(type != null) {
			return type.getValue();
		}
		return -1;
	}
	
	public void setType(int type) {
		this.type = BundleTypeEnum.valueOf(type);
	}
	
	public void setTypeEnum(BundleTypeEnum type) {
		this.type = type;
	}
	
	public BundleTypeEnum getTypeEnum() {
		return type;
	}
	
	@Getter
	public enum BundleTypeEnum {
		
		UN_MANAGED_SYSTEM(1,"Un-Managed System"),
		UN_MANAGED(2,"Un-Managed"),
		MANGED(3,"Managed"),
		;
		
		int value;
		String name;

		BundleTypeEnum(int value, String name) {
			this.value = value;
			this.name = name;
		}

		
		private static final Map<Integer,BundleTypeEnum> CREATION_TYPES = BundleTypeEnum.initValues();
		
		public static Map<Integer,BundleTypeEnum> initValues() {
			
			Map<Integer,BundleTypeEnum> CREATION_TYPES = new HashMap<Integer, BundleTypeEnum>();
			
			for(BundleTypeEnum mode : BundleTypeEnum.values()) {
				CREATION_TYPES.put(mode.getValue(), mode);
			}
			return CREATION_TYPES;
		}
		
		public static BundleTypeEnum valueOf(Integer value) {
			
			return CREATION_TYPES.get(value);
		}
		
	}

}

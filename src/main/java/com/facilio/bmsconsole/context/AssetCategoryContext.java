package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AssetCategoryContext extends ModuleBaseWithCustomFields {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private AssetCategoryType type;
	public int getType() {
		if(type != null) {
			return type.getIntVal();
		}
		return -1;
	}
	public void setType(int type) {
		this.type = AssetCategoryType.STATE_MAP.get(type);
	}
	public void setType(AssetCategoryType type) {
		this.type = type;
	}
	public String getTypeVal() {
		if(type != null) {
			return type.getStringVal();
		}
		return null;
	}
	public AssetCategoryType getTypeEnum() {
		return type;
	}
	
	private long parentCategoryId;
	public long getParentCategoryId() {
		return parentCategoryId;
	}
	public void setParentCategoryId(long parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}
	
	private long assetModuleID;
	public long getAssetModuleID() {
		return assetModuleID;
	}
	public void setAssetModuleID(long assetModuleID) {
		this.assetModuleID = assetModuleID;
	}

	public static enum AssetCategoryType {
		MISC(0, "Misc"),
		HVAC(1, "HVAC"),
		ENERGY(2, "Energy"),
		FIRE(3, "Fire")
		;
		
		private int intVal;
		private String strVal;
		
		private AssetCategoryType(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		public static AssetCategoryType getType(int val) {
			return STATE_MAP.get(val);
		}
		
		private static final Map<Integer, AssetCategoryType> STATE_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, AssetCategoryType> initTypeMap() {
			Map<Integer, AssetCategoryType> typeMap = new HashMap<>();
			
			for(AssetCategoryType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, AssetCategoryType> getAllTypes() {
			return STATE_MAP;
		}
	}
}

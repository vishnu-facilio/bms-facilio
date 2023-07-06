package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.modules.ModuleBaseWithCustomFields;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
@Setter
@Getter
public class AssetCategoryContext extends ModuleBaseWithCustomFields {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
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
	private long assetModuleID;

//	public String getAssetModuleName() throws Exception {
//		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//		FacilioModule module = modBean.getModule(assetModuleID);
//		if (module != null) {
//			return module.getName();
//		}
//		return null;
//	}
	
	private String moduleName;

	private String displayName;
	public String getDisplayName() {
		if(displayName != null && !displayName.isEmpty()) {
			return displayName;
		}
		else {
			return name;
		}
	}
	private Boolean isDefault;
	private String description;

	public static enum AssetCategoryType {
		MISC(0, "Misc"),
		HVAC(1, "HVAC"),
		ENERGY(2, "Energy"),
		FIRE(3, "Fire"),
		CONTROLLER(4,"Controller"),
		DEVICE(5,"Device")
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

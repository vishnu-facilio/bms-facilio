package com.facilio.energystar.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.energystar.context.EnergyStarPropertyContext.Building_Type;
import com.facilio.modules.FieldType;

public class EnergyStarPropertyUseContext {

	long id;
	long orgId;
	long propertyId;
	Property_Use properyUseType;
	String value;
	
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
	public long getPropertyId() {
		return propertyId;
	}
	public void setPropertyId(long propertId) {
		this.propertyId = propertId;
	}
	public int getProperyUseType() {
		if(properyUseType != null) {
			return properyUseType.getIntVal();
		}
		return -1;
	}
	public Property_Use getProperyUseTypeEnum() {
		return properyUseType;
	}
	public void setProperyUseType(int properyUseType) {
		this.properyUseType = Property_Use.getAllAppTypes().get(properyUseType);
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public enum Property_Use {
		
		totalGrossFloorArea(1, "Total gross floor area","totalGrossFloorArea",FieldType.STRING,Building_Type.OFFICE,"ft\u00B2"),
		numberOfComputers(2, "No. of computers","numberOfComputers",FieldType.STRING,Building_Type.OFFICE,null),
		percentOfficeCooled(3, "Percent office cooled","percentOfficeCooled",FieldType.STRING,Building_Type.OFFICE,"%"),
		percentOfficeHeated(4, "Percent office heated","percentOfficeHeated",FieldType.STRING,Building_Type.OFFICE,"%"),
		weeklyOperatingHours(5, "Weekly operating hours","weeklyOperatingHours",FieldType.STRING,Building_Type.OFFICE,null),
		numberOfWorkers(6, "Number of workers","numberOfWorkers",FieldType.STRING,Building_Type.OFFICE,null),
		;

		int intVal;
		String displayName;
		String fieldName;
		FieldType type;
		Building_Type buildingType;
		String unit;

		public String getDisplayName() {
			return displayName;
		}

		public String getFieldName() {
			return fieldName;
		}

		public FieldType getType() {
			return type;
		}
		
		public String getUnit() {
			return unit;
		}
		
		public Building_Type getBuildingType() {
			return buildingType;
		}

		public int getIntVal() {
			return intVal;
		}

		private Property_Use(int intVal, String displayName,String fieldName,FieldType fieldType, Building_Type building_Type,String unit) {
			this.intVal = intVal;
			this.displayName = displayName;
			this.fieldName = fieldName;
			this.type = fieldType;
			this.buildingType = building_Type;
			this.unit = unit;
		}
		
		private static final Map<Integer, List<Property_Use>> PROPERTY_USE_BUILDING_TYPE_MAP = Collections.unmodifiableMap(initPointUseBuilddingTypeMap());
		
		private static Map<Integer, List<Property_Use>> initPointUseBuilddingTypeMap() {
			Map<Integer, List<Property_Use>> typeMap = new HashMap<>();
			
			for(Property_Use type : values()) {
				List<Property_Use> useList = typeMap.get(type.getBuildingType().getIntVal()) == null ? new ArrayList<>() : typeMap.get(type.getBuildingType().getIntVal()); 
				useList.add(type);
				
				typeMap.put(type.getBuildingType().getIntVal(), useList);
			}
			return typeMap;
		}

		private static final Map<Integer, Property_Use> optionMap = Collections.unmodifiableMap(initTypeMap());
		
		private static final Map<String, Property_Use> nameMap = Collections.unmodifiableMap(initNameMap());

		private static Map<Integer, Property_Use> initTypeMap() {
			Map<Integer, Property_Use> typeMap = new HashMap<>();

			for (Property_Use type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		
		private static Map<String, Property_Use> initNameMap() {
			Map<String, Property_Use> typeMap = new HashMap<>();

			for (Property_Use type : values()) {
				typeMap.put(type.getFieldName(), type);
			}
			return typeMap;
		}

		public static Map<Integer, Property_Use> getAllAppTypes() {
			return optionMap;
		}
		
		public static Map<String, Property_Use> getNameMap() {
			return nameMap;
		}
		
		public static Map<Integer, List<Property_Use>> getPropertyUseBuildingTypeMap() {
			return PROPERTY_USE_BUILDING_TYPE_MAP;
		}
	}
}

package com.facilio.energystar.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.energystar.context.EnergyStarPropertyContext.Building_Type;
import com.facilio.modules.FieldType;

public class EnergyStarProperyUseContext {

	long id;
	long orgId;
	long propertyId;
	Propert_Use properyUseType;
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
	public void setProperyUseType(int properyUseType) {
		this.properyUseType = Propert_Use.getAllAppTypes().get(properyUseType);
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public enum Propert_Use {
		
		totalGrossFloorArea(1, "Total gross floor area","totalGrossFloorArea",FieldType.STRING,Building_Type.OFFICE),
		numberOfComputers(2, "No. of computers","numberOfComputers",FieldType.STRING,Building_Type.OFFICE),
		percentOfficeCooled(3, "Percent office cooled","percentOfficeCooled",FieldType.STRING,Building_Type.OFFICE),
		percentOfficeHeated(4, "Percent office heated","percentOfficeHeated",FieldType.STRING,Building_Type.OFFICE),
		weeklyOperatingHours(5, "Weekly operating hours","weeklyOperatingHours",FieldType.STRING,Building_Type.OFFICE),
		numberOfWorkers(6, "Number of worklers","numberOfWorkers",FieldType.STRING,Building_Type.OFFICE),
		;

		int intVal;
		String displayName;
		String fieldName;
		FieldType type;
		Building_Type buildingType;

		public String getDisplayName() {
			return displayName;
		}

		public String getFieldName() {
			return fieldName;
		}

		public FieldType getType() {
			return type;
		}
		
		public Building_Type getBuildingType() {
			return buildingType;
		}

		public int getIntVal() {
			return intVal;
		}

		private Propert_Use(int intVal, String displayName,String fieldName,FieldType fieldType, Building_Type building_Type) {
			this.intVal = intVal;
			this.displayName = displayName;
			this.fieldName = fieldName;
			this.type = fieldType;
			this.buildingType = building_Type;
		}

		private static final Map<Integer, Propert_Use> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, Propert_Use> initTypeMap() {
			Map<Integer, Propert_Use> typeMap = new HashMap<>();

			for (Propert_Use type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, Propert_Use> getAllAppTypes() {
			return optionMap;
		}
	}
}

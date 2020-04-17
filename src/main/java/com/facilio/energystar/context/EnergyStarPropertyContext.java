package com.facilio.energystar.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.json.annotations.JSON;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class EnergyStarPropertyContext {

	long id;
	long orgId;
	long buildingId;
	Building_Type buildingType;
	long energyStarPropertyId;
	long energyStarPropertyUseId;
	long energyStarDesignId;
	
	List<EnergyStarProperyUseContext> propertyUseContexts;
	
	List<EnergyStarMeterContext> meterContexts;

	
	public List<EnergyStarProperyUseContext> getPropertyUseContexts() {
		return propertyUseContexts;
	}


	public void setPropertyUseContexts(List<EnergyStarProperyUseContext> propertyUseContexts) {
		this.propertyUseContexts = propertyUseContexts;
	}


	public List<EnergyStarMeterContext> getMeterContexts() {
		return meterContexts;
	}


	public void setMeterContexts(List<EnergyStarMeterContext> meterContexts) {
		this.meterContexts = meterContexts;
	}


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


	public long getBuildingId() {
		return buildingId;
	}


	public void setBuildingId(long buildingId) {
		this.buildingId = buildingId;
	}


	public int getBuildingType() {
		if(buildingType != null) {
			return buildingType.getIntVal();
		}
		return -1;
	}


	public void setBuildingType(int buildingType) {
		this.buildingType = Building_Type.getAllAppTypes().get(buildingType);
	}


	public long getEnergyStarPropertyId() {
		return energyStarPropertyId;
	}

	@JsonIgnore
	@JSON(serialize=false)
	public void setEnergyStarPropertyId(long energyStarPropertyId) {
		this.energyStarPropertyId = energyStarPropertyId;
	}


	public long getEnergyStarPropertyUseId() {
		return energyStarPropertyUseId;
	}

	@JsonIgnore
	@JSON(serialize=false)
	public void setEnergyStarPropertyUseId(long energyStarPropertyUseId) {
		this.energyStarPropertyUseId = energyStarPropertyUseId;
	}


	public long getEnergyStarDesignId() {
		return energyStarDesignId;
	}

	@JsonIgnore
	@JSON(serialize=false)
	public void setEnergyStarDesignId(long energyStarDesignId) {
		this.energyStarDesignId = energyStarDesignId;
	}


	enum Building_Type {
		SCHOOL(1, "k-12 School"),
		OFFICE(2, "Office"),
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}

		private Building_Type(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}

		private static final Map<Integer, Building_Type> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, Building_Type> initTypeMap() {
			Map<Integer, Building_Type> typeMap = new HashMap<>();

			for (Building_Type type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, Building_Type> getAllAppTypes() {
			return optionMap;
		}
	}
}

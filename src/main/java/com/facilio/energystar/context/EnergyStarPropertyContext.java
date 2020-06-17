package com.facilio.energystar.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class EnergyStarPropertyContext extends ModuleBaseWithCustomFields {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	long buildingId;
	Building_Type buildingType;
	String energyStarPropertyId;
	String energyStarPropertyUseId;
	String energyStarDesignId;
	
	String yearBuild;
	String occupancyPercentage;
	
	BuildingContext buildingContext;
	String meta;
	
	boolean isConfigured;
	
	public boolean isConfigured() {
		return isConfigured;
	}

	public void setConfigured(boolean isConfigured) {
		this.isConfigured = isConfigured;
	}


	long baselineMonth = -1l;
	long lastMetricsRetrivedDate = -1l;
	
	public long getLastMetricsRetrivedDate() {
		return lastMetricsRetrivedDate;
	}

	public void setLastMetricsRetrivedDate(long lastMetricsRetrivedDate) {
		this.lastMetricsRetrivedDate = lastMetricsRetrivedDate;
	}

	public long getBaselineMonth() {
		return baselineMonth;
	}

	public void setBaselineMonth(long baselineMonth) {
		this.baselineMonth = baselineMonth;
	}

	public String getMeta() {
		return meta;
	}

	public void setMeta(String meta) {
		this.meta = meta;
	}

	public BuildingContext getBuildingContext() {
		return buildingContext;
	}


	public void setBuildingContext(BuildingContext buildingContext) {
		this.buildingContext = buildingContext;
	}


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

	public String getYearBuild() {
		return yearBuild;
	}


	public void setYearBuild(String yearBuild) {
		this.yearBuild = yearBuild;
	}


	public String getOccupancyPercentage() {
		return occupancyPercentage;
	}


	public void setOccupancyPercentage(String occupancyPercentage) {
		this.occupancyPercentage = occupancyPercentage;
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
	
	public Building_Type getBuildingTypeEnum() {
			return buildingType;
	}


	public void setBuildingType(int buildingType) {
		this.buildingType = Building_Type.getAllAppTypes().get(buildingType);
	}


	public String getEnergyStarPropertyId() {
		return energyStarPropertyId;
	}

	@JSON(serialize=false)
	public void setEnergyStarPropertyId(String energyStarPropertyId) {
		this.energyStarPropertyId = energyStarPropertyId;
	}


	public String getEnergyStarPropertyUseId() {
		return energyStarPropertyUseId;
	}

	@JSON(serialize=false)
	public void setEnergyStarPropertyUseId(String energyStarPropertyUseId) {
		this.energyStarPropertyUseId = energyStarPropertyUseId;
	}


	public String getEnergyStarDesignId() {
		return energyStarDesignId;
	}

	@JSON(serialize=false)
	public void setEnergyStarDesignId(String energyStarDesignId) {
		this.energyStarDesignId = energyStarDesignId;
	}


	public enum Building_Type {
		SCHOOL(1, "k-12 School","k12School"),
		OFFICE(2, "Office","office"),
		;

		int intVal;
		String name;
		String linkName;

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}
		
		public String getLinkName() {
			return linkName;
		}

		private Building_Type(int intVal, String name,String linkName) {
			this.intVal = intVal;
			this.name = name;
			this.linkName = linkName;
					
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

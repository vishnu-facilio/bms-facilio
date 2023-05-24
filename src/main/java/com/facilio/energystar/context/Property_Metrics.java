package com.facilio.energystar.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public enum Property_Metrics {
	
	SCORE(1, "score","Score","targetScore","medianScore"),
	SOURCE_EUI(2, "sourceIntensity","Source EUI","targetSourceIntensity","medianSourceIntensity"),	
	SITE_EUI(3, "siteIntensity","Site EUI","targetSiteIntensity","medianSiteIntensity"),		
	//TOTAL_GHG_EMISSION(4, "totalLocationBasedGHGEmissions","Total GHG Emissions","targetTotalLocationBasedGHGEmissions","medianTotalLocationBasedGHGEmissions"),
	COST(5, "energyCost","Energy Cost","targetEnergyCost","medianEnergyCost"),
	;

	int intVal;
	String name;
	String displayName;
	String targetFieldName;
	String medianFieldName;

	public int getIntVal() {
		return intVal;
	}

	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public FacilioField getField() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		return modBean.getField(getName(), EnergyStarUtil.ENERGY_STAR_PROPERTY_DATA_MODULE_NAME);
	}

	private Property_Metrics(int intVal, String name,String displayName,String targetFieldName,String medianFieldName) {
		this.intVal = intVal;
		this.name = name;
		this.displayName = displayName;
		this.targetFieldName = targetFieldName;
		this.medianFieldName = medianFieldName;
	}

	public String getTargetFieldName() {
		return targetFieldName;
	}

	public String getMedianFieldName() {
		return medianFieldName;
	}

	private static final Map<Integer, Property_Metrics> optionMap = Collections.unmodifiableMap(initTypeMap());
	
	private static final Map<String, Property_Metrics> nameMap = Collections.unmodifiableMap(initNameTypeMap());
	
	private static Map<Integer, Property_Metrics> initTypeMap() {
		Map<Integer, Property_Metrics> typeMap = new HashMap<>();

		for (Property_Metrics type : values()) {
			typeMap.put(type.getIntVal(), type);
		}
		return typeMap;
	}
	
	private static Map<String, Property_Metrics> initNameTypeMap() {
		Map<String, Property_Metrics> typeMap = new HashMap<>();

		for (Property_Metrics type : values()) {
			typeMap.put(type.getName(), type);
		}
		return typeMap;
	}

	public static Map<Integer, Property_Metrics> getAllMetrics() {
		return optionMap;
	}
	
	public static Map<String, Property_Metrics> getNameMap() {
		return nameMap;
	}

}

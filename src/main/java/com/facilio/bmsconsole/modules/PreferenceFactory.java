package com.facilio.bmsconsole.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.Preference;
import com.facilio.bmsconsole.util.ContractsAPI;
import com.facilio.constants.FacilioConstants;

public class PreferenceFactory {
	private final static Map<String, List<Preference>> map = new HashMap<String, List<Preference>>();
	private final static Map<String, List<Preference>> modulePrefMap = new HashMap<String, List<Preference>>();
	
	static {
		initializeMap();
		initializeModuleMap();
	}
	
	private static void initializeMap() {
		
		map.put(FacilioConstants.ContextNames.PURCHASE_CONTRACTS, getContractsPrefList());
	}
	
	private static void initializeModuleMap() {
		
	//	modulePrefMap.put(FacilioConstants.ContextNames.CONTRACTS, getContractsModulePrefList());
	}
	
	public static List<Preference> getAllPreferencesForModuleRecord(String moduleName) {
		return map.get(moduleName);
	}
	
	public static List<Preference> getAllPreferencesForModule(String moduleName) {
		return modulePrefMap.get(moduleName);
	}
	
	public static Preference getModuleRecordPreference(String moduleName, String name) {
		List<Preference> list = map.get(moduleName);
		if (CollectionUtils.isNotEmpty(list)) {
			for (Preference p : list) {
				if (p.getName().equals(name)) {
					return p;
				}
			}
		}
		return null;
	}
	
	public static Preference getModulePreference(String moduleName, String name) {
		List<Preference> list = modulePrefMap.get(moduleName);
		if (CollectionUtils.isNotEmpty(list)) {
			for (Preference p : list) {
				if (p.getName().equals(name)) {
					return p;
				}
			}
		}
		return null;
	}
	
	private static List<Preference> getContractsPrefList() {
		
		List<Preference> contractPreference = new ArrayList<Preference>();
		contractPreference.add(ContractsAPI.getExpiryNotificationPref());
		return contractPreference;
	}

}

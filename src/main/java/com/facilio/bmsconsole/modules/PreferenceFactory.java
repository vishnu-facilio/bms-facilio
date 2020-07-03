package com.facilio.bmsconsole.modules;

import java.util.*;

import com.facilio.bmsconsoleV3.util.QuotationAPI;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.bmsconsole.context.Preference;
import com.facilio.bmsconsole.util.ContractsAPI;
import com.facilio.bmsconsole.util.InventoryApi;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;

public class PreferenceFactory {
	private final static Map<String, List<Preference>> map = new HashMap<String, List<Preference>>();
	private final static Map<String, List<Preference>> modulePrefMap = new HashMap<String, List<Preference>>();
	private final static Map<String, Preference> orgPrefMap = new HashMap<String, Preference>();

	static {
		initializeMap();
		initializeModuleMap();
		initializeOrgPrefMap();
	}
	
	private static void initializeMap() {
		
		map.put(FacilioConstants.ContextNames.STORE_ROOM, getStoreRoomPrefList());
		map.put(FacilioConstants.ContextNames.PURCHASE_CONTRACTS, getContractsPrefList());
		map.put(FacilioConstants.ContextNames.LABOUR_CONTRACTS, getContractsPrefList());
		map.put(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS, getContractsPrefList());
		map.put(FacilioConstants.ContextNames.WARRANTY_CONTRACTS, getContractsPrefList());
	}

	private static void initializeOrgPrefMap() {
		orgPrefMap.put("taxApplication", QuotationAPI.getTaxPref());
	}

	private static void initializeModuleMap() {
		
	//	modulePrefMap.put(FacilioConstants.ContextNames.CONTRACTS, getContractsModulePrefList());
		modulePrefMap.put(FacilioConstants.ContextNames.VISITOR_LOGGING, getVisitorLogModulePrefList());
		modulePrefMap.put(FacilioConstants.ContextNames.WATCHLIST, getWatchListModulePrefList());
		modulePrefMap.put(FacilioConstants.ContextNames.VENDORS, getVendorModulePrefList());
		modulePrefMap.put(FacilioConstants.ContextNames.VISITOR, getVisitorLogGeneralPrefList());
			
	}
	
	public static List<Preference> getAllPreferencesForModuleRecord(String moduleName) {
		return map.get(moduleName);
	}
	
	public static List<Preference> getAllPreferencesForModule(String moduleName) {
		return modulePrefMap.get(moduleName);
	}

	public static Map<String, Preference> getAllPreferencesForOrg() {
		return orgPrefMap;
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

	public static Preference getOrgPreference(String name) {
		Preference pref = orgPrefMap.get(name);
		return pref;
	}
	
	private static List<Preference> getContractsPrefList() {
		
		List<Preference> contractPreference = new ArrayList<Preference>();
		contractPreference.add(ContractsAPI.getExpiryNotificationPref());
		contractPreference.add(ContractsAPI.getPaymentNotificationPref());
		contractPreference.add(ContractsAPI.getRenewalNotificationPref());
		return contractPreference;
	}
	
	private static List<Preference> getStoreRoomPrefList() {
		
		List<Preference> storeRoomPreference = new ArrayList<Preference>();
		storeRoomPreference.add(InventoryApi.getStoreRoomOutOfStockNotificationPref());
		storeRoomPreference.add(InventoryApi.getStoreRoomMinQtyNotificationPref());
		return storeRoomPreference;
	}
	
	private static List<Preference> getVisitorLogModulePrefList() {
		
		List<Preference> visitorLogPreferences = new ArrayList<Preference>();
		
		visitorLogPreferences.add(VisitorManagementAPI.getHostMailNotificationsPref());
		visitorLogPreferences.add(VisitorManagementAPI.getHostSmsNotificationsPref());
		
		visitorLogPreferences.add(VisitorManagementAPI.getWelcomeMailNotificationsPref());
		visitorLogPreferences.add(VisitorManagementAPI.getWelcomeSmsNotificationsPref());
		
		visitorLogPreferences.add(VisitorManagementAPI.getThanksMailNotificationsPref());
		visitorLogPreferences.add(VisitorManagementAPI.getThanksSmsNotificationsPref());
		
		visitorLogPreferences.add(VisitorManagementAPI.getInviteMailNotificationsPref());
		visitorLogPreferences.add(VisitorManagementAPI.getInviteSmsNotificationsPref());
		
		visitorLogPreferences.add(VisitorManagementAPI.getApprovalMailNotificationsPref());
		visitorLogPreferences.add(VisitorManagementAPI.getApprovalSmsNotificationsPref());
		
		if(AccountUtil.getCurrentOrg().getOrgId() == 155 || AccountUtil.getCurrentOrg().getOrgId() == 210) {
			visitorLogPreferences.add(VisitorManagementAPI.getWelcomeWhatsappNotificationsPref());
			visitorLogPreferences.add(VisitorManagementAPI.getThanksWhatsappNotificationsPref());
			visitorLogPreferences.add(VisitorManagementAPI.getInviteWhatsappNotificationsPref());
			visitorLogPreferences.add(VisitorManagementAPI.getHostWhatsappNotificationsPref());
			visitorLogPreferences.add(VisitorManagementAPI.getApprovalWhatsappNotificationsPref());
		}
		
		return visitorLogPreferences;
	}
	
	private static List<Preference> getWatchListModulePrefList() {
		
		List<Preference> visitorLogPreferences = new ArrayList<Preference>();
		
		visitorLogPreferences.add(VisitorManagementAPI.getBlockedMailNotificationPref());
		visitorLogPreferences.add(VisitorManagementAPI.getBlockedSmsNotificationPref());
//		
		visitorLogPreferences.add(VisitorManagementAPI.getVipVisitorMailNotificationPref());
		visitorLogPreferences.add(VisitorManagementAPI.getVipSmsNotificationPref());
		
		return visitorLogPreferences;
	}
	
	private static List<Preference> getVendorModulePrefList() {
		
		List<Preference> vendorPreferences = new ArrayList<Preference>();
		
		vendorPreferences.add(InventoryApi.getRegisterVendorMailNotificationsPref());
		return vendorPreferences;
	}
	
	private static List<Preference> getVisitorLogGeneralPrefList() {
		
		List<Preference> visitorLogGeneralPreferences = new ArrayList<Preference>();
		
		visitorLogGeneralPreferences.add(VisitorManagementAPI.getVisitorCheckOutPref());
		return visitorLogGeneralPreferences;
	}


}

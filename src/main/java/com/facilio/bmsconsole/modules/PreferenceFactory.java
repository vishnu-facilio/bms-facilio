package com.facilio.bmsconsole.modules;

import java.util.*;

import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
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
		orgPrefMap.put("discountApplication", QuotationAPI.getDiscountPref());
	}

	private static void initializeModuleMap() {
		
	//	modulePrefMap.put(FacilioConstants.ContextNames.CONTRACTS, getContractsModulePrefList());
		modulePrefMap.put(FacilioConstants.ContextNames.VISITOR_LOG, getVisitorLogModulePrefList());
		modulePrefMap.put(FacilioConstants.ContextNames.INVITE_VISITOR, getVisitorInviteModulePrefList());
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
		
		visitorLogPreferences.add(V3VisitorManagementAPI.getHostMailNotificationsPref());
		visitorLogPreferences.add(V3VisitorManagementAPI.getHostSmsNotificationsPref());
		
		visitorLogPreferences.add(V3VisitorManagementAPI.getWelcomeMailNotificationsPref());
		visitorLogPreferences.add(V3VisitorManagementAPI.getWelcomeSmsNotificationsPref());
		
		visitorLogPreferences.add(V3VisitorManagementAPI.getThanksMailNotificationsPref());
		visitorLogPreferences.add(V3VisitorManagementAPI.getThanksSmsNotificationsPref());
		
		visitorLogPreferences.add(V3VisitorManagementAPI.getInviteMailNotificationsPref());
		visitorLogPreferences.add(V3VisitorManagementAPI.getInviteSmsNotificationsPref());
		
		visitorLogPreferences.add(V3VisitorManagementAPI.getApprovalMailNotificationsPref());
		visitorLogPreferences.add(V3VisitorManagementAPI.getApprovalSmsNotificationsPref());
		
		if(AccountUtil.getCurrentOrg().getOrgId() == 155 || AccountUtil.getCurrentOrg().getOrgId() == 210) {
			visitorLogPreferences.add(V3VisitorManagementAPI.getWelcomeWhatsappNotificationsPref());
			visitorLogPreferences.add(V3VisitorManagementAPI.getThanksWhatsappNotificationsPref());
			visitorLogPreferences.add(V3VisitorManagementAPI.getInviteWhatsappNotificationsPref());
			visitorLogPreferences.add(V3VisitorManagementAPI.getHostWhatsappNotificationsPref());
			visitorLogPreferences.add(V3VisitorManagementAPI.getApprovalWhatsappNotificationsPref());
		}
		
		return visitorLogPreferences;
	}

	private static List<Preference> getVisitorInviteModulePrefList() {

		List<Preference> visitorInvitePreferences = new ArrayList<Preference>();

		visitorInvitePreferences.add(V3VisitorManagementAPI.getInviteMailNotificationsPref());
		visitorInvitePreferences.add(V3VisitorManagementAPI.getInviteSmsNotificationsPref());

		if(AccountUtil.getCurrentOrg().getOrgId() == 155 || AccountUtil.getCurrentOrg().getOrgId() == 210) {
			visitorInvitePreferences.add(V3VisitorManagementAPI.getInviteWhatsappNotificationsPref());
		}

		return visitorInvitePreferences;
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
		
		visitorLogGeneralPreferences.add(V3VisitorManagementAPI.getVisitorCheckOutPref());
		return visitorLogGeneralPreferences;
	}


}

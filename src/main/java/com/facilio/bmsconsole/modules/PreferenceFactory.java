package com.facilio.bmsconsole.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.text.StringSubstitutor;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.bmsconsole.context.Preference;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.util.ContractsAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.SingleRecordRuleAPI;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.ScheduledRuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;

public class PreferenceFactory {
	private final static Map<String, List<Preference>> map = new HashMap<String, List<Preference>>();
	private final static Map<String, List<Preference>> modulePrefMap = new HashMap<String, List<Preference>>();
	
	static {
		initializeMap();
		initializeModuleMap();
	}
	
	private static void initializeMap() {
		
		map.put(FacilioConstants.ContextNames.CONTRACTS, getContractsPrefList());
	}
	
	private static void initializeModuleMap() {
		
		modulePrefMap.put(FacilioConstants.ContextNames.CONTRACTS, getContractsModulePrefList());
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
private static List<Preference> getContractsModulePrefList() {
		
		List<Preference> contractPreference = new ArrayList<Preference>();
		contractPreference.add(ContractsAPI.getExpiryNotificationPref2());
		return contractPreference;
	}

}

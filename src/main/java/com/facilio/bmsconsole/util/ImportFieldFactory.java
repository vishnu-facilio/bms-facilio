package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.actions.ImportProcessContext.ImportSetting;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ImportFieldFactory {

	private static Logger LOGGER = Logger.getLogger(ImportFieldFactory.class.getName());
	
	String moduleName;
	
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public static List<FacilioField> getPurchasedToolImportFields() throws Exception{
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = getAllFields("purchasedTool");
		
		List<FacilioField> selectedFields = new ArrayList<FacilioField>();
		List<String> fieldsToBeRemoved = getFieldsTobeRemoved("purchasedTool");
		List<FacilioField> customFields = bean.getAllCustomFields("purchasedTool");
		
		for(FacilioField field : fields) {
			if(field.getModule().getName().equals("purchasedTool") && 
					!field.getName().equals("isUsed") && !field.getName().equals("sysModifiedTime") && !field.getName().equals("sysCreatedTime")) {
				selectedFields.add(field);
			}
			else if(field.getModule().getName().equals("purchasedTool") == false){
				if(fieldsToBeRemoved.contains(field.getName())) {
					continue;
				}
				else {
					selectedFields.add(field);
				}
				
			}
		}
		if(customFields != null && customFields.size() != 0) {
			selectedFields.addAll(customFields);
		}
		selectedFields = selectedFields.stream().filter(f -> !f.getName().equals("toolType") && !f.getName().equals("tool")).collect(Collectors.toList());
		return selectedFields;
	
	}
	
	public static List<FacilioField> getPurchasedItemImportFields() throws Exception{
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = getAllFields("purchasedItem");
		
		List<FacilioField> customFields =bean.getAllCustomFields("purchasedItem");
		
		List<FacilioField> selectedFields = new ArrayList<FacilioField>();
		List<String> fieldsToBeRemoved = getFieldsTobeRemoved("purchasedItem");
		for(FacilioField field : fields) {
			if(field.getModule().getName().equals("purchasedItem") && 
					!field.getName().equals("isUsed") && !field.getName().equals("currentQuantity") && !field.getName().equals("sysModifiedTime") && !field.getName().equals("sysCreatedTime")) {
				selectedFields.add(field);
			}
			else if(field.getModule().getName().equals("purchasedItem") == false){
				if(fieldsToBeRemoved.contains(field.getName())) {
					continue;
				}
				else {
					selectedFields.add(field);
				}
				
			}
		}
		if(customFields != null && customFields.size() != 0) {
			selectedFields.addAll(customFields);
		}
		
		selectedFields = selectedFields.stream().filter(f -> !f.getName().equals("itemType") && !f.getName().equals("item")).collect(Collectors.toList());
		return selectedFields;
		
	}
	
	public static List<FacilioField> getAllFields (String moduleName) throws Exception{
		LOGGER.severe("MODULENAME!!!" + moduleName);
		List<FacilioField> temp = new ArrayList<FacilioField>();
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		temp = bean.getAllFields(moduleName);
		
		List<FacilioField> fields = new ArrayList<FacilioField>(temp);
		
		for(FacilioField field : fields) {
			System.out.println(field.getName());
		}
		List<FacilioField> additionalFields = new ArrayList<FacilioField>();
		
		for(FacilioField field : fields) {
			if(field.getDataType() == FieldType.LOOKUP.getTypeAsInt() && !ImportAPI.isRemovableFieldOnImport(field.getName())) {
				LOGGER.severe("LookupField!!!" + field.getName());
				LookupField lookupField = (LookupField) field;
				LOGGER.severe(lookupField.getLookupModule().getName());
				List<FacilioField> lookupModuleFields = bean.getAllFields(lookupField.getLookupModule().getName());
				for(FacilioField lkField : lookupModuleFields) {
					additionalFields.add(lkField);
				}
			}
		}
		
		fields.addAll(additionalFields);
		return fields;
	}
	public static List<FacilioField> getImportFieldsAsList(String moduleName) throws Exception{
		List<FacilioField> fields = new ArrayList<FacilioField>();
		switch (moduleName) {
		case "purchasedItem":{
			fields = getPurchasedItemImportFields();
			break;
		}
		case "purchasedTool":{
			fields = getPurchasedToolImportFields();
			break;
		}
		}
		LOGGER.severe(fields.toString());
		return fields;
	}
	
	public static HashMap<String, FacilioField> getFacilioFieldMapping(String moduleName) throws Exception{
		HashMap<String, FacilioField> fieldMapping = new HashMap<String, FacilioField>();
		for(FacilioField field : getImportFieldsAsList(moduleName)) {
			fieldMapping.put(field.getName(), field);
		}
		return fieldMapping;
	}
	
	public static HashMap<String, String> getFieldMapping(String moduleName) throws Exception{
		HashMap<String, String> fieldMapping = new HashMap<String, String>();
		for(FacilioField field : getImportFieldsAsList(moduleName)) {
			fieldMapping.put(field.getName(), "-1");
		}
		return fieldMapping;
	}
	
	public static JSONArray getImportFieldNames(String moduleName) throws Exception{
		
		List<FacilioField> fields = getImportFieldsAsList(moduleName);
		JSONArray selectedFields = new JSONArray();
		for(FacilioField field : fields) {
			selectedFields.add(field.getName());
		}
		
		return selectedFields;
	}
	
	public static JSONObject getImportOptions(FacilioModule module){
		JSONObject options = new JSONObject();
		if(module.getName().equals(FacilioConstants.ContextNames.ASSET)
				|| (module.getExtendModule() != null && module.getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET))
				) {
			for(ImportSetting setting : ImportProcessContext.ImportSetting.values()) {
				options.put(setting.toString(), setting.getValue());
			}
		}
		else {
			options.put(ImportProcessContext.ImportSetting.INSERT.toString(),ImportProcessContext.ImportSetting.INSERT.getValue());
		}
		return options;
	}
	public static List<String> getFieldsTobeRemoved(String moduleName){
		List<String> removedFields= new ArrayList<String>();
		switch (moduleName) {
		case "purchasedItem":
		{
			removedFields.add("quantity");
			removedFields.add("currentQuantity");
			removedFields.add("localId");
			removedFields.add("status");
			removedFields.add("storeRoom");
			removedFields.add("lastPurchasedDate");
			removedFields.add("lastPurchasedPrice");
			removedFields.add("unit");
			removedFields.add("serialNumber");
			removedFields.add("minimumQuantity");
			removedFields.add("photoId");
			removedFields.add("individualTracking");
			removedFields.add("sysCreatedTime");
			removedFields.add("sysModifiedTime");
			removedFields.add("lastIssuedDate");
			removedFields.add("isApprovalNeeded");
		}
		case "purchasedTool": 
		{
			removedFields.add("currentQuantity");
			removedFields.add("localId");
			removedFields.add("rate");
			removedFields.add("status");
			removedFields.add("storeRoom");
			removedFields.add("lastPurchasedDate");
			removedFields.add("lastPurchasedPrice");
			removedFields.add("unit");
			removedFields.add("serialNumber");
			removedFields.add("minimumQuantity");
			removedFields.add("photoId");
			removedFields.add("individualTracking");
			removedFields.add("sysCreatedTime");
			removedFields.add("sysModifiedTime");
			removedFields.add("lastIssuedDate");
			removedFields.add("isApprovalNeeded");
		}
		}
		return removedFields;
	}
}

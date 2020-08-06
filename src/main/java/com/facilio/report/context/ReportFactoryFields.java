package com.facilio.report.context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.report.context.ReportFactory.Alarm;
import com.facilio.report.context.ReportFactory.ModuleType;
import com.facilio.report.context.ReportFactory.ReportFacilioField;
import com.facilio.report.context.ReportFactory.WorkOrder;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportFactoryFields {
	
	public static JSONObject getDefaultReportFields(String moduleName) throws Exception{
		ModuleBean bean = (ModuleBean)BeanFactory.lookup("ModuleBean");
		FacilioModule facilioModule = bean.getModule(moduleName);
		Map<String, FacilioField> fields = FieldFactory.getAsMap(bean.getAllFields(moduleName));
		Map<String, FacilioField> customFields = new HashMap<String, FacilioField>();
		if(bean.getAllCustomFields(moduleName) != null) {
			customFields = FieldFactory.getAsMap(bean.getAllCustomFields(moduleName));
		}
		List<FacilioField> selectedFields = new ArrayList<FacilioField>();
		HashMap<String,String> lookUpModuleNames = new HashMap<String,String>();
		
		for(String fieldName: fields.keySet()) {
			FacilioField field = fields.get(fieldName);
			if(field.getDataType() == FieldType.LOOKUP.getTypeAsInt()) {
				LookupField lookupField = (LookupField) field;
				if(lookupField.getLookupModule().getTypeEnum() != FacilioModule.ModuleType.PICK_LIST && !"users".equalsIgnoreCase(lookupField.getLookupModule().getName())) {
				if(!lookUpModuleNames.containsKey(lookupField.getDisplayName())) {
					lookUpModuleNames.put(lookupField.getDisplayName(),lookupField.getLookupModule().getName());
				}
				}
				else {
					selectedFields.add(fields.get(fieldName));
				}
			}
			else {
				selectedFields.add(fields.get(fieldName));
			}
	}
		
//		if(customFields.size() != 0 && !(facilioModule.isCustom() )) {
//			for(String customFieldName: customFields.keySet()) {
//				FacilioField customField = fields.get(customFieldName);
//				if(customField.getDataType() == FieldType.LOOKUP.getTypeAsInt()) {
//					LookupField lookupField = (LookupField) customField;
//					if(lookupField.getLookupModule().getTypeEnum() != FacilioModule.ModuleType.PICK_LIST && !"user".equalsIgnoreCase(lookupField.getLookupModule().getName())) {
//					if(!lookUpModuleNames.containsKey(lookupField.getDisplayName())) {
//						lookUpModuleNames.put(lookupField.getDisplayName(),lookupField.getLookupModule().getName());
//					}
//					}
//					else {
//						selectedFields.add(customFields.get(customFieldName));
//					}
//				}
//				else {
//					selectedFields.add(customFields.get(customFieldName));
//				}
//			}
//		}
		if(facilioModule.isCustom()) {
			selectedFields.addAll(FieldFactory.getSystemPointFields(facilioModule));
		}
		
		
		// loading additional module fields
		JSONObject rearrangedFields = rearrangeFields(selectedFields, facilioModule);
		setDefaultAdditionalModulemap(rearrangedFields, lookUpModuleNames, bean);
		HashMap<String , List<FacilioField>> additionalModuleFields = getAdditionalModuleFields(moduleName,lookUpModuleNames, bean);
		
		Map<String, List<FacilioField>> dimensionFieldMap = (Map<String, List<FacilioField>>)rearrangedFields.get("dimension");
		
		ArrayList<String> dimensionListOrder = new ArrayList<String>();
		if(dimensionFieldMap.get("time") != null) {
		dimensionListOrder.add("time");
		}
		dimensionListOrder.add(facilioModule.getDisplayName());
		
		for(String module:lookUpModuleNames.keySet()) {
			dimensionFieldMap.put(module, getDimensionLookupFields((List<FacilioField>) additionalModuleFields.get(lookUpModuleNames.get(module))));
			dimensionListOrder.add(module);
		}
		
		
		rearrangedFields.put("dimensionListOrder", dimensionListOrder);
		
		rearrangedFields.put("displayName",facilioModule.getDisplayName());
		
		return rearrangedFields;
	}
	
	public static JSONObject getworkorderReportFields() throws Exception{
		ModuleBean bean = (ModuleBean)BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fields = FieldFactory.getAsMap(bean.getAllFields("workorder"));
		Map<String, FacilioField> customFields = new HashMap<String, FacilioField>();
		if(bean.getAllCustomFields("workorder") != null) {
			customFields = FieldFactory.getAsMap(bean.getAllCustomFields("workorder"));
		}
		List<FacilioField> selectedFields = new ArrayList<FacilioField>();
		
		selectedFields.add(fields.get("createdTime"));
		selectedFields.add(fields.get("dueDate"));
		selectedFields.add(fields.get("actualWorkStart"));
		selectedFields.add(fields.get("actualWorkEnd"));
		selectedFields.add(fields.get("actualWorkDuration"));
		selectedFields.add(fields.get("priority"));
		selectedFields.add(fields.get("category"));
		selectedFields.add(fields.get("assignedTo"));
		selectedFields.add(fields.get("assignmentGroup"));
		selectedFields.add(fields.get("type"));
		selectedFields.add(fields.get("sourceType"));
		selectedFields.add(fields.get("totalCost"));
		selectedFields.add(fields.get("status"));
		selectedFields.add(fields.get("requestedBy"));
		if(AccountUtil.isFeatureEnabled(FeatureLicense.TENANTS)) {
			selectedFields.add(fields.get("tenant"));
		}
		if(AccountUtil.isFeatureEnabled(FeatureLicense.INVENTORY)) {
			selectedFields.add(fields.get("vendor"));
		}
		
		if(customFields.size() != 0) {
			for(String customFieldName: customFields.keySet()) {
				selectedFields.add(customFields.get(customFieldName));
			}
		}
		
		// loading additional module fields
		JSONObject rearrangedFields = rearrangeFields(selectedFields, "workorder");
		setAdditionalModulemap(rearrangedFields, "workorder", bean);
		HashMap<String , Map<String, FacilioField>> additionalModuleFields = getAdditionalModuleFields("workorder", bean);
		
	
		List<FacilioField> assetFields = new ArrayList<FacilioField>();
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("name"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("category"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("type"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("department"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("state"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("unitPrice"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("purchasedDate"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("retireDate"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("warrantyExpiryDate"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("rotatingTool"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("rotatingItem"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("lastIssuedToUser"));
		
		List<FacilioField> spaceFields = new ArrayList<FacilioField>();
		spaceFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.SPACE).get("spaceCategory"));
		
		Map<String, List<FacilioField>> dimensionFieldMap = (Map<String, List<FacilioField>>)rearrangedFields.get("dimension");

		
		dimensionFieldMap.put(FacilioConstants.ContextNames.ASSET, assetFields);
		dimensionFieldMap.put(FacilioConstants.ContextNames.SPACE, spaceFields);
		
		ArrayList<String> dimensionListOrder = new ArrayList<String>();
		dimensionListOrder.add("time");
		dimensionListOrder.add("workorder");
		dimensionListOrder.add("asset");
		
		rearrangedFields.put("dimensionListOrder", dimensionListOrder);
		
		rearrangedFields.put("displayName","workorders");
		
		return rearrangedFields;
	}
	
	public static JSONObject getassetReportFields() throws Exception{
		ModuleBean bean = (ModuleBean)BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fields = FieldFactory.getAsMap(bean.getAllFields("asset"));
		
		
		Map<String, FacilioField> customFields = new HashMap<String, FacilioField>();
		if(bean.getAllCustomFields("asset") != null) {
			customFields = FieldFactory.getAsMap(bean.getAllCustomFields("asset"));
		}
		
		List<FacilioField> selectedFields = new ArrayList<FacilioField>();
		selectedFields.add(fields.get("type"));
		selectedFields.add(fields.get("category"));
		selectedFields.add(fields.get("department"));
		selectedFields.add(fields.get("state"));
		selectedFields.add(fields.get("unitPrice"));
		selectedFields.add(fields.get("purchasedDate"));
		selectedFields.add(fields.get("retireDate"));
		selectedFields.add(fields.get("warrantyExpiryDate"));
		selectedFields.add(fields.get("rotatingTool"));
		selectedFields.add(fields.get("rotatingItem"));
		selectedFields.add(fields.get("lastIssuedToUser"));
		
		
		if(customFields.size() != 0) {
			for(String customFieldName: customFields.keySet()) {
				selectedFields.add(customFields.get(customFieldName));
			}
		}
		
		
		JSONObject rearrangedFields = rearrangeFields(selectedFields, "asset");
		
		ArrayList<String> dimensionListOrder = new ArrayList<String>();
		dimensionListOrder.add("time");
		dimensionListOrder.add("asset");
		
		rearrangedFields.put("dimensionListOrder", dimensionListOrder);
		
		rearrangedFields.put("displayName","assets");
		
		return rearrangedFields;
		
	}
	
	public static JSONObject getitemReportFields() throws Exception{
		ModuleBean bean = (ModuleBean)BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fields = FieldFactory.getAsMap(bean.getAllFields("item"));
		
		
		Map<String, FacilioField> customFields = new HashMap<String, FacilioField>();
		if(bean.getAllCustomFields("item") != null) {
			customFields = FieldFactory.getAsMap(bean.getAllCustomFields("item"));
		}
		
		List<FacilioField> selectedFields = new ArrayList<FacilioField>();
		selectedFields.add(fields.get("status"));
		selectedFields.add(fields.get("costType"));
		selectedFields.add(fields.get("quantity"));
		selectedFields.add(fields.get("localId"));
		selectedFields.add(fields.get("sysCreatedTime"));
		selectedFields.add(fields.get("sysModifiedTime"));
		selectedFields.add(fields.get("lastPurchasedDate"));
		selectedFields.add(fields.get("lastPurchasedPrice"));
		selectedFields.add(fields.get("minimumQuantity"));
		
		if(customFields.size() != 0) {
			for(String customFieldName: customFields.keySet()) {
				selectedFields.add(customFields.get(customFieldName));
			}
		}
		
		//loading additional module fields
		JSONObject rearrangedFields = rearrangeFields(selectedFields, "item");
		setAdditionalModulemap(rearrangedFields, "item", bean);
		HashMap<String , Map<String, FacilioField>> additionalModuleFields = getAdditionalModuleFields("item", bean);
				
			
		List<FacilioField> itemTypeFields = new ArrayList<FacilioField>();
		itemTypeFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM_TYPES).get("name"));
		itemTypeFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM_TYPES).get("description"));
		itemTypeFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM_TYPES).get("category"));
		itemTypeFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM_TYPES).get("status"));
		itemTypeFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM_TYPES).get("serialNumber"));
		itemTypeFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM_TYPES).get("unit"));
		itemTypeFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM_TYPES).get("currentQuantity"));
		itemTypeFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM_TYPES).get("minimumQuantity"));
		
		List<FacilioField> storeRoomFields = new ArrayList<FacilioField>();
		storeRoomFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.STORE_ROOM).get("name"));
		storeRoomFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.STORE_ROOM).get("description"));
		storeRoomFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.STORE_ROOM).get("location"));
		storeRoomFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.STORE_ROOM).get("owner"));
		storeRoomFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.STORE_ROOM).get("site"));
		storeRoomFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.STORE_ROOM).get("noOfItemTypes"));
		storeRoomFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.STORE_ROOM).get("noOfToolTypes"));
		
		Map<String, List<FacilioField>> dimensionFieldMap = (Map<String, List<FacilioField>>)rearrangedFields.get("dimension");

		
		dimensionFieldMap.put(FacilioConstants.ModuleNames.ITEM_TYPES, itemTypeFields);
		dimensionFieldMap.put(FacilioConstants.ModuleNames.STORE_ROOM, storeRoomFields);
		
		ArrayList<String> dimensionListOrder = new ArrayList<String>();
		dimensionListOrder.add("time");
		dimensionListOrder.add("item");
		dimensionListOrder.add("itemtypes");
		dimensionListOrder.add("storeroom");
		
		rearrangedFields.put("dimensionListOrder", dimensionListOrder);
		
		rearrangedFields.put("displayName","items");
		
		return rearrangedFields;
		
	}
	
	public static JSONObject gettoolReportFields() throws Exception{
		ModuleBean bean = (ModuleBean)BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fields = FieldFactory.getAsMap(bean.getAllFields("tool"));
		
		
		Map<String, FacilioField> customFields = new HashMap<String, FacilioField>();
		if(bean.getAllCustomFields("tool") != null) {
			customFields = FieldFactory.getAsMap(bean.getAllCustomFields("tool"));
		}
		
		List<FacilioField> selectedFields = new ArrayList<FacilioField>();
		selectedFields.add(fields.get("status"));
		selectedFields.add(fields.get("quantity"));
		selectedFields.add(fields.get("currentQuantity"));
		selectedFields.add(fields.get("rate"));
		selectedFields.add(fields.get("localId"));
		selectedFields.add(fields.get("sysCreatedTime"));
		selectedFields.add(fields.get("sysModifiedTime"));
		selectedFields.add(fields.get("lastPurchasedDate"));
		
		if(customFields.size() != 0) {
			for(String customFieldName: customFields.keySet()) {
				selectedFields.add(customFields.get(customFieldName));
			}
		}
		
		//loading additional module fields
		JSONObject rearrangedFields = rearrangeFields(selectedFields, "tool");
		setAdditionalModulemap(rearrangedFields, "tool", bean);
		HashMap<String , Map<String, FacilioField>> additionalModuleFields = getAdditionalModuleFields("tool", bean);
				
			
		List<FacilioField> toolTypeFields = new ArrayList<FacilioField>();
		toolTypeFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL_TYPES).get("name"));
		toolTypeFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL_TYPES).get("description"));
		toolTypeFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL_TYPES).get("category"));
		toolTypeFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL_TYPES).get("status"));
		toolTypeFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL_TYPES).get("serialNumber"));
		toolTypeFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL_TYPES).get("unit"));
		toolTypeFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL_TYPES).get("currentQuantity"));
				
		List<FacilioField> storeRoomFields = new ArrayList<FacilioField>();
		storeRoomFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.STORE_ROOM).get("name"));
		storeRoomFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.STORE_ROOM).get("description"));
		storeRoomFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.STORE_ROOM).get("location"));
		storeRoomFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.STORE_ROOM).get("owner"));
		storeRoomFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.STORE_ROOM).get("site"));
		storeRoomFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.STORE_ROOM).get("noOfItemTypes"));
		storeRoomFields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.STORE_ROOM).get("noOfToolTypes"));
		
		Map<String, List<FacilioField>> dimensionFieldMap = (Map<String, List<FacilioField>>)rearrangedFields.get("dimension");

		
		dimensionFieldMap.put(FacilioConstants.ModuleNames.TOOL_TYPES, toolTypeFields);
		dimensionFieldMap.put(FacilioConstants.ModuleNames.STORE_ROOM, storeRoomFields);
		
		ArrayList<String> dimensionListOrder = new ArrayList<String>();
		dimensionListOrder.add("time");
		dimensionListOrder.add("tool");
		dimensionListOrder.add("tooltypes");
		dimensionListOrder.add("storeroom");
		
		rearrangedFields.put("dimensionListOrder", dimensionListOrder);
		
		rearrangedFields.put("displayName","tools");
		
		return rearrangedFields;
		
	}
	
	public static JSONObject getassetDownTimeReportFields() throws Exception{
		ModuleBean bean = (ModuleBean)BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fields = FieldFactory.getAsMap(bean.getAllFields(FacilioConstants.ModuleNames.ASSET_BREAKDOWN));
		
		
		Map<String, FacilioField> customFields = new HashMap<String, FacilioField>();
		if(bean.getAllCustomFields(FacilioConstants.ModuleNames.ASSET_BREAKDOWN) != null) {
			customFields = FieldFactory.getAsMap(bean.getAllCustomFields(FacilioConstants.ModuleNames.ASSET_BREAKDOWN));
		}
		
		List<FacilioField> selectedFields = new ArrayList<FacilioField>();
		selectedFields.add(fields.get("fromtime"));
		selectedFields.add(fields.get("totime"));
		selectedFields.add(fields.get("duration"));
		selectedFields.add(fields.get("timeBetweenFailure"));
		
		if(customFields.size() != 0) {
			for(String customFieldName: customFields.keySet()) {
				selectedFields.add(customFields.get(customFieldName));
			}
		}
		
		//loading additional module fields
		JSONObject rearrangedFields = rearrangeFields(selectedFields, FacilioConstants.ModuleNames.ASSET_BREAKDOWN);
		setAdditionalModulemap(rearrangedFields, FacilioConstants.ModuleNames.ASSET_BREAKDOWN, bean);
		HashMap<String , Map<String, FacilioField>> additionalModuleFields = getAdditionalModuleFields(FacilioConstants.ModuleNames.ASSET_BREAKDOWN, bean);
				
			
		List<FacilioField> assetFields = new ArrayList<FacilioField>();
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("name"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("category"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("type"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("department"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("state"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("unitPrice"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("purchasedDate"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("retireDate"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("warrantyExpiryDate"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("rotatingTool"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("rotatingItem"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("lastIssuedToUser"));
		
		Map<String, List<FacilioField>> dimensionFieldMap = (Map<String, List<FacilioField>>)rearrangedFields.get("dimension");

		
		dimensionFieldMap.put(FacilioConstants.ContextNames.ASSET, assetFields);
		
		ArrayList<String> dimensionListOrder = new ArrayList<String>();
		dimensionListOrder.add("time");
		dimensionListOrder.add(FacilioConstants.ModuleNames.ASSET_BREAKDOWN);
		dimensionListOrder.add("asset");
		
		rearrangedFields.put("dimensionListOrder", dimensionListOrder);
		
		rearrangedFields.put("displayName","breakdowns");
		
		return rearrangedFields;
		
	}
	
	public static JSONObject getitemTransactionsReportFields() throws Exception{
		ModuleBean bean = (ModuleBean)BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fields = FieldFactory.getAsMap(bean.getAllFields(FacilioConstants.ModuleNames.ITEM_TRANSACTIONS));
		
		
		Map<String, FacilioField> customFields = new HashMap<String, FacilioField>();
		if(bean.getAllCustomFields(FacilioConstants.ModuleNames.ITEM_TRANSACTIONS) != null) {
			customFields = FieldFactory.getAsMap(bean.getAllCustomFields(FacilioConstants.ModuleNames.ITEM_TRANSACTIONS));
		}
		
		List<FacilioField> selectedFields = new ArrayList<FacilioField>();

		selectedFields.add(fields.get("sysModifiedTime"));
		selectedFields.add(fields.get("sysCreatedTime"));
		selectedFields.add(fields.get("remainingQuantity"));
		selectedFields.add(fields.get("shipment"));
		selectedFields.add(fields.get("quantity"));
		selectedFields.add(fields.get("approvedState"));
		selectedFields.add(fields.get("transactionCost"));
		selectedFields.add(fields.get("parentId"));
		selectedFields.add(fields.get("parentTransactionId"));
		selectedFields.add(fields.get("transactionType"));
		selectedFields.add(fields.get("transactionState"));
		selectedFields.add(fields.get("issuedTo"));
		
		if(customFields.size() != 0) {
			for(String customFieldName: customFields.keySet()) {
				selectedFields.add(customFields.get(customFieldName));
			}
		}
		
		//loading additional module fields
		JSONObject rearrangedFields = rearrangeFields(selectedFields, FacilioConstants.ModuleNames.ITEM_TRANSACTIONS);
		setAdditionalModulemap(rearrangedFields, FacilioConstants.ModuleNames.ITEM_TRANSACTIONS, bean);
		HashMap<String , Map<String, FacilioField>> additionalModuleFields = getAdditionalModuleFields(FacilioConstants.ModuleNames.ITEM_TRANSACTIONS, bean);
		
		List<FacilioField> assetFields = new ArrayList<FacilioField>();
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("name"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("category"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("type"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("department"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("state"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("unitPrice"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("purchasedDate"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("retireDate"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("warrantyExpiryDate"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("serialNumber"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("rotatingTool"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("rotatingItem"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("lastIssuedToUser"));
		
		List <FacilioField> workorderfields = new ArrayList<FacilioField>();
		
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("createdTime"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("dueDate"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("actualWorkStart"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("actualWorkEnd"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("actualWorkDuration"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("priority"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("category"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("assignedTo"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("assignmentGroup"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("type"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("sourceType"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("totalCost"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("status"));
		
//		List <FacilioField> gatepassfields = new ArrayList<FacilioField>();
//		for(String fieldName: additionalModuleFields.get(FacilioConstants.ModuleNames.GATE_PASS).keySet()) {
//			gatepassfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.GATE_PASS).get(fieldName));
//		}
//		
		
		List <FacilioField> itemfields = new ArrayList<FacilioField>();
		for(String fieldName: additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM).keySet()) {
			itemfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM).get("status"));
		}
		itemfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM).get("status"));
		itemfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM).get("costType"));
		itemfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM).get("quantity"));
		itemfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM).get("localId"));
		itemfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM).get("sysCreatedTime"));
		itemfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM).get("sysModifiedTime"));
		itemfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM).get("lastPurchasedDate"));
		itemfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM).get("lastPurchasedPrice"));
		itemfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM).get("minimumQuantity"));
		
		List <FacilioField> itemtypefields = new ArrayList<FacilioField>();

		itemtypefields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM_TYPES).get("name"));
		itemtypefields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM_TYPES).get("description"));
		itemtypefields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM_TYPES).get("category"));
		itemtypefields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM_TYPES).get("status"));
		itemtypefields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM_TYPES).get("serialNumber"));
		itemtypefields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM_TYPES).get("unit"));
		itemtypefields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM_TYPES).get("currentQuantity"));
		itemtypefields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.ITEM_TYPES).get("minimumQuantity"));
		
		List <FacilioField> purchaseditemfields = new ArrayList<FacilioField>();
		for(String fieldName: additionalModuleFields.get(FacilioConstants.ModuleNames.PURCHASED_ITEM).keySet()) {
			purchaseditemfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.PURCHASED_ITEM).get(fieldName));
		}
		
//		List <FacilioField> requestedlineitemfields = new ArrayList<FacilioField>();
//		for(String fieldName: additionalModuleFields.get(FacilioConstants.ModuleNames.REQUEST_LINE_ITEM).keySet()) {
//			requestedlineitemfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.REQUEST_LINE_ITEM).get(fieldName));
//		}
				
		
		Map<String, List<FacilioField>> dimensionFieldMap = (Map<String, List<FacilioField>>)rearrangedFields.get("dimension");

		dimensionFieldMap.put(FacilioConstants.ContextNames.ASSET, assetFields);
		dimensionFieldMap.put(FacilioConstants.ContextNames.WORK_ORDER, workorderfields);
//		dimensionFieldMap.put(FacilioConstants.ModuleNames.GATE_PASS, gatepassfields);
		dimensionFieldMap.put(FacilioConstants.ModuleNames.ITEM, itemfields);
		dimensionFieldMap.put(FacilioConstants.ModuleNames.ITEM_TYPES, itemtypefields);
		dimensionFieldMap.put(FacilioConstants.ModuleNames.PURCHASED_ITEM, purchaseditemfields);
//		dimensionFieldMap.put(FacilioConstants.ModuleNames.REQUEST_LINE_ITEM, requestedlineitemfields);
		
		ArrayList<String> dimensionListOrder = new ArrayList<String>();
		dimensionListOrder.add("time");
		dimensionListOrder.add(FacilioConstants.ModuleNames.ITEM_TRANSACTIONS);
		dimensionListOrder.add(FacilioConstants.ContextNames.ASSET);
		dimensionListOrder.add(FacilioConstants.ContextNames.WORK_ORDER);
		dimensionListOrder.add(FacilioConstants.ModuleNames.GATE_PASS);
		dimensionListOrder.add(FacilioConstants.ModuleNames.ITEM);
		dimensionListOrder.add(FacilioConstants.ModuleNames.ITEM_TYPES);
		dimensionListOrder.add(FacilioConstants.ModuleNames.PURCHASED_ITEM);
		dimensionListOrder.add(FacilioConstants.ModuleNames.REQUEST_LINE_ITEM);
		
		
		rearrangedFields.put("dimensionListOrder", dimensionListOrder);
		
		rearrangedFields.put("displayName","item transactions");
		
		return rearrangedFields;
		
	}
	
	public static JSONObject gettoolTransactionsReportFields() throws Exception{
		ModuleBean bean = (ModuleBean)BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fields = FieldFactory.getAsMap(bean.getAllFields(FacilioConstants.ModuleNames.TOOL_TRANSACTIONS));
		
		
		Map<String, FacilioField> customFields = new HashMap<String, FacilioField>();
		if(bean.getAllCustomFields(FacilioConstants.ModuleNames.TOOL_TRANSACTIONS) != null) {
			customFields = FieldFactory.getAsMap(bean.getAllCustomFields(FacilioConstants.ModuleNames.TOOL_TRANSACTIONS));
		}
		
		List<FacilioField> selectedFields = new ArrayList<FacilioField>();

		selectedFields.add(fields.get("sysModifiedTime"));
		selectedFields.add(fields.get("sysCreatedTime"));
		selectedFields.add(fields.get("remainingQuantity"));
		selectedFields.add(fields.get("shipment"));
		selectedFields.add(fields.get("quantity"));
		selectedFields.add(fields.get("approvedState"));
		selectedFields.add(fields.get("transactionCost"));
		selectedFields.add(fields.get("parentId"));
		selectedFields.add(fields.get("parentTransactionId"));
		selectedFields.add(fields.get("transactionType"));
		selectedFields.add(fields.get("transactionState"));
		selectedFields.add(fields.get("issuedTo"));
		
		if(customFields.size() != 0) {
			for(String customFieldName: customFields.keySet()) {
				selectedFields.add(customFields.get(customFieldName));
			}
		}
		
		//loading additional module fields
		JSONObject rearrangedFields = rearrangeFields(selectedFields, FacilioConstants.ModuleNames.TOOL_TRANSACTIONS);
		setAdditionalModulemap(rearrangedFields, FacilioConstants.ModuleNames.TOOL_TRANSACTIONS, bean);
		HashMap<String , Map<String, FacilioField>> additionalModuleFields = getAdditionalModuleFields(FacilioConstants.ModuleNames.TOOL_TRANSACTIONS, bean);
		
		List<FacilioField> assetFields = new ArrayList<FacilioField>();
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("name"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("category"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("type"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("department"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("state"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("unitPrice"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("purchasedDate"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("retireDate"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("warrantyExpiryDate"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("serialNumber"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("rotatingTool"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("rotatingItem"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("lastIssuedToUser"));
		
		List <FacilioField> workorderfields = new ArrayList<FacilioField>();
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("createdTime"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("dueDate"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("actualWorkStart"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("actualWorkEnd"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("actualWorkDuration"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("priority"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("category"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("assignedTo"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("assignmentGroup"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("type"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("sourceType"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("totalCost"));
		workorderfields.add(additionalModuleFields.get(FacilioConstants.ContextNames.WORK_ORDER).get("status"));
		
//		List <FacilioField> gatepassfields = new ArrayList<FacilioField>();
//		for(String fieldName: additionalModuleFields.get(FacilioConstants.ModuleNames.GATE_PASS).keySet()) {
//			gatepassfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.GATE_PASS).get(fieldName));
//		}
		
		List <FacilioField> toolfields = new ArrayList<FacilioField>();
		toolfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL).get("status"));
		toolfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL).get("quantity"));
		toolfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL).get("currentQuantity"));
		toolfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL).get("rate"));
		toolfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL).get("localId"));
		toolfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL).get("sysCreatedTime"));
		toolfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL).get("sysModifiedTime"));
		toolfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL).get("lastPurchasedDate"));
		
		List <FacilioField> tooltypefields = new ArrayList<FacilioField>();
		tooltypefields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL_TYPES).get("name"));
		tooltypefields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL_TYPES).get("description"));
		tooltypefields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL_TYPES).get("category"));
		tooltypefields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL_TYPES).get("status"));
		tooltypefields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL_TYPES).get("serialNumber"));
		tooltypefields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL_TYPES).get("unit"));
		tooltypefields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.TOOL_TYPES).get("currentQuantity"));
		
		List <FacilioField> purchasedtoolfields = new ArrayList<FacilioField>();
		for(String fieldName: additionalModuleFields.get(FacilioConstants.ModuleNames.PURCHASED_TOOL).keySet()) {
			purchasedtoolfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.PURCHASED_TOOL).get(fieldName));
		}
		
//		List <FacilioField> requestedlineitemfields = new ArrayList<FacilioField>();
//		for(String fieldName: additionalModuleFields.get(FacilioConstants.ModuleNames.REQUEST_LINE_ITEM).keySet()) {
//			requestedlineitemfields.add(additionalModuleFields.get(FacilioConstants.ModuleNames.REQUEST_LINE_ITEM).get(fieldName));
//		}
		
		
		Map<String, List<FacilioField>> dimensionFieldMap = (Map<String, List<FacilioField>>)rearrangedFields.get("dimension");

		dimensionFieldMap.put(FacilioConstants.ContextNames.ASSET, assetFields);
		dimensionFieldMap.put(FacilioConstants.ContextNames.WORK_ORDER, workorderfields);
//		dimensionFieldMap.put(FacilioConstants.ModuleNames.GATE_PASS, gatepassfields);
		dimensionFieldMap.put(FacilioConstants.ModuleNames.TOOL, toolfields);
		dimensionFieldMap.put(FacilioConstants.ModuleNames.TOOL_TYPES, tooltypefields);
		dimensionFieldMap.put(FacilioConstants.ModuleNames.PURCHASED_TOOL, purchasedtoolfields);
//		dimensionFieldMap.put(FacilioConstants.ModuleNames.REQUEST_LINE_ITEM, requestedlineitemfields);
		
		ArrayList<String> dimensionListOrder = new ArrayList<String>();
		dimensionListOrder.add("time");
		dimensionListOrder.add(FacilioConstants.ModuleNames.TOOL_TRANSACTIONS);
		dimensionListOrder.add(FacilioConstants.ContextNames.ASSET);
		dimensionListOrder.add(FacilioConstants.ContextNames.WORK_ORDER);
		dimensionListOrder.add(FacilioConstants.ModuleNames.GATE_PASS);
		dimensionListOrder.add(FacilioConstants.ModuleNames.TOOL);
		dimensionListOrder.add(FacilioConstants.ModuleNames.TOOL_TYPES);
		dimensionListOrder.add(FacilioConstants.ModuleNames.PURCHASED_TOOL);
		dimensionListOrder.add(FacilioConstants.ModuleNames.REQUEST_LINE_ITEM);
		
		rearrangedFields.put("dimensionListOrder", dimensionListOrder);
		
		rearrangedFields.put("displayName","tool transactions");
		
		return rearrangedFields;
		
	}
	
	public static JSONObject getAlarmReportFields() throws Exception{
		String moduleName = new String("alarm");
		if(AccountUtil.isFeatureEnabled(FeatureLicense.NEW_ALARMS)) {
			moduleName = new String("alarmoccurrence");
		}
		ModuleBean bean = (ModuleBean)BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fields = FieldFactory.getAsMap(bean.getAllFields(moduleName));
		
		List<FacilioField> selectedFields = new ArrayList<FacilioField>();
		
		Map<String, FacilioField> customFields = new HashMap<String, FacilioField>();
		if(bean.getAllCustomFields(moduleName) != null) {
			customFields = FieldFactory.getAsMap(bean.getAllCustomFields(moduleName));
		}
		
		selectedFields.add(fields.get("createdTime"));
		selectedFields.add(fields.get("modifiedTime"));
		selectedFields.add(fields.get("clearedTime"));
		selectedFields.add(fields.get("acknowledgedTime"));
		selectedFields.add(fields.get("acknowledgedBy"));
		selectedFields.add(fields.get("severity"));
		selectedFields.add(fields.get("isWoCreated"));
		
		if(customFields.size() != 0) {
			for(String customFieldName: customFields.keySet()) {
				selectedFields.add(customFields.get(customFieldName));
			}
		}
		
		// loading additional module fields
				JSONObject rearrangedFields = rearrangeFields(selectedFields, moduleName);
				HashMap<String , Map<String, FacilioField>> additionalModuleFields = getAdditionalModuleFields(moduleName, bean);
				
				setAdditionalModulemap(rearrangedFields, moduleName, bean);
				
				List<FacilioField> assetFields = new ArrayList<FacilioField>();
				assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("name"));
				assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("category"));
				assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("type"));
				assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("department"));
				assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("state"));
				assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("unitPrice"));
				assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("purchasedDate"));
				assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("retireDate"));
				assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("warrantyExpiryDate"));
				assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("rotatingTool"));
				assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("rotatingItem"));
				assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("lastIssuedToUser"));
				
				
				List<FacilioField> spaceFields = new ArrayList<FacilioField>();
				spaceFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.SPACE).get("spaceCategory"));
				
				Map<String, List<FacilioField>> dimensionFieldMap = (Map<String, List<FacilioField>>)rearrangedFields.get("dimension");
				
				dimensionFieldMap.put(FacilioConstants.ContextNames.ASSET, assetFields);
				dimensionFieldMap.put(FacilioConstants.ContextNames.SPACE, spaceFields);
				
				ArrayList<String> dimensionListOrder = new ArrayList<String>();
				dimensionListOrder.add("time");
				dimensionListOrder.add(moduleName);
				dimensionListOrder.add("asset");
				
				rearrangedFields.put("dimensionListOrder", dimensionListOrder);
				
				rearrangedFields.put("displayName","alarms");
		
		return rearrangedFields;	
	}
	
	public static JSONObject getReportFields(String moduleName) throws Exception {
		JSONObject fields = new JSONObject();
		switch (moduleName.toLowerCase()){
		case "workorder":{
			fields = getworkorderReportFields();
			break;
		}
		case "asset":{
			fields =  getassetReportFields();
			break;
		}
		case "alarm":
		case "alarmoccurrence":{
			fields = getAlarmReportFields();
			break;
		}
//		case "item":{
//			fields = getitemReportFields();
//			break;
//		}
//		case "tool":{
//			fields = gettoolReportFields();
//			break;
//		}
		case "assetbreakdown":{
			fields = getassetDownTimeReportFields();
			break;
		}
//		case "itemtransactions":{
//			fields = getitemTransactionsReportFields();
//			break;
//		}
//		case "tooltransactions":{
//			fields = gettoolTransactionsReportFields();
//			break;
//		}
		default:{
			fields = getDefaultReportFields(moduleName);
			break;
		}
		}
		return fields;
	}
	
	private static JSONObject rearrangeFields(List<FacilioField> fields, String module) throws Exception{
		JSONObject fieldsObject = new JSONObject();
		Map<String, List<FacilioField>> dimensionFieldMap = new HashMap<>();
		List<FacilioField> metricFields = new ArrayList<>();
		FacilioModule resourceModule = ModuleFactory.getResourceModule();
		
		for (FacilioField field : fields) {
			if(field != null) {
				if (field instanceof NumberField) {
					metricFields.add(field);
				} else if (field.getDataTypeEnum() == FieldType.DATE || field.getDataTypeEnum() == FieldType.DATE_TIME) {
					addFieldInList(dimensionFieldMap, "time", field);
				} else {
					addFieldInList(dimensionFieldMap, module, field);
				}
			}
		}
		FacilioField resourceField = getModuleResourceField(module);
		if(resourceField != null) {
			addFieldInList(dimensionFieldMap, "resource_fields", resourceField);
		}		
		fieldsObject.put("dimension", dimensionFieldMap);
		fieldsObject.put("metrics", metricFields);
		fieldsObject.put("moduleType", addModuleTypes(module));
		
		fieldsObject =  addFormulaFields(fieldsObject, module);
		
		return fieldsObject;
	}
	
	private static JSONObject rearrangeFields(List<FacilioField> fields, FacilioModule module) throws Exception{
		JSONObject fieldsObject = new JSONObject();
		Map<String, List<FacilioField>> dimensionFieldMap = new HashMap<>();
		List<FacilioField> metricFields = new ArrayList<>();
		FacilioModule resourceModule = ModuleFactory.getResourceModule();
		
		for (FacilioField field : fields) {
			if(field != null) {
				if (field instanceof NumberField) {
					if("siteId".equalsIgnoreCase(field.getName())) {
						addFieldInList(dimensionFieldMap, "siteId", field);
					}
					else if((!"stateFlowId".equalsIgnoreCase(field.getName()))) {
						metricFields.add(field);
					}
				} else if (field.getDataTypeEnum() == FieldType.DATE || field.getDataTypeEnum() == FieldType.DATE_TIME) {
					addFieldInList(dimensionFieldMap, "time", field);
				} else if(field.getDataTypeEnum() != FieldType.FILE && (field.getDataTypeEnum() != FieldType.STRING || AccountUtil.isFeatureEnabled(FeatureLicense.ETISALAT))){
					addFieldInList(dimensionFieldMap, module.getDisplayName(), field);
				} else if(field.isMainField()) {
					addFieldInList(dimensionFieldMap, module.getDisplayName(), field);
				}
			}
		}
		FacilioField resourceField = getModuleResourceField(module.getName());
		if(resourceField != null) {
			addFieldInList(dimensionFieldMap, "resource_fields", resourceField);
		}		
		fieldsObject.put("dimension", dimensionFieldMap);
		fieldsObject.put("metrics", metricFields);
		fieldsObject.put("moduleType", addModuleTypes(module.getName()));
		
		fieldsObject =  addFormulaFields(fieldsObject, module.getName());
		
		return fieldsObject;
	}
	
	private static List<FacilioField> getDimensionLookupFields(List<FacilioField> fields) throws Exception{
		List<FacilioField> dimensionFields = new ArrayList();
		for (FacilioField field : fields) {
			if(field != null) {
				if (    !(field instanceof NumberField) 
						&& (field.getDataTypeEnum() == FieldType.ENUM 
						|| field.getDataTypeEnum() == FieldType.LOOKUP
						|| field.getDataTypeEnum() == FieldType.SYSTEM_ENUM 
						|| field.getDataTypeEnum() == FieldType.BOOLEAN) 
						|| field.isMainField()
					) {
					
					dimensionFields.add(field);
				}
			}
		}
		return dimensionFields;
	}
	
	private static FacilioField getModuleResourceField(String moduleName) throws Exception{
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleResourceFieldName = new String();
		switch(moduleName){
		case "workorder":
			moduleResourceFieldName = "resource";
			break;
		case "asset":
			moduleResourceFieldName = "space";
			break;
		case "assetbreakdown":
//			moduleName = "asset";
			moduleResourceFieldName = "asset";
			break;
		case "alarm":
			moduleResourceFieldName = "resource";
			break;
		case "alarmoccurrence":
			moduleResourceFieldName = "resource";
			break;
		case "itemtransactions":
			moduleResourceFieldName = "resource";
			break;
		case "tooltransactions":
			moduleResourceFieldName = "resource";
			break;
		default:
			moduleResourceFieldName = "resource";
			break;
		}
		FacilioField moduleResourceField = FieldFactory.getAsMap(bean.getAllFields(moduleName)).get(moduleResourceFieldName);
		if(moduleResourceField == null) {
			return null;
		}
		return moduleResourceField;
	}
	public static List<ModuleType> addModuleTypes(String moduleName){
		List<ModuleType> moduleTypes = new ArrayList<ModuleType>();
		
		switch (moduleName) {
		case "workorder": {
			break;
		}
		}
		return moduleTypes;
	}
	
	private static JSONObject addFormulaFields(JSONObject fieldsObject, String module){
		
		List<FacilioField> metricFields = (List<FacilioField>)fieldsObject.get("metrics");
		Map<String, List<FacilioField>> dimensionFieldMap = (Map<String, List<FacilioField>>)fieldsObject.get("dimension");
		
		if (module.equals("workorder")) {
			metricFields.add(ReportFactory.getReportField(WorkOrder.FIRST_RESPONSE_TIME_COL));
			metricFields.add(ReportFactory.getReportField(WorkOrder.ESTIMATED_DURATION_COL));
			if (FacilioProperties.isProduction() && AccountUtil.getCurrentOrg().getOrgId() == 210) {
//				if (FacilioProperties.isProduction() && AccountUtil.getCurrentOrg().getOrgId() == 210) {
					ReportFacilioField totalScorePercentageField = (ReportFacilioField) ReportFactory.getReportField(WorkOrder.TOTAL_SCORE_PERCENTAGE_COL);
					metricFields.add(totalScorePercentageField);	
//				}
				
//				metricFields.add(ReportFactory.getReportField(WorkOrder.TOTAL_SCORE_PERCENTAGE_COL));
			}
			List<FacilioField> workorderFields = dimensionFieldMap.get(module);
			workorderFields.add(ReportFactory.getReportField(WorkOrder.OPENVSCLOSE_COL));
			workorderFields.add(ReportFactory.getReportField(WorkOrder.OVERDUE_OPEN_COL));
			workorderFields.add(ReportFactory.getReportField(WorkOrder.OVERDUE_CLOSED_COL));
			workorderFields.add(ReportFactory.getReportField(WorkOrder.PLANNED_VS_UNPLANNED_COL));
			
			
		}
		else if(module.equals("alarm")) {
			metricFields.add(ReportFactory.getReportField(Alarm.FIRST_RESPONSE_TIME_COL));
			metricFields.add(ReportFactory.getReportField(Alarm.ALARM_DURATION_COL));
		}
		else if(module.equals("alarmoccurrence")) {
			metricFields.add(ReportFactory.getReportField(Alarm.NEW_ALARM_DURATION_COL));
			metricFields.add(ReportFactory.getReportField(Alarm.NEW_FIRST_RESPONSE_TIME_COL));
			List<FacilioField> dimensionFields = dimensionFieldMap.get(module);
			dimensionFields.add(ReportFactory.getReportField(Alarm.WO_ID));
			
		}
		
		fieldsObject.put("metrics", metricFields);
		fieldsObject.put("dimension", dimensionFieldMap);
		return fieldsObject;
		
	}
	private static void addFieldInList(Map<String, List<FacilioField>> map, String name, FacilioField field) {
		List<FacilioField> list = map.get(name);
		if (list == null) {
			list = new ArrayList<>();
			map.put(name, list);
		}
		list.add(field);
	}
	
	private static HashMap<String, Map<String, FacilioField>> getAdditionalModuleFields(String moduleName, ModuleBean bean) throws Exception {
		List<String> additonalModules = getAdditionalModules(moduleName);
		HashMap<String, Map<String, FacilioField>> additionalModuleFields = new HashMap<String, Map<String, FacilioField>>();
		
		for(String module: additonalModules) {
			Map<String, FacilioField> moduleFields = FieldFactory.getAsMap(bean.getAllFields(module));

			List<FacilioField> customModuleFields = bean.getAllCustomFields(module);
			if(customModuleFields != null) {
				moduleFields.putAll(FieldFactory.getAsMap(customModuleFields));
			}
			
			additionalModuleFields.put(module, moduleFields);
		}
		
		return additionalModuleFields;
		
	}
	
	private static HashMap<String, List<FacilioField>> getAdditionalModuleFields(String moduleName,Map<String,String> additonalModules, ModuleBean bean) throws Exception {
		HashMap<String, List<FacilioField>> additionalModuleFields = new HashMap<String, List<FacilioField>>();
		
		for(String module: additonalModules.keySet()) {
			FacilioModule facilioModule = bean.getModule(additonalModules.get(module));
			List<FacilioField> moduleFields = bean.getAllFields(additonalModules.get(module));

			List<FacilioField> customModuleFields = bean.getAllCustomFields(additonalModules.get(module));
			if(customModuleFields != null && !facilioModule.isCustom()) {
				moduleFields.addAll(customModuleFields);
			}
			
			additionalModuleFields.put(additonalModules.get(module), moduleFields);
		}
		
		return additionalModuleFields;
		
	}
	
	private static List<String> getAdditionalModules(String moduleName) {
		List<String> moduleNames = new ArrayList<String>();
		
		switch(moduleName.toLowerCase()) {
			case "workorder":
				moduleNames.add(FacilioConstants.ContextNames.ASSET);
				moduleNames.add(FacilioConstants.ContextNames.SPACE);
				break;

			case "alarm":
			case "alarmoccurrence":
				moduleNames.add(FacilioConstants.ContextNames.ASSET);
				moduleNames.add(FacilioConstants.ContextNames.SPACE);
				break;
				
			case "item":
				moduleNames.add(FacilioConstants.ModuleNames.ITEM_TYPES);
				moduleNames.add(FacilioConstants.ModuleNames.STORE_ROOM);
				break;
				
			case "tool":
				moduleNames.add(FacilioConstants.ModuleNames.TOOL_TYPES);
				moduleNames.add(FacilioConstants.ModuleNames.STORE_ROOM);
				break;
				
			case "assetbreakdown":
				
				moduleNames.add(FacilioConstants.ContextNames.ASSET);
				break;
				
			case "itemtransactions":
				moduleNames.add(FacilioConstants.ContextNames.ASSET);
				moduleNames.add(FacilioConstants.ContextNames.WORK_ORDER);
				moduleNames.add(FacilioConstants.ModuleNames.REQUEST_LINE_ITEM);
				moduleNames.add(FacilioConstants.ModuleNames.PURCHASED_ITEM);
				moduleNames.add(FacilioConstants.ModuleNames.ITEM_TYPES);
				moduleNames.add(FacilioConstants.ModuleNames.ITEM);
				moduleNames.add(FacilioConstants.ModuleNames.GATE_PASS);
				break;
				
			case "tooltransactions":
				moduleNames.add(FacilioConstants.ContextNames.ASSET);
				moduleNames.add(FacilioConstants.ContextNames.WORK_ORDER);
				moduleNames.add(FacilioConstants.ModuleNames.REQUEST_LINE_ITEM);
				moduleNames.add(FacilioConstants.ModuleNames.PURCHASED_TOOL);
				moduleNames.add(FacilioConstants.ModuleNames.TOOL_TYPES);
				moduleNames.add(FacilioConstants.ModuleNames.TOOL);
				moduleNames.add(FacilioConstants.ModuleNames.GATE_PASS);
				break;
		}
		
		return moduleNames;
	}
	
	private static void setAdditionalModulemap(JSONObject rearragedFields, String moduleName, ModuleBean bean) throws Exception{
		
		List<String> additionalModules = getAdditionalModules(moduleName);
		HashMap<String, Long> moduleMap = new HashMap<String, Long>();
		for(String module: additionalModules) {
			FacilioModule facilioModule = bean.getModule(module);
			moduleMap.put(module.toLowerCase(), facilioModule.getModuleId());
		}
		rearragedFields.put("moduleMap", moduleMap);
	}
	private static void setDefaultAdditionalModulemap(JSONObject rearragedFields, Map<String,String> additionalModules, ModuleBean bean) throws Exception{
		
		HashMap<String, Long> moduleMap = new HashMap<String, Long>();
		for(String module: additionalModules.keySet()) {
			FacilioModule facilioModule = bean.getModule(additionalModules.get(module));
			moduleMap.put(module.toLowerCase(), facilioModule.getModuleId());
		}
		rearragedFields.put("moduleMap", moduleMap);
	}
}

package com.facilio.report.context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.report.context.ReportFactory.Alarm;
import com.facilio.report.context.ReportFactory.ModuleType;
import com.facilio.report.context.ReportFactory.WorkOrder;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportFactoryFields {
	
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
		
		return rearrangedFields;
		
	}
	
	public static JSONObject getAlarmReportFields() throws Exception{
		ModuleBean bean = (ModuleBean)BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fields = FieldFactory.getAsMap(bean.getAllFields("alarm"));
		
		List<FacilioField> selectedFields = new ArrayList<FacilioField>();
		
		Map<String, FacilioField> customFields = new HashMap<String, FacilioField>();
		if(bean.getAllCustomFields("alarm") != null) {
			customFields = FieldFactory.getAsMap(bean.getAllCustomFields("alarm"));
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
				JSONObject rearrangedFields = rearrangeFields(selectedFields, "alarm");
				HashMap<String , Map<String, FacilioField>> additionalModuleFields = getAdditionalModuleFields("alarm", bean);
				
				setAdditionalModulemap(rearrangedFields, "alarm", bean);
				
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
				
				
				List<FacilioField> spaceFields = new ArrayList<FacilioField>();
				spaceFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.SPACE).get("spaceCategory"));
				
				Map<String, List<FacilioField>> dimensionFieldMap = (Map<String, List<FacilioField>>)rearrangedFields.get("dimension");
				
				dimensionFieldMap.put(FacilioConstants.ContextNames.ASSET, assetFields);
				dimensionFieldMap.put(FacilioConstants.ContextNames.SPACE, spaceFields);
				
				ArrayList<String> dimensionListOrder = new ArrayList<String>();
				dimensionListOrder.add("time");
				dimensionListOrder.add("alarm");
				dimensionListOrder.add("asset");
				
				rearrangedFields.put("dimensionListOrder", dimensionListOrder);
		
		return rearrangedFields;	
	}
	
	public static JSONObject getReportFields(String moduleName) throws Exception {
		JSONObject fields = new JSONObject();
		switch (moduleName){
		case "workorder":{
			fields = getworkorderReportFields();
			break;
		}
		case "asset":{
			fields =  getassetReportFields();
			break;
		}
		case "alarm":{
			fields = getAlarmReportFields();
			break;
		}
		case "item":{
			fields = getitemReportFields();
			break;
		}
		case "tool":{
			fields = gettoolReportFields();
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
		case "alarm":
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
		
		fieldsObject.put("metrics", metricFields);
		fieldsObject.put("dimension", dimensionFieldMap);
		return fieldsObject;
		
	}
	private static void addFieldInList(Map<String, List<FacilioField>> map, String name, FacilioField field) {
		List<FacilioField> list = (List<FacilioField>) map.get(name);
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
	
	private static List<String> getAdditionalModules(String moduleName) {
		List<String> moduleNames = new ArrayList<String>();
		
		switch(moduleName) {
			case "workorder":
				moduleNames.add(FacilioConstants.ContextNames.ASSET);
				moduleNames.add(FacilioConstants.ContextNames.SPACE);
				break;

			case "alarm":
				moduleNames.add(FacilioConstants.ContextNames.ASSET);
				moduleNames.add(FacilioConstants.ContextNames.SPACE);
				break;
				
			case "item":
				moduleNames.add(FacilioConstants.ModuleNames.ITEM_TYPES);
				moduleNames.add(FacilioConstants.ModuleNames.STORE_ROOM);
				
			case "tool":
				moduleNames.add(FacilioConstants.ModuleNames.TOOL_TYPES);
				moduleNames.add(FacilioConstants.ModuleNames.STORE_ROOM);
		}
		
		return moduleNames;
	}
	
	private static void setAdditionalModulemap(JSONObject rearragedFields, String moduleName, ModuleBean bean) throws Exception{
		
		List<String> additionalModules = getAdditionalModules(moduleName);
		HashMap<String, Long> moduleMap = new HashMap<String, Long>();
		for(String module: additionalModules) {
			FacilioModule facilioModule = bean.getModule(module);
			moduleMap.put(module, facilioModule.getModuleId());
		}
		rearragedFields.put("moduleMap", moduleMap);
	}
}

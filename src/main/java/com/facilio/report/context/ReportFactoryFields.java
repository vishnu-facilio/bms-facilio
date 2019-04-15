package com.facilio.report.context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReportFactory.Alarm;
import com.facilio.report.context.ReportFactory.ModuleType;
import com.facilio.report.context.ReportFactory.WorkOrder;
import com.mysql.fabric.xmlrpc.base.Array;

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
		JSONObject rearragedFields = rearrangeFields(selectedFields, "workorder");
		HashMap<String , Map<String, FacilioField>> additionalModuleFields = getAdditionalModuleFields("workorder", bean);
		
	
		List<FacilioField> assetFields = new ArrayList<FacilioField>();
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("category"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("type"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("department"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("state"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("unitPrice"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("purchasedDate"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("retireDate"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("warrantyExpiryDate"));
		
		
		Map<String, List<FacilioField>> dimensionFieldMap = (Map<String, List<FacilioField>>)rearragedFields.get("dimension");

		
		dimensionFieldMap.put(FacilioConstants.ContextNames.ASSET, assetFields);
		
		return rearragedFields;
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
		
		
		return rearrangeFields(selectedFields, "asset");
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
				JSONObject rearragedFields = rearrangeFields(selectedFields, "alarm");
				HashMap<String , Map<String, FacilioField>> additionalModuleFields = getAdditionalModuleFields("alarm", bean);
				
			
				List<FacilioField> assetFields = new ArrayList<FacilioField>();
				assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("category"));
				assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("type"));
				assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("department"));
				assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("state"));
				assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("unitPrice"));
				assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("purchasedDate"));
				assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("retireDate"));
				assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("warrantyExpiryDate"));
				
				
				Map<String, List<FacilioField>> dimensionFieldMap = (Map<String, List<FacilioField>>)rearragedFields.get("dimension");
				
				dimensionFieldMap.put(FacilioConstants.ContextNames.ASSET, assetFields);
		
		return rearragedFields;	
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
				break;

			case "alarm":
				moduleNames.add(FacilioConstants.ContextNames.ASSET);
				break;
		}
		
		return moduleNames;
	}
}

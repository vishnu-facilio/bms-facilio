package com.facilio.report.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReportFactory.ModuleType;
import com.facilio.report.context.ReportFactory.WorkOrder;
import com.mysql.fabric.xmlrpc.base.Array;

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
		selectedFields.add(fields.get("status"));
		
		if(customFields.size() != 0) {
			for(String customFieldName: customFields.keySet()) {
				selectedFields.add(customFields.get(customFieldName));
			}
		}
		return rearrangeFields(selectedFields, "workorder");
	}
	
	public static JSONObject getassetReportFields() throws Exception{
		ModuleBean bean = (ModuleBean)BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fields = FieldFactory.getAsMap(bean.getAllFields("asset"));
		
		
		Map<String, FacilioField> customFields = new HashMap<String, FacilioField>();
		if(bean.getAllCustomFields("workorder") != null) {
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
		return rearrangeFields(bean.getAllFields("alarm"), "alarm");	
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
			if (field instanceof NumberField) {
				metricFields.add(field);
			} else if (field instanceof LookupField) {
				addFieldInList(dimensionFieldMap, module, field);
			} else if (field.getDataTypeEnum() == FieldType.DATE || field.getDataTypeEnum() == FieldType.DATE_TIME) {
				addFieldInList(dimensionFieldMap, "time", field);
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
}

package com.facilio.report.context;

import java.util.*;
import java.util.stream.Collectors;

import com.facilio.modules.fields.MultiCurrencyField;
import com.facilio.report.util.ReportUtil;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.report.context.ReportFactory.Alarm;
import com.facilio.report.context.ReportFactory.ReportFacilioField;
import com.facilio.report.context.ReportFactory.WorkOrder;

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
		HashMap<String,String> lookupModuleMap = new HashMap<>();
		Map<String,List<FacilioField>> newDimensionMap = new HashMap<>();
		Map<String,String> linkNameVsDisplayName = new HashMap<>();


		for(String fieldName: fields.keySet()) {
			FacilioField field = fields.get(fieldName);
			if(field.getDataType() == FieldType.LOOKUP.getTypeAsInt()) {
				LookupField lookupField = (LookupField) field;
				if(lookupField.getLookupModule().getTypeEnum() != FacilioModule.ModuleType.PICK_LIST && !"users".equalsIgnoreCase(lookupField.getLookupModule().getName())) {
					if(!lookUpModuleNames.containsKey(lookupField.getDisplayName()) && lookupField.getLookupModule() != null && lookupField.getLookupModule().getName() != null
							&& !lookupField.getLookupModule().getName().equals("preventivemaintenance")) {
						lookUpModuleNames.put(lookupField.getDisplayName(),lookupField.getLookupModule().getName());
						String uniqueKey = ReportUtil.generateUniqueKey(lookupField.getName(),lookupField.getModule().getName());
						lookupModuleMap.put(lookupField.getDisplayName(),uniqueKey.toString());
						String lookupModuleName = lookupField.getLookupModule().getName();
						if(lookupModuleName.equals("basespace") || lookupModuleName.equals("resource")){
							List<FacilioField> resourceFields = ReportUtil.getResourceFields(lookupModuleName,field);
							newDimensionMap.put(uniqueKey,resourceFields);
							linkNameVsDisplayName.put(uniqueKey,lookupField.getDisplayName());
						}
					}
				}
				selectedFields.add(fields.get(fieldName));
			}
			else {
				selectedFields.add(fields.get(fieldName));
			}
	}
		if(	moduleName.equalsIgnoreCase(FacilioConstants.ContextNames.WORK_ORDER)) {
			selectedFields.addAll(FieldFactory.getSystemPointFields(facilioModule));
		}
		
		JSONObject rearrangedFields = rearrangeFields(selectedFields, facilioModule);
		setDefaultAdditionalModulemap(rearrangedFields, lookUpModuleNames, bean);
		HashMap<String , List<FacilioField>> additionalModuleFields = getAdditionalModuleFields(moduleName,lookUpModuleNames, bean);
		
		Map<String, List<FacilioField>> dimensionFieldMap = (Map<String, List<FacilioField>>)rearrangedFields.get("dimension");
		String key = ReportUtil.generateUniqueKey(moduleName,moduleName);
		newDimensionMap.put(key,dimensionFieldMap.get(facilioModule.getDisplayName()));
		linkNameVsDisplayName.put(key,facilioModule.getDisplayName());
		lookupModuleMap.put(facilioModule.getDisplayName(),key);
		ArrayList<String> dimensionListOrder = new ArrayList<String>();
		if(dimensionFieldMap.get("time") != null) {
			dimensionListOrder.add("time");
			String timeKey =ReportUtil.generateUniqueKey("time","time");
			newDimensionMap.put(timeKey,dimensionFieldMap.get("time"));
			linkNameVsDisplayName.put(timeKey,"Time");
			lookupModuleMap.put("time",timeKey);
		}
		dimensionListOrder.add(facilioModule.getDisplayName());
		
		for(String module:lookUpModuleNames.keySet()) {
			String uniqueKey = lookupModuleMap.get(module);
			List<FacilioField> fieldsList = getDimensionLookupFields( additionalModuleFields.get(lookUpModuleNames.get(module)));
			dimensionFieldMap.put(module, fieldsList);
			dimensionListOrder.add(module);
			if(newDimensionMap.containsKey(uniqueKey)){
				List<FacilioField> existingFields = newDimensionMap.get(uniqueKey);
				existingFields.addAll(fieldsList);
				newDimensionMap.put(uniqueKey,existingFields);
			}
			else{
				newDimensionMap.put(uniqueKey,fieldsList);
			}
			linkNameVsDisplayName.put(uniqueKey,module);
		}
		
		
		rearrangedFields.put("dimensionListOrder", dimensionListOrder);
		
		rearrangedFields.put("displayName",facilioModule.getDisplayName());
		rearrangedFields.put("newDimension",newDimensionMap);
		rearrangedFields.put("linkNameVsDisplayName",linkNameVsDisplayName);
		rearrangedFields.put("lookupModuleMap",lookupModuleMap);

		return rearrangedFields;
	}
	
	public static JSONObject getworkorderReportFields() throws Exception{
		ModuleBean bean = (ModuleBean)BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fields = FieldFactory.getAsMap(bean.getAllFields("workorder"));
		Map<String, FacilioField> customFields = new HashMap<String, FacilioField>();
		List<FacilioField> customFieldsList = bean.getAllCustomFields("workorder");
		FacilioModule woModule = bean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		if(customFieldsList != null) {
			customFields = FieldFactory.getAsMap(customFieldsList.stream().filter(field -> field.getDataTypeEnum() != null).collect(Collectors.toList()));
		}
		HashMap<String,FacilioField> lookUpModuleFieldMap = new HashMap<String,FacilioField>();
		HashMap<String,String> additonalModules = new HashMap<String,String>();
		List<FacilioField> selectedFields = new ArrayList<FacilioField>();
		
		selectedFields.add(fields.get("createdTime"));
		selectedFields.add(fields.get("dueDate"));
		selectedFields.add(fields.get("actualWorkStart"));
		selectedFields.add(fields.get("actualWorkEnd"));
		selectedFields.add(fields.get("actualWorkDuration"));
		selectedFields.add(fields.get("responseDueDate"));
		selectedFields.add(fields.get("estimatedStart"));
		selectedFields.add(fields.get("estimatedEnd"));
		selectedFields.add(fields.get("scheduledStart"));
		selectedFields.add(fields.get("priority"));
		selectedFields.add(fields.get("category"));
		selectedFields.add(fields.get("assignedTo"));
		selectedFields.add(fields.get("assignmentGroup"));
		selectedFields.add(fields.get("type"));
		selectedFields.add(fields.get("sourceType"));
		selectedFields.add(fields.get("totalCost"));
		selectedFields.add(fields.get("moduleState"));
		selectedFields.add(fields.get("requestedBy"));
		selectedFields.add(fields.get("modifiedTime"));
		selectedFields.addAll(FieldFactory.getSystemPointFields(woModule));
		
		if(AccountUtil.isFeatureEnabled(FeatureLicense.TENANTS)) {
			selectedFields.add(fields.get("tenant"));
		}
		if(AccountUtil.isFeatureEnabled(FeatureLicense.VENDOR)) {
			selectedFields.add(fields.get("vendor"));
		}

		Map<String, List<FacilioField>> newDimensionMap = new HashMap<>();
		Map<String,String> linkNameVsDisplayName = new HashMap<>();
		HashMap<String,String> lookupModuleMap = new HashMap<>();


		if(customFields.size() != 0) {
			for(String customFieldName: customFields.keySet()) {
				selectedFields.add(customFields.get(customFieldName));
				if(customFields.get(customFieldName).getDataType() == FieldType.LOOKUP.getTypeAsInt()) {
					LookupField lookupField = (LookupField) customFields.get(customFieldName);
					if(lookupField.getLookupModule().getTypeEnum() != FacilioModule.ModuleType.PICK_LIST && !"users".equalsIgnoreCase(lookupField.getLookupModule().getName())) {
						if(!additonalModules.containsKey(lookupField.getDisplayName())) {
							additonalModules.put(lookupField.getDisplayName(),lookupField.getLookupModule().getName());
							lookUpModuleFieldMap.put(lookupField.getDisplayName().toLowerCase(), lookupField);
							String uniqueKey = ReportUtil.generateUniqueKey(lookupField.getName(),lookupField.getModule().getName());
							lookupModuleMap.put(lookupField.getDisplayName(),uniqueKey);
						}
					}
				}
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
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("moduleState"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("unitPrice"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("purchasedDate"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("retireDate"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("warrantyExpiryDate"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("rotatingTool"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("rotatingItem"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("lastIssuedToUser"));

		List<FacilioField> customAssetFields = bean.getAllCustomFields(FacilioConstants.ContextNames.ASSET);
		if(customAssetFields != null) {
			for (FacilioField field : customAssetFields) {
			if(field != null) {
				if (field.getDataTypeEnum() != FieldType.FILE 
					&& field.getDataTypeEnum() != FieldType.STRING
					|| field.isMainField()) {
					
					assetFields.add(field);
				}
			}
		}
		}
		
		List<FacilioField> spaceFields = new ArrayList<FacilioField>();
		spaceFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.SPACE).get("spaceCategory"));
		
		Map<String, List<FacilioField>> dimensionFieldMap = (Map<String, List<FacilioField>>)rearrangedFields.get("dimension");
		lookUpModuleFieldMap.put(FacilioConstants.ContextNames.ASSET, fields.get("resource"));
		
		dimensionFieldMap.put(FacilioConstants.ContextNames.ASSET, assetFields);
		dimensionFieldMap.put(FacilioConstants.ContextNames.SPACE, spaceFields);
		
		ArrayList<String> dimensionListOrder = new ArrayList<String>();
		dimensionListOrder.add("time");
		dimensionListOrder.add("workorder");
		dimensionListOrder.add("asset");
		String timeKey = ReportUtil.generateUniqueKey("time","time");
		String workOrderKey = ReportUtil.generateUniqueKey("workorder","workorder");
		String assetkey = ReportUtil.generateUniqueKey("asset","asset");
		HashMap<String, Long> moduleMap = (HashMap<String, Long>) rearrangedFields.get("moduleMap");
		linkNameVsDisplayName.put(timeKey,"Time");
		linkNameVsDisplayName.put(workOrderKey,"Work Order");
		linkNameVsDisplayName.put(assetkey,"Asset");
		lookupModuleMap.put("time",timeKey);
		lookupModuleMap.put("workorder",workOrderKey);
        lookupModuleMap.put("asset",assetkey);
		for(String module: additonalModules.keySet()) {
			FacilioModule facilioModule = bean.getModule(additonalModules.get(module));
			List<FacilioField> moduleFields = bean.getAllFields(additonalModules.get(module));

//			List<FacilioField> customModuleFields = bean.getAllCustomFields(additonalModules.get(module));
//			if(customModuleFields != null && !facilioModule.isCustom()) {
//				moduleFields.addAll(customModuleFields);
//			}
			moduleMap.put(module.toLowerCase(), facilioModule.getModuleId());
			dimensionFieldMap.put(module, moduleFields);
			newDimensionMap.put(lookupModuleMap.get(module),moduleFields);
			dimensionListOrder.add(module);
			linkNameVsDisplayName.put(lookupModuleMap.get(module),module);
		}
		newDimensionMap.put(timeKey,dimensionFieldMap.get("time"));
		newDimensionMap.put(workOrderKey,dimensionFieldMap.get("workorder"));
		newDimensionMap.put(assetkey,dimensionFieldMap.get("asset"));
		if(dimensionFieldMap.containsKey("resource_fields")){
			newDimensionMap.put("resource_fields",new HashMap<>((Map<String,List<FacilioField>>) rearrangedFields.get("dimension")).get("resource_fields"));
			FacilioField field = newDimensionMap.get("resource_fields").get(0);
			if(field!=null){
				LookupField lookupField = (LookupField) field.clone();
				String uniqueKey = ReportUtil.generateUniqueKey(lookupField.getName(),lookupField.getModule().getName());
				List<FacilioField> resourceFields = ReportUtil.generateResourceFields(field);
				newDimensionMap.put(uniqueKey,resourceFields);
				linkNameVsDisplayName.put(uniqueKey,lookupField.getDisplayName());
			}
		}
		rearrangedFields.put("moduleMap", moduleMap);
		rearrangedFields.put("dimensionListOrder", dimensionListOrder);
		rearrangedFields.put("parentlookupFileds", lookUpModuleFieldMap);
		rearrangedFields.put("displayName","workorders");
		rearrangedFields.put("newDimension",newDimensionMap);
		rearrangedFields.put("linkNameVsDisplayName",linkNameVsDisplayName);
		rearrangedFields.put("lookupModuleMap",lookupModuleMap);

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
		selectedFields.add(fields.get("name"));
		selectedFields.add(fields.get("type"));
		selectedFields.add(fields.get("category"));
		selectedFields.add(fields.get("department"));
		selectedFields.add(fields.get("moduleState"));
		selectedFields.add(fields.get("unitPrice"));
		selectedFields.add(fields.get("purchasedDate"));
		selectedFields.add(fields.get("retireDate"));
		selectedFields.add(fields.get("warrantyExpiryDate"));
		selectedFields.add(fields.get("rotatingTool"));
		selectedFields.add(fields.get("rotatingItem"));
		selectedFields.add(fields.get("lastIssuedToUser"));
		selectedFields.add(fields.get("connected"));
		
		
		if(customFields.size() != 0) {
			for(String customFieldName: customFields.keySet()) {
				selectedFields.add(customFields.get(customFieldName));
			}
		}

		Map<String,String> linkNameVsDisplayName = new HashMap<>();
		Map<String,List<FacilioField>> newDimensionMap = new HashMap<>();
		Map<String,String> lookupModuleMap = new HashMap<>();
		String timeKey =  ReportUtil.generateUniqueKey("time","time");
		String assetKey = ReportUtil.generateUniqueKey("asset","asset");
		JSONObject rearrangedFields = rearrangeFields(selectedFields, "asset");
		Map<String,List<FacilioField>> dimensionMap = (Map<String, List<FacilioField>>) rearrangedFields.get("dimension");
		newDimensionMap.put(timeKey,dimensionMap.get("time"));
		newDimensionMap.put(assetKey,dimensionMap.get("asset"));
		linkNameVsDisplayName.put(timeKey,"Time");
		linkNameVsDisplayName.put(assetKey,"Asset");
		lookupModuleMap.put("time",timeKey);
		lookupModuleMap.put("asset",assetKey);
		if(dimensionMap.containsKey("resource_fields")){
			newDimensionMap.put("resource_fields",new HashMap<>((Map<String, List<FacilioField>>) rearrangedFields.get("dimension")).get("resource_fields"));
			FacilioField field = newDimensionMap.get("resource_fields").get(0);
			if(field!=null){
				LookupField lookupField = (LookupField) field;
				String uniqueKey = ReportUtil.generateUniqueKey(lookupField.getName(),lookupField.getModule().getName());
				List<FacilioField> resourceFields = ReportUtil.generateResourceFields(field);
				newDimensionMap.put(uniqueKey,resourceFields);
				linkNameVsDisplayName.put(uniqueKey,lookupField.getDisplayName());
			}
		}

		ArrayList<String> dimensionListOrder = new ArrayList<String>();
		dimensionListOrder.add("time");
		dimensionListOrder.add("asset");
		
		rearrangedFields.put("dimensionListOrder", dimensionListOrder);
		
		rearrangedFields.put("displayName","assets");
		rearrangedFields.put("newDimension",newDimensionMap);
		rearrangedFields.put("linkNameVsDisplayName",linkNameVsDisplayName);
		rearrangedFields.put("lookupModuleMap",lookupModuleMap);


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
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("moduleState"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("unitPrice"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("purchasedDate"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("retireDate"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("warrantyExpiryDate"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("rotatingTool"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("rotatingItem"));
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("lastIssuedToUser"));

		Map<String, FacilioField> parent_customFields = new HashMap<String, FacilioField>();
		if(bean.getAllCustomFields(FacilioConstants.ContextNames.ASSET) != null) {
			parent_customFields = FieldFactory.getAsMap(bean.getAllCustomFields(FacilioConstants.ContextNames.ASSET));
			if(parent_customFields.size() != 0) {
				for(String customFieldName: parent_customFields.keySet()) {
					assetFields.add(parent_customFields.get(customFieldName));
				}
			}
		}
		Map<String, List<FacilioField>> dimensionFieldMap = (Map<String, List<FacilioField>>)rearrangedFields.get("dimension");

		
		dimensionFieldMap.put(FacilioConstants.ContextNames.ASSET, assetFields);
		
		ArrayList<String> dimensionListOrder = new ArrayList<String>();
		dimensionListOrder.add("time");
		dimensionListOrder.add(FacilioConstants.ModuleNames.ASSET_BREAKDOWN);
		dimensionListOrder.add("asset");

		Map<String,String> linkNameVsDisplayName = new HashMap<>();
		Map<String,String> lookupModuleMap = new HashMap<>();
		String timeKey = ReportUtil.generateUniqueKey("time","time");
		String assetBreakDownKey = ReportUtil.generateUniqueKey("assetbreakdown","assetbreakdown");
		String assetKey = ReportUtil.generateUniqueKey("asset","asset");
		linkNameVsDisplayName.put(timeKey,"Time");
		linkNameVsDisplayName.put(assetBreakDownKey,"Asset Breakdown");
		linkNameVsDisplayName.put(assetKey,"Asset");
		lookupModuleMap.put("time",timeKey);
		lookupModuleMap.put("assetbreakdown",assetBreakDownKey);
		lookupModuleMap.put("asset",assetKey);

		Map<String,List<FacilioField>> newDimensionMap = new HashMap<>();
		newDimensionMap.put(timeKey,dimensionFieldMap.get("time"));
		newDimensionMap.put(assetBreakDownKey,dimensionFieldMap.get("assetbreakdown"));
		newDimensionMap.put(assetKey,dimensionFieldMap.get("asset"));
		if(dimensionFieldMap.containsKey("resource_fields")){
			newDimensionMap.put("resource_fields",new HashMap<>((Map<String, List<FacilioField>>) rearrangedFields.get("dimension")).get("resource_fields"));
			FacilioField field = newDimensionMap.get("resource_fields").get(0);
			if(field!=null){
				LookupField lookupField = (LookupField) field;
				String uniqueKey = ReportUtil.generateUniqueKey(lookupField.getName(),lookupField.getModule().getName());
				List<FacilioField> resourceFields = ReportUtil.generateResourceFields(field);
				newDimensionMap.put(uniqueKey,resourceFields);
				linkNameVsDisplayName.put(uniqueKey,lookupField.getDisplayName());
			}
		}


		rearrangedFields.put("dimensionListOrder", dimensionListOrder);
		
		rearrangedFields.put("displayName","breakdowns");
		rearrangedFields.put("newDimension",newDimensionMap);
		rearrangedFields.put("linkNameVsDisplayName",linkNameVsDisplayName);
		rearrangedFields.put("lookupModuleMap",lookupModuleMap);


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
		assetFields.add(additionalModuleFields.get(FacilioConstants.ContextNames.ASSET).get("moduleState"));
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

		Map<String,String> linkNameVsDisplayName = new HashMap<>();
		Map<String,String> lookupModuleMap = new HashMap<>();
		String timeKey = ReportUtil.generateUniqueKey("time","time");
		String alarmKey = ReportUtil.generateUniqueKey(moduleName,moduleName);
		String assetKey = ReportUtil.generateUniqueKey("asset","asset");
		linkNameVsDisplayName.put(timeKey,"Time");
		FacilioModule module = bean.getModule(moduleName);
		linkNameVsDisplayName.put(alarmKey,module.getDisplayName());
		linkNameVsDisplayName.put(assetKey,"Asset");
		lookupModuleMap.put("time",timeKey);
		lookupModuleMap.put(moduleName,alarmKey);
		lookupModuleMap.put("asset",assetKey);

		Map<String,List<FacilioField>> newDimensionMap = new HashMap<>();
		newDimensionMap.put(ReportUtil.generateUniqueKey("time","time"),dimensionFieldMap.get("time"));
		newDimensionMap.put(ReportUtil.generateUniqueKey(moduleName,moduleName),dimensionFieldMap.get(moduleName));
		newDimensionMap.put(ReportUtil.generateUniqueKey("asset","asset"),dimensionFieldMap.get("asset"));
		if(dimensionFieldMap.containsKey("resource_fields")){
			newDimensionMap.put("resource_fields",new HashMap<>((Map<String, List<FacilioField>>) rearrangedFields.get("dimension")).get("resource_fields"));
			FacilioField field = newDimensionMap.get("resource_fields").get(0);
			if(field!=null){
				LookupField lookupField = (LookupField) field;
				String uniqueKey = ReportUtil.generateUniqueKey(lookupField.getName(),lookupField.getModule().getName());
				List<FacilioField> resourceFields = ReportUtil.generateResourceFields(field);
				newDimensionMap.put(uniqueKey,resourceFields);
				linkNameVsDisplayName.put(uniqueKey,lookupField.getDisplayName());
			}
		}


		rearrangedFields.put("dimensionListOrder", dimensionListOrder);
		rearrangedFields.put("displayName","alarms");
		rearrangedFields.put("newDimension",newDimensionMap);
		rearrangedFields.put("linkNameVsDisplayName",linkNameVsDisplayName);
		rearrangedFields.put("lookupModuleMap",lookupModuleMap);

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
		case "assetbreakdown":{
			fields = getassetDownTimeReportFields();
			break;
		}
		default:{
			fields = getDefaultReportFields(moduleName);
			break;
		}
		}
		return fields;
	}
	public static Set<FacilioModule> getSubModulesList(String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		Set<FacilioModule> modules = new HashSet<>();
		if(!module.isCustom()) {
			modules = modBean.getSubModules(moduleName, ModuleType.SUB_ENTITY, ModuleType.TIME_LOG, ModuleType.SLA_TIME)
												.stream().filter(submodule -> !submodule.isCustom()).collect(Collectors.toSet());
		}
		return modules;
	}
	
	private static JSONObject rearrangeFields(List<FacilioField> fields, String module) throws Exception{
		JSONObject fieldsObject = new JSONObject();
		Map<String, List<FacilioField>> dimensionFieldMap = new HashMap<>();
		List<FacilioField> metricFields = new ArrayList<>();
		FacilioModule resourceModule = ModuleFactory.getResourceModule();
		
		for (FacilioField field : fields) {
			if(field != null) {
				if (field.getDataTypeEnum() != FieldType.BIG_STRING && field.getDataTypeEnum() != FieldType.LARGE_TEXT) {
					if (field instanceof NumberField || field instanceof MultiCurrencyField) {
						metricFields.add(field);
					} else if (field.getDataTypeEnum() == FieldType.DATE || field.getDataTypeEnum() == FieldType.DATE_TIME) {
						addFieldInList(dimensionFieldMap, "time", field);
					} else {
						addFieldInList(dimensionFieldMap, module, field);
					}
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
		
		fieldsObject =  addFormulaFields(fieldsObject, module, null);
		
		return fieldsObject;
	}
	
	private static JSONObject rearrangeFields(List<FacilioField> fields, FacilioModule module) throws Exception{
		JSONObject fieldsObject = new JSONObject();
		Map<String, List<FacilioField>> dimensionFieldMap = new HashMap<>();
		List<FacilioField> metricFields = new ArrayList<>();
		FacilioModule resourceModule = ModuleFactory.getResourceModule();
		
		for (FacilioField field : fields) {
			if(field != null) {
				if(field.getDataTypeEnum() != FieldType.BIG_STRING && field.getDataTypeEnum() != FieldType.LARGE_TEXT) {
					if (field instanceof NumberField || field instanceof MultiCurrencyField) {
						if ("siteId".equalsIgnoreCase(field.getName())) {
							addFieldInList(dimensionFieldMap, "siteId", field);
						} else if ((!"stateFlowId".equalsIgnoreCase(field.getName()))) {
							metricFields.add(field);
						}
					} else if (field.getDataTypeEnum() == FieldType.DATE || field.getDataTypeEnum() == FieldType.DATE_TIME) {
						addFieldInList(dimensionFieldMap, "time", field);
					} else if (field.getDataTypeEnum() != FieldType.FILE && (field.getDataTypeEnum() != FieldType.STRING || AccountUtil.isFeatureEnabled(FeatureLicense.ETISALAT))) {
						addFieldInList(dimensionFieldMap, module.getDisplayName(), field);
					} else if (field.getDataTypeEnum() == FieldType.STRING) {
						addFieldInList(dimensionFieldMap, module.getDisplayName(), field);
					} else if (field.isMainField()) {
						addFieldInList(dimensionFieldMap, module.getDisplayName(), field);
					}
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
		
		fieldsObject =  addFormulaFields(fieldsObject, module.getName(), module.getDisplayName());
		
		return fieldsObject;
	}
	private static List<FacilioField> getPivotRowSupportedFields(List<FacilioField> fields) throws Exception{
		List<FacilioField> pivotRowFields = new ArrayList<>();
		for (FacilioField field : fields) {
			if(field != null && field.getColumnName() != null) {
				if (     
						field.getDataTypeEnum() == FieldType.ID 
						||field.getDataTypeEnum() == FieldType.NUMBER
						||field.getDataTypeEnum() == FieldType.DECIMAL
						||field.getDataTypeEnum() == FieldType.CURRENCY_FIELD
						|| field.getDataTypeEnum() == FieldType.MULTI_CURRENCY_FIELD
						|| field.getDataTypeEnum() == FieldType.ENUM 
						|| field.getDataTypeEnum() == FieldType.LOOKUP
						|| field.getDataTypeEnum() == FieldType.SYSTEM_ENUM 
						|| field.getDataTypeEnum() == FieldType.BOOLEAN
						||field.getDataTypeEnum() == FieldType.STRING
						|| field.getDataTypeEnum() == FieldType.DATE 
						|| field.getDataTypeEnum() == FieldType.DATE_TIME
						|| field.getDataTypeEnum() == FieldType.MULTI_ENUM
						|| field.getDataTypeEnum() == FieldType.BIG_STRING
						|| field.getDataTypeEnum() == FieldType.LARGE_TEXT
						|| field.isMainField()
					) {
					
					pivotRowFields.add(field);
				}
			}
		}
		return pivotRowFields;
	}
	
	private static List<FacilioField> getDimensionLookupFields(List<FacilioField> fields) throws Exception{
		List<FacilioField> dimensionFields = new ArrayList();
		for (FacilioField field : fields) {
			if(field != null) {
				if (    !(field instanceof NumberField) 
						&& (field.getDataTypeEnum() == FieldType.ENUM 
						|| field.getDataTypeEnum() == FieldType.LOOKUP
						|| field.getDataTypeEnum() == FieldType.SYSTEM_ENUM 
						|| field.getDataTypeEnum() == FieldType.BOOLEAN
						||(field.getDataTypeEnum() == FieldType.STRING
						|| field.getDataTypeEnum() == FieldType.DATE 
						|| field.getDataTypeEnum() == FieldType.DATE_TIME
						&& AccountUtil.isFeatureEnabled(FeatureLicense.ETISALAT))) 
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
	
	private static JSONObject addFormulaFields(JSONObject fieldsObject, String module, String displayName){
		
		List<FacilioField> metricFields = (List<FacilioField>)fieldsObject.get("metrics");
		Map<String, List<FacilioField>> dimensionFieldMap = (Map<String, List<FacilioField>>)fieldsObject.get("dimension");
		List<FacilioField> dimensionFields = dimensionFieldMap.get(module);
		if(dimensionFields == null && displayName != null) {
			dimensionFields = dimensionFieldMap.get(displayName);
		}
		
		if (module.equals("workorder")) {
			metricFields.add(ReportFactory.getReportField(WorkOrder.FIRST_RESPONSE_TIME_COL));
			metricFields.add(ReportFactory.getReportField(WorkOrder.ESTIMATED_DURATION_COL));
			if (FacilioProperties.isProduction() && AccountUtil.getCurrentOrg().getOrgId() == 210) {
					ReportFacilioField totalScorePercentageField = (ReportFacilioField) ReportFactory.getReportField(WorkOrder.TOTAL_SCORE_PERCENTAGE_COL);
					metricFields.add(totalScorePercentageField);	
			}
			if (FacilioProperties.isProduction() && AccountUtil.getCurrentOrg().getOrgId() == 274) {
				ReportFacilioField ResolutionDueStatusField = (ReportFacilioField) ReportFactory.getReportField(WorkOrder.RESOLUTION_DUE_COL);
				dimensionFields.add(ResolutionDueStatusField);	
				ReportFacilioField AcceptanceDueStatusField = (ReportFacilioField) ReportFactory.getReportField(WorkOrder.ACCEPTANCE_DUE_COL);
				dimensionFields.add(AcceptanceDueStatusField);
				ReportFacilioField FirstResponseDueStatusField = (ReportFacilioField) ReportFactory.getReportField(WorkOrder.FIRST_RESPONSE_DUE_COL);
				dimensionFields.add(FirstResponseDueStatusField);
			}
			dimensionFields.add(ReportFactory.getReportField(WorkOrder.OPENVSCLOSE_COL));
			dimensionFields.add(ReportFactory.getReportField(WorkOrder.OVERDUE_OPEN_COL));
			dimensionFields.add(ReportFactory.getReportField(WorkOrder.OVERDUE_CLOSED_COL));
			dimensionFields.add(ReportFactory.getReportField(WorkOrder.PLANNED_VS_UNPLANNED_COL));
			dimensionFields.add(ReportFactory.getReportField(WorkOrder.RESPONSE_SLA_COL));
			dimensionFields.add(ReportFactory.getReportField(WorkOrder.RESPONSE_DUE_COL));
			
		}
		else if(module.equals("alarm")) {
			metricFields.add(ReportFactory.getReportField(Alarm.FIRST_RESPONSE_TIME_COL));
			metricFields.add(ReportFactory.getReportField(Alarm.ALARM_DURATION_COL));
		}
		else if(module.equals("alarmoccurrence")) {
			metricFields.add(ReportFactory.getReportField(Alarm.NEW_ALARM_DURATION_COL));
			metricFields.add(ReportFactory.getReportField(Alarm.NEW_FIRST_RESPONSE_TIME_COL));
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
	public static Set<FacilioModule> getDataModulesList(String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<Long> selectModules = new ArrayList<Long>();
		selectModules.add(module.getModuleId());
		FacilioModule extendedModule = module.getExtendModule();
		while(extendedModule != null) {
			selectModules.add(extendedModule.getModuleId());
			extendedModule = extendedModule.getExtendModule();
		}
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getModuleFields())
				.table(ModuleFactory.getModuleModule().getTableName())
				.innerJoin(ModuleFactory.getFieldsModule().getTableName())
				.on("Fields.MODULEID = Modules.MODULEID")
				.innerJoin(ModuleFactory.getLookupFieldsModule().getTableName())
				.on("Fields.FIELDID = "+ ModuleFactory.getLookupFieldsModule().getTableName() +".FIELDID")
				.andCustomWhere(ModuleFactory.getLookupFieldsModule().getTableName()+".LOOKUP_MODULE_ID IN ("+StringUtils.join(selectModules, ',')+")");
		List<Map<String, Object>> props = builder.get();
		for (Map<String, Object> prop: props) {
			if (prop.containsKey("createdBy")) {
				IAMUser user = new IAMUser();
				user.setId((long) prop.get("createdBy"));
				prop.put("createdBy", user);
			}
		}
		Set<FacilioModule> modules = FieldUtil.getAsBeanListFromMapList(props, FacilioModule.class)
				.stream().collect(Collectors.toSet());
		if(moduleName.equalsIgnoreCase("asset")) {
			FacilioModule womodule = modBean.getModule("workorder");
			modules.add(womodule);
		}
		return modules;
	}
	public static Set<FacilioField> getMetricsList(String moduleName) throws Exception {
		//List<String> column_names= Arrays.asList("STATE_FLOW_ID","SITE_ID","SLA_POLICY_ID","APPROVAL_FLOW_ID");
		ModuleBean modBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> allFields = modBean.getAllFields(moduleName);
		Set<FacilioField> metrics = modBean.getAllFields(moduleName)
				.stream().filter(field -> field instanceof NumberField || field instanceof MultiCurrencyField).collect(Collectors.toSet());
		FacilioField idField = FieldFactory.getIdField(module);
		if(idField != null) {
			idField.setDisplayName("Number of "+module.getDisplayName());
		}
		idField.getModule().setFields(allFields);
		metrics.add(idField);
		return metrics;
	}

	public static JSONObject getTabularRowReportFields(String moduleName) throws Exception {
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule facilioModule = bean.getModule(moduleName);

		List<FacilioField> allFields = bean.getAllFields(moduleName);
		List<FacilioField> selectedFields = new ArrayList<FacilioField>();
		Set<FacilioModule> lookUpModules = new HashSet<>();

		// collect lookup modules , also replace special lookups(basespace/resource)
		// with child lookups
		FacilioField site_field = FieldFactory.getSiteField(facilioModule);
		if(site_field != null && moduleName != null && !moduleName.equals("inspectionResponse")){
			selectedFields.add(site_field);
		}
		for (FacilioField field : allFields) {
			if (field.getDataType() == FieldType.LOOKUP.getTypeAsInt()) {
				LookupField lookupField = (LookupField) field;
				if (lookupField.getLookupModule().getTypeEnum() != FacilioModule.ModuleType.PICK_LIST
						&& !"users".equalsIgnoreCase(lookupField.getLookupModule().getName())) {
					FacilioModule lookupModule = lookupField.getLookupModule();
					lookUpModules.add(lookupModule);

					if ((lookupModule.getName().equals(FacilioConstants.ContextNames.BASE_SPACE)
							|| lookupModule.getName().equals(FacilioConstants.ContextNames.RESOURCE))) {
						LookupField siteField = (LookupField) field.clone();
						selectedFields.add(siteField);
						List<FacilioField> fieldsList = ReportUtil.getResourceFields(lookupModule.getName(),field);
						selectedFields.addAll(fieldsList);
					} else {
						selectedFields.add(field);

					}

				} else {
					selectedFields.add(field);
				}

			} else {
				selectedFields.add(field);

			}

		}

		JSONObject dimensionFieldMap = new JSONObject();
	

		// FOR EACH lookup field in module , fill map -> lookupModuleName:lookupModule's fields
		
		for (FacilioModule lookupModule : lookUpModules) {
			if (lookupModule.getName().equalsIgnoreCase("resource")||lookupModule.getName().equalsIgnoreCase("basespace")) {
				
				dimensionFieldMap.put(FacilioConstants.ContextNames.SITE,
						getPivotRowSupportedFields(bean.getAllFields(FacilioConstants.ContextNames.SITE)));
				dimensionFieldMap.put(FacilioConstants.ContextNames.BUILDING,
						getPivotRowSupportedFields(bean.getAllFields(FacilioConstants.ContextNames.BUILDING)));
				dimensionFieldMap.put(FacilioConstants.ContextNames.FLOOR,
						getPivotRowSupportedFields(bean.getAllFields(FacilioConstants.ContextNames.FLOOR)));
				dimensionFieldMap.put(FacilioConstants.ContextNames.SPACE,
						getPivotRowSupportedFields(bean.getAllFields(FacilioConstants.ContextNames.SPACE)));
				if (lookupModule.getName().equalsIgnoreCase("resource"))
				{
					dimensionFieldMap.put(FacilioConstants.ContextNames.ASSET,
							getPivotRowSupportedFields(bean.getAllFields(FacilioConstants.ContextNames.ASSET)));

			} 

				

			} else {
				dimensionFieldMap.put(lookupModule.getName(),
						getPivotRowSupportedFields(bean.getAllFields(lookupModule.getName())));
			}
		}
		dimensionFieldMap.put(moduleName, getPivotRowSupportedFields(selectedFields));

		
		return dimensionFieldMap;
	}
}

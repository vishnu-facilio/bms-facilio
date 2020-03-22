package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.BimDefaultValuesContext;
import com.facilio.bmsconsole.context.BimImportProcessMappingContext;
import com.facilio.bmsconsole.context.BimIntegrationLogsContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.ImportAPI.ImportProcessConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;

public class BimAPI {
	
	public static BimIntegrationLogsContext getBimIntegrationLog(FacilioModule module,List<FacilioField> fields,Long bimImportId) throws Exception {
		BimIntegrationLogsContext bimIntegrationLogsContext;
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(bimImportId, module));
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
	        for (Map<String, Object> prop : props) {
	        	bimIntegrationLogsContext = FieldUtil.getAsBeanFromMap(prop, BimIntegrationLogsContext.class);
                return bimIntegrationLogsContext;
	        }
		}
		
		return null;

	}

	public static ImportProcessContext getColumnHeadings(Sheet sheet, ImportProcessContext importProcessContext) throws Exception{

		JSONArray columnHeadings = getColumnHeadings(sheet);
		if(columnHeadings.size() == 1) {
			if(columnHeadings.get(0) instanceof java.util.HashMap<?,?>) {
				HashMap<Integer,ArrayList<String>> missingColumns = (HashMap<Integer, ArrayList<String>>) columnHeadings.get(0);
				if(!missingColumns.isEmpty()) {
		    		String warningMessage = ImportAPI.constructWarning(missingColumns);
		    		if(importProcessContext.getImportJobMetaJson().isEmpty()) {
		    			JSONObject importMeta = new JSONObject();
		    			importMeta.put(ImportProcessConstants.IMPORT_WARNING, warningMessage);
		    		}
		    		else {
		    			JSONObject importMeta = importProcessContext.getImportJobMetaJson();
		    			importMeta.put(ImportProcessConstants.IMPORT_WARNING, warningMessage);
		    		}
		    	}
				importProcessContext.setColumnHeadingString(null);
			}
			else {
				importProcessContext.setColumnHeadingString(columnHeadings.toJSONString().replaceAll("\"", "\\\""));
			}	
		}
		else {
			importProcessContext.setColumnHeadingString(columnHeadings.toJSONString().replaceAll("\"", "\\\""));
		}
		return importProcessContext;
	}
	
	public static JSONObject getFirstRow (Workbook workbook,Sheet sheet) throws Exception {
		JSONObject firstRow = new JSONObject();
		JSONArray columnHeadings = getColumnHeadings(sheet);
		Row row = sheet.getRow(1);
		int lastCellNum = row.getLastCellNum();
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

		for (int i = 0; i < lastCellNum; i++) {
			Cell cell = row.getCell(i);
			if (columnHeadings.get(i) == null || columnHeadings.get(i) == "null") {
				continue;
			} else {
				if (cell == null) {
					firstRow.put(columnHeadings.get(i), null);
				}
				else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC && HSSFDateUtil.isCellDateFormatted(cell)) {
					throw new IllegalArgumentException("Unsupported Date/Time Formatted Field under column "
							+ columnHeadings.get(i) + " Kindly Use Plain text");
				} else {
					Object obj = 0.0;
					try {
						CellValue cellValue = evaluator.evaluate(cell);
						obj = ImportAPI.getValueFromCell(cell, cellValue);
						firstRow.put(columnHeadings.get(i), obj);
					} catch (Exception e) {
						throw new IllegalArgumentException("Unable To Read Value under column " + columnHeadings.get(i));
					}
				}
			}
		}
		return firstRow;
	}
	

	public static JSONArray getColumnHeadings(Sheet sheet) throws Exception
	{
		JSONArray columnheadings = new JSONArray();
        
        Iterator<Row> itr = sheet.iterator();
        while (itr.hasNext()) {
        	Row row = itr.next();
        	Iterator<Cell> cellItr = row.cellIterator();
        	while (cellItr.hasNext()) {
        		Cell cell = cellItr.next();
        		if(cell.getCellType() == Cell.CELL_TYPE_BLANK) {
        			columnheadings.add(null);
        		}
        		else {
        			String cellValue = cell.getStringCellValue();
        			columnheadings.add(cellValue);
        		}
        	}
        	break;
        }
        return columnheadings;
	}

	public static List<ImportProcessContext> uploadSheet(Workbook workbook,Sheet sheet,long fileId,Set<String> importedModules,SiteContext site) throws Exception {
		
		List<ImportProcessContext> importProcessList = new LinkedList<ImportProcessContext>();

		ImportProcessContext importProcessContext = new ImportProcessContext();
		
		importProcessContext = BimAPI.getColumnHeadings(sheet, importProcessContext);
		
		JSONObject firstRow = BimAPI.getFirstRow(workbook,sheet);	
		
		importProcessContext.setfirstRow(firstRow);
		importProcessContext.setFirstRowString(firstRow.toString());
		importProcessContext.setFileId(fileId);
        importProcessContext.setImportTime(DateTimeUtil.getCurrenTime());
        importProcessContext.setImportType(ImportProcessContext.ImportType.EXCEL.getValue());
        importProcessContext.setUploadedBy(AccountUtil.getCurrentUser().getOuid());
        importProcessContext.setImportSetting(ImportProcessContext.ImportSetting.INSERT.getValue());
    	importProcessContext.setImportMode(ImportProcessContext.ImportMode.NORMAL.getValue());
    	importProcessContext.setStatus(ImportProcessContext.ImportStatus.PARSING_IN_PROGRESS.getValue());
    	
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleNameStr = getModuleNameBySheetName(sheet.getSheetName());
		
		String[] moduleNames = moduleNameStr.split("&&");
		for(int i=0;i<moduleNames.length;i++){
			ImportProcessContext importNew = (ImportProcessContext) importProcessContext.clone();
		    
			String moduleName = moduleNames[i];
			if(importedModules.contains(moduleName)){
				continue;
			}
			FacilioModule facilioModule = modBean.getModule(moduleName);
	        
			if(facilioModule ==  null) {
				throw new IllegalArgumentException("Module cannot be null");
			}
			importNew.setModuleId(facilioModule.getModuleId());
	        
			JSONObject json = new JSONObject();
			JSONObject moduleMetaJson = new JSONObject();
			json.put("module", moduleName);
			moduleMetaJson.put("moduleInfo", json);
			
			moduleMetaJson.put(ImportAPI.ImportProcessConstants.DATE_FORMATS, getDateFormatsBySheetName(sheet.getSheetName()));
			
			importNew.setImportJobMeta(moduleMetaJson.toString());
	        
			importNew.setFieldMappingJSON(getFieldMappingJSON(moduleName));
			importNew.setFieldMappingString(importNew.getFieldMappingJSON().toString());
			
			if(moduleName.equals("site") && site !=null && site.getName()!=null && !site.getName().equals("")){
				long siteId = addSite(site);
				importNew.setSiteId(siteId);
			}
			
	        ImportAPI.addImportProcess(importNew);
	        
	    	importProcessList.add(importNew);
		}
		
		
        return importProcessList;
	}
	
	public static Long addSite(SiteContext site) throws Exception 
	{
		FacilioContext context = new FacilioContext();
		
		LocationContext location = site.getLocation();
		if(location != null && location.getLat() != -1 && location.getLng() != -1)
		{
			location.setName(site.getName()+"_Location");
			context.put(FacilioConstants.ContextNames.RECORD, location);
			FacilioChain addLocation = FacilioChainFactory.addLocationChain();
			addLocation.execute(context);
			long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
			location.setId(locationId);
		}
		context.put(FacilioConstants.ContextNames.SITE, site);
		FacilioChain addCampus = FacilioChainFactory.getAddCampusChain();
		addCampus.execute(context);
		
		return site.getId();
		
	}
	
	
	public static String getModuleNameBySheetName(String sheetName) {
		// TODO Auto-generated method stub
		if(sheetName.equalsIgnoreCase("Contact")){
			return FacilioConstants.ContextNames.CONTACT;
		}else if(sheetName.equalsIgnoreCase("Component")){
			return FacilioConstants.ContextNames.ASSET;
		}else if(sheetName.equalsIgnoreCase("Type")){
			return FacilioConstants.ContextNames.ASSET_CATEGORY;
		}else if(sheetName.equalsIgnoreCase("Facility")){
			return "site&&building";
		}else if(sheetName.equalsIgnoreCase("Floor")){
			return "floor";
		}else if(sheetName.equalsIgnoreCase("Space")){
			return "space";
		}else if(sheetName.equalsIgnoreCase("Zone")){
			return "zone&&zonespacerel";
		}else if(sheetName.equalsIgnoreCase("Job")){
			return "preventivemaintenance";
		}
		return null;
	}

	public static JSONObject getFieldMappingJSON(String moduleName) throws Exception{
		JSONObject fieldMapping = new JSONObject();
		if(moduleName.equalsIgnoreCase(FacilioConstants.ContextNames.CONTACT)){
			fieldMapping.put(moduleName+"__"+"email", "Email");
			fieldMapping.put(moduleName+"__"+"phone", "Phone");
			fieldMapping.put(moduleName+"__"+"name", "GivenName&&FamilyName");
		}else if(moduleName.equalsIgnoreCase(FacilioConstants.ContextNames.ASSET)){
			fieldMapping.put("resource"+"__"+"name", "Name");
			fieldMapping.put(moduleName+"__"+"category", "TypeName");
			fieldMapping.put(moduleName+"__"+"warrantyExpiryDate", "WarrantyStartDate");
			fieldMapping.put(moduleName+"__"+"serialNumber", "SerialNumber");
			fieldMapping.put(moduleName+"__"+"tagNumber", "TagNumber");
		}else if(moduleName.equalsIgnoreCase(FacilioConstants.ContextNames.ASSET_CATEGORY)){
			fieldMapping.put(moduleName+"__"+"name", "Name");
			fieldMapping.put(moduleName+"__"+"displayName", "Name");
		}else if(moduleName.equalsIgnoreCase("site")){
			fieldMapping.put("resource"+"__"+"name", "Name");
			fieldMapping.put("resource"+"__"+"description", "Description");
		}else if(moduleName.equalsIgnoreCase("building")){
			fieldMapping.put("resource"+"__"+"name", "ProjectName");
			fieldMapping.put("resource"+"__"+"description", "ProjectDescription");
			fieldMapping.put(moduleName+"__"+"site", "Name");
		}else if(moduleName.equalsIgnoreCase("floor")){
			fieldMapping.put("resource"+"__"+"name", "Name");
			fieldMapping.put("resource"+"__"+"description", "Description");
		}else if(moduleName.equalsIgnoreCase("space")){
			fieldMapping.put("resource"+"__"+"name", "Name");
			fieldMapping.put("resource"+"__"+"description", "Description");
			fieldMapping.put(moduleName+"__"+"floor", "FloorName");
		}else if(moduleName.equalsIgnoreCase("zone")){
			fieldMapping.put("resource"+"__"+"name", "Name");
		}else if(moduleName.equalsIgnoreCase("zonespacerel")){
			fieldMapping.put("resource"+"__"+"name", "Name");
			fieldMapping.put(moduleName+"__"+"space", "SpaceNames");
		}else if(moduleName.equalsIgnoreCase("preventivemaintenance")){
			fieldMapping.put(moduleName+"__"+"title", "Name");
			fieldMapping.put(moduleName+"__"+"secName", "Description");
			
		}
		return fieldMapping;
	}
	
	private static HashMap<String, Object> getDateFormatsBySheetName(String sheetName) {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		if(sheetName.equalsIgnoreCase("Component")){
			json.put("WarrantyStartDate", "yyyy-MM-dd'T'HH:mm:ss");
		}
		return json;
	}
	
	
	public static long addBimIntegrationLog(FacilioModule module,List<FacilioField> fields,BimIntegrationLogsContext bimIntegrationLog) throws Exception{
		Map<String, Object> props = FieldUtil.getAsProperties(bimIntegrationLog);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().table(module.getTableName())
				.fields(fields).addRecord(props);
		insertBuilder.save();
		return (Long) props.get("id");
	}
	
	public static long addBimDefaultValue(FacilioModule module,List<FacilioField> fields,BimDefaultValuesContext bimDefaultValuesContext) throws Exception{
		Map<String, Object> props = FieldUtil.getAsProperties(bimDefaultValuesContext);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().table(module.getTableName())
				.fields(fields).addRecord(props);
		insertBuilder.save();
		return (Long) props.get("id");
	}
	
	public static void DeleteBimDefaultValue(FacilioModule module,List<FacilioField> fields,BimDefaultValuesContext bimDefaultValuesContext)throws Exception{
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get("bimId"), String.valueOf(bimDefaultValuesContext.getBimId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get("moduleId"), String.valueOf(bimDefaultValuesContext.getModuleId()), NumberOperators.EQUALS))
		        .andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get("fieldId"), String.valueOf(bimDefaultValuesContext.getFieldId()), NumberOperators.EQUALS));
		builder.delete();
	}
	
	public static HashMap<String, Object> getBimDefaultValues(Long bimId,String moduleName) throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Long moduleId = modBean.getModule(moduleName).getModuleId();
		HashMap<String, Object> bimDefaultValuesMap  = new LinkedHashMap<String, Object>();
		FacilioModule module = ModuleFactory.getBimDefaultValuesModule();
		List<FacilioField> fields = FieldFactory.getBimDefaultValuesFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get("bimId"), String.valueOf(bimId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get("moduleId"), String.valueOf(moduleId), NumberOperators.EQUALS))
				;
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null){
				for(Map<String, Object> prop:props){
					BimDefaultValuesContext bimDef = FieldUtil.getAsBeanFromMap(prop, BimDefaultValuesContext.class);
					
					FacilioField field = modBean.getField(bimDef.getFieldId());
					Object valueObj  = castOrParseValueAsPerType(field, bimDef.getDefaultValue());
					
					bimDefaultValuesMap.put(field.getName(),valueObj);
				}
			}
		
		if(moduleName.equals(FacilioConstants.ContextNames.ASSET_CATEGORY)){
			bimDefaultValuesMap.put("type", AssetCategoryContext.AssetCategoryType.HVAC.getIntVal());
		}
		return bimDefaultValuesMap;
	}
	
	public static void addBimImportProcessMapping(FacilioModule module,List<FacilioField> fields,BimImportProcessMappingContext bim) throws Exception{
		Map<String, Object> props = FieldUtil.getAsProperties(bim);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().table(module.getTableName())
				.fields(fields).addRecord(props);
		insertBuilder.save();
	}
	
	public static void DeleteInBimImportProcessMapping(long bimId,String sheetName,String moduleName)throws Exception{
		FacilioModule module = ModuleFactory.getBimImportProcessMappingModule();
		List<FacilioField> fields = FieldFactory.getBimImportProcessMappingFields();
		
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get("bimId"), String.valueOf(bimId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get("sheetName"), sheetName, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get("moduleName"), moduleName, StringOperators.IS));
		builder.delete();
	}
	
	public static void updateBimImportProcessMapping(FacilioModule module,List<FacilioField> fields,BimImportProcessMappingContext bimImport,Condition condition) throws Exception{
		Map<String, Object> props = FieldUtil.getAsProperties(bimImport);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table(module.getTableName())
				.fields(fields)
				.andCondition(condition)
				;
		updateBuilder.update(props);
	}
	
	public static BimImportProcessMappingContext getBimImportProcessMappingByImportProcessId(FacilioModule module,List<FacilioField> fields,long importProcessId) throws Exception{
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
			.select(fields)
			.table(module.getTableName())
			.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get("importProcessId"), String.valueOf(importProcessId), NumberOperators.EQUALS))
			;
		Map<String, Object> props = selectBuilder.fetchFirst();
		if (props != null){
			return FieldUtil.getAsBeanFromMap(props, BimImportProcessMappingContext.class);
		}
		return null;
	}
	
	public static List<BimImportProcessMappingContext> getCompletedBimImportProcessMapping(FacilioModule module,List<FacilioField> fields,long bimId) throws Exception{
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
			.select(fields)
			.table(module.getTableName())
			.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get("bimId"), String.valueOf(bimId), NumberOperators.EQUALS))
			.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get("status"), String.valueOf(BimImportProcessMappingContext.Status.COMPLETED.getValue()), NumberOperators.EQUALS))
			;
		List<Map<String, Object>> props = selectBuilder.get();
		List<BimImportProcessMappingContext> bimImportList = new ArrayList<>();
		if(!props.isEmpty()){
			for(Map<String, Object> prop:props){
				bimImportList.add(FieldUtil.getAsBeanFromMap(prop, BimImportProcessMappingContext.class));
			}
		}
		
		return bimImportList;
	}
	
	public static List<BimImportProcessMappingContext> getBimImportProcessMapping(FacilioModule module,List<FacilioField> fields,long bimId) throws Exception{
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
			.select(fields)
			.table(module.getTableName())
			.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get("bimId"), String.valueOf(bimId), NumberOperators.EQUALS))
			;
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<BimImportProcessMappingContext> bimImportList = new ArrayList<>();
		if(!props.isEmpty()){
			for(Map<String, Object> prop:props){
				bimImportList.add(FieldUtil.getAsBeanFromMap(prop, BimImportProcessMappingContext.class));
			}
		}
		
		return bimImportList;
	}
	
	public static void updateBimIntegrationLog(FacilioModule module,List<FacilioField> fields,BimIntegrationLogsContext bimIntegrationLog) throws Exception{
		
		Map<String, Object> props = FieldUtil.getAsProperties(bimIntegrationLog);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table(module.getTableName())
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(bimIntegrationLog.getId(), module));
		updateBuilder.update(props);
	}
	
	public static boolean isBIM(long importProcessId) throws Exception{
		FacilioModule module = ModuleFactory.getBimImportProcessMappingModule();
		List<FacilioField> fields = FieldFactory.getBimImportProcessMappingFields();
		
		BimImportProcessMappingContext bimImport =getBimImportProcessMappingByImportProcessId(module, fields, importProcessId);
		if(bimImport != null){
			return true;
		}
		return false;
	}
	
	public static Object castOrParseValueAsPerType(FacilioField field, String value)  {
		switch(field.getDataTypeEnum()) {
			case STRING:
				if(value != null && !((String)value).isEmpty()) {
					if(!(value instanceof String)) {
						value= value.toString();
					}
				}
				else {
					value= null;
				}
				return value;
			case DECIMAL:
				if(value != null && !((String)value).isEmpty()) {
					return FacilioUtil.parseDouble(value);
				}
				else {
					return null;
				}
			case LOOKUP:
			case NUMBER:
			case FILE:
			case COUNTER:
			case ID:
			case SYSTEM_ENUM:
				if(value != null && !((String)value).isEmpty()) {
					return FacilioUtil.parseLong(value);
				}
				else {
					return null;
				}
			case DATE:
			case DATE_TIME:
				if(value != null && !((String)value).isEmpty()) {
					Long longVal = FacilioUtil.parseLong(value);
					return longVal > 0l ? longVal : null;
				}
				else {
					return null;
				}
			case ENUM:
				Integer enumVal = null;
				if(value != null && !((String)value).isEmpty()) {
					EnumField enumField = (EnumField) field;
					
					int val = Double.valueOf(value.toString()).intValue();
					if (enumField.getValue(val) != null) {
						enumVal = val;
					}
				}
				return enumVal;
			case MISC:
			default:
				return value;
		}
	}
	
	public static ResourceContext getResource(String name) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE);
		SelectRecordsBuilder<ResourceContext> resourceBuilder = new SelectRecordsBuilder<ResourceContext>()
				.select(fields).module(module)
				.beanClass(ResourceContext.class)
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get("name"), name, StringOperators.IS));
	
		List<ResourceContext> resources = resourceBuilder.get();
		if (resources != null && !resources.isEmpty()) {
			return resources.get(0);
		}
		return null;
	}
	
	public static void ScheduleGenericImportJob(ImportProcessContext importProcessContext,String sheetName)throws Exception{
		FacilioContext context1= new FacilioContext();
		context1.put(FacilioConstants.ContextNames.IS_BIM,true);
		context1.put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
		context1.put(FacilioConstants.ContextNames.SHEET_NAME, sheetName);
		FacilioTimer.scheduleInstantJob("GenericImportDataLogJob", context1);
	}

	public static void SchedulePMImportJob(ImportProcessContext importProcessContext) throws Exception {
		FacilioTimer.scheduleOneTimeJobWithTimestampInSec(importProcessContext.getId(), "PMImportData" , 10 , "priority");
	}
	
	
	public static void ScheduleBimGenericImportJob(String moduleName,String sheetName,ImportProcessContext importProcess) throws Exception{
		if("preventivemaintenance".equals(moduleName)){
			SchedulePMImportJob(importProcess);
		}else if("site".equals(moduleName) && importProcess.getSiteId()>0){
			FacilioChain importDataChain = TransactionChainFactory.getbimImportUpdateChain();
			importDataChain.getContext().put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcess);
			importDataChain.execute();	
		}else{
			ScheduleGenericImportJob(importProcess,sheetName);
		}
	}
	
	
}

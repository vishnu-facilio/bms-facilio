package com.facilio.bmsconsole.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmContext.AlarmType;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;

public class ExportUtil {
	
	public static String exportData(FileFormat fileFormat,FacilioModule facilioModule, List<ViewField> fields, List<? extends ModuleBaseWithCustomFields> records, boolean isS3Url) throws Exception {
		String fileUrl = null;
		if(fileFormat == FileFormat.XLS){
			fileUrl=ExportUtil.exportDataAsXLS(facilioModule, fields, records, isS3Url);
		}
		else if(fileFormat == FileFormat.CSV){
			fileUrl=ExportUtil.exportDataAsCSV(facilioModule, fields, records, isS3Url);
		}
		return fileUrl;
	}

	public static String exportData(FileFormat fileFormat,String name, Map<String,Object> table, boolean isS3Url) throws Exception {
		String fileUrl = null;
		if(fileFormat == FileFormat.XLS){
			fileUrl=ExportUtil.exportDataAsXLS(name, table, isS3Url);
		}
		else if(fileFormat == FileFormat.CSV){
			fileUrl=ExportUtil.exportDataAsCSV(name, table, isS3Url);
		}
		return fileUrl;
	}
	
	@SuppressWarnings("resource")
	public static String exportDataAsXLS(FacilioModule facilioModule, List<ViewField> fields, List<? extends ModuleBaseWithCustomFields> records, boolean isS3Url) throws Exception
	{
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(facilioModule.getDisplayName());
		HSSFRow rowhead = sheet.createRow((short) 0);

		int i = 0;
		for(ViewField vf : fields)
		{
			String displayName = vf.getColumnDisplayName() != null && !vf.getColumnDisplayName().isEmpty() ? vf.getColumnDisplayName() : vf.getField().getDisplayName();
			rowhead.createCell((short) i).setCellValue(displayName);
			i++;
		}
		
		Map<String, List<Long>> modVsIds= getModuleVsLookupIds(fields, records);
		Map<String, Map<Long,Object>> modVsData= getModuleData(modVsIds);


		int rowCount = 1;
		for(ModuleBaseWithCustomFields record : records)
		{
			HSSFRow row = sheet.createRow(rowCount);
			i = 0;
			for(ViewField vf : fields)
			{
				FacilioField field = vf.getField();
				Map<String, Object> recordProp = FieldUtil.getAsProperties(record);
				Object value = getValue(recordProp, vf);
				if(value != null)
				{
					Object val = getFormattedValue(modVsData, field, value);
					if(val!=null) {
						value=val;
					}

					row.createCell((short) i).setCellValue(value.toString());
				}
				else
				{
					row.createCell((short) i).setCellValue("");
				}
				i++;
			}
			rowCount++;
		}
		autoSizeColumns(sheet);
		
		String fileName = facilioModule.getDisplayName() + ".xls";
		FileOutputStream fileOut = new FileOutputStream(fileName);
		workbook.write(fileOut);
		fileOut.close();

		File file = new File(fileName);
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		long fileId = fs.addFile(file.getPath(), file, "application/xls");

		if (isS3Url) {
			return fs.getOrgiDownloadUrl(fileId);
		}

		return fs.getDownloadUrl(fileId);
	}
	
	public static String exportDataAsXLS(String name, Map<String,Object> table, boolean isS3Url) throws Exception 
	{
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(name);
		HSSFRow rowhead = sheet.createRow((short) 0);
		
		List<String> headers = (ArrayList<String>) table.get("headers");
		Map<String, FacilioField> headerFields = new HashMap<>();
	    	Map<String, Map<Long,Object>> modVsData = new HashMap<>();
	    	if (table.containsKey("modVsIds")) {
	    		Map<String, List<Long>> modVsIds = (Map<String, List<Long>>) table.get("modVsIds");
	    		if (modVsIds != null) {
	    			modVsData = getModuleData(modVsIds);
	    		}
	    		headerFields = (HashMap<String, FacilioField>) table.get("headerFields");
	    	}
		for(int i = 0, len = headers.size(); i < len; i++) {
			String header = headers.get(i).toString();
			if (i != 0 && headerFields.containsKey(header)) {
				FacilioField field = headerFields.get(header);
				if(field.getDataTypeEnum() == FieldType.LOOKUP) {
					Object val = getFormattedValue(modVsData, field, header);
	    				if(val!=null) {
	    					header=(String) val;
	    				}
				}
			}
			rowhead.createCell((short) i).setCellValue(header);
		}
		
		List<List> records = (ArrayList<List>) table.get("records");
		int rowCount = 1;
		for(int i = 0, len = records.size(); i < len; i++) {
			HSSFRow row = sheet.createRow(rowCount);
			Object recordObj = records.get(i);
			if (recordObj instanceof List) {
				List<Object> record = (List<Object>) recordObj;
				for (int j = 0, rowLen = record.size(); j < rowLen; j++) {
					Object value = record.get(j);
					if(value instanceof Double) {
						value = BigDecimal.valueOf((Double)value).toPlainString();
					}
					row.createCell((short) j).setCellValue(value != null ? value.toString() : "");
				}
			}
			else if (recordObj instanceof Map) {
				Map<String,Object> record = (Map<String,Object>) recordObj;
				for(int j = 0, headerLen = headers.size(); j < headerLen; j++) {
					String header = headers.get(j).toString();
					Object value = record.containsKey(header) ? record.get(header) : "";
					if (j == 0 && modVsData != null && !modVsData.isEmpty() && headerFields.containsKey(header)) {
						FacilioField field = headerFields.get(header);
	    					if(field.getDataTypeEnum() == FieldType.LOOKUP) {
	    						Object val = getFormattedValue(modVsData, field, value);
	    	    	    				if(val!=null) {
	    	    	    					value=val;
	    	    	    				}
	    					}
					}
					if(value instanceof Double) {
						value = BigDecimal.valueOf((Double)value).toPlainString();
					}
					row.createCell((short) j).setCellValue(value != null ? value.toString() : "");
	    			}
			}
			
			rowCount++;
		}
		
		autoSizeColumns(sheet);
	
		String fileName = name + ".xls";
		FileOutputStream fileOut = new FileOutputStream(fileName);
		workbook.write(fileOut);
		fileOut.close();

		File file = new File(fileName);
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		long fileId = fs.addFile(file.getPath(), file, "application/xls");
		
		if (isS3Url) {
			return fs.getOrgiDownloadUrl(fileId);
		}

		return fs.getDownloadUrl(fileId);
	}
	
	private static void autoSizeColumns(HSSFSheet sheet) {
		if (sheet.getPhysicalNumberOfRows() > 0) {
			HSSFRow row = sheet.getRow(0);
			Iterator<Cell> cellIterator = row.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				int columnIndex = cell.getColumnIndex();
				sheet.autoSizeColumn(columnIndex);
				int maxWidth = 10000;
				if (sheet.getColumnWidth(columnIndex) > maxWidth) {
					CellStyle style = cell.getCellStyle();
					style.setWrapText(true); 
					cell.setCellStyle(style);
					sheet.setColumnWidth(columnIndex, maxWidth);
				}
			}
		}
	}
	
	public static String exportDataAsCSV(FacilioModule facilioModule, List<ViewField> fields, List<? extends ModuleBaseWithCustomFields> records, boolean isS3Url) throws Exception
    {
		String fileName = facilioModule.getDisplayName() + ".csv";
        	FileWriter writer = new FileWriter(fileName, false);
        	
        	StringBuilder str = new StringBuilder();
        	for(ViewField vf : fields)
    		{
    			String displayName = vf.getColumnDisplayName() != null && !vf.getColumnDisplayName().isEmpty() ? vf.getColumnDisplayName() : vf.getField().getDisplayName();
        		str.append(displayName);
        		str.append(',');
        	}
        	writer.append(StringUtils.stripEnd(str.toString(), ","));
        	writer.append('\n');
        	
        	Map<String, List<Long>> modVsIds= getModuleVsLookupIds(fields, records);
    		Map<String, Map<Long,Object>> modVsData= getModuleData(modVsIds);
        
        	for(ModuleBaseWithCustomFields record : records)
        	{
        		str = new StringBuilder();
	    		for(ViewField vf : fields)
			{
	    			FacilioField field = vf.getField();
				Map<String, Object> recordProp = FieldUtil.getAsProperties(record);
				Object value = getValue(recordProp, vf);
	    			if(value != null)
	    			{
	    				Object val = getFormattedValue(modVsData, field, value);
	    				if(val!=null) {
	    					value=val;
	    				}
	    				str.append(value.toString());
	    			}
	    			else
	    			{
	    				str.append("");
	    			}
	    			str.append(',');
			}
	    		writer.append(StringUtils.stripEnd(str.toString(), ","));
	    		writer.append('\n');
        	}
        	writer.flush();
        	writer.close();
        	
        	File file = new File(fileName);
	    FileStore fs = FileStoreFactory.getInstance().getFileStore();
	    long fileId = fs.addFile(file.getPath(), file, "application/csv");

	    if (isS3Url) {
	    	return fs.getOrgiDownloadUrl(fileId);
		}
	    return fs.getDownloadUrl(fileId);
    }
	
	@SuppressWarnings("unchecked")
	public static String exportDataAsCSV(String name, Map<String,Object> table, boolean isS3Url) throws Exception
    {
		String fileName = name + ".csv";
        	FileWriter writer = new FileWriter(fileName, false);
        	
        	StringBuilder str = new StringBuilder();
        	List<String> headers = (ArrayList<String>) table.get("headers");
        	Map<String, FacilioField> headerFields = new HashMap<>();
        	Map<String, Map<Long,Object>> modVsData = new HashMap<>();
        	if (table.containsKey("modVsIds")) {
        		Map<String, List<Long>> modVsIds = (Map<String, List<Long>>) table.get("modVsIds");
        		if (modVsIds != null) {
        			modVsData = getModuleData(modVsIds);
        		}
        		headerFields = (HashMap<String, FacilioField>) table.get("headerFields");
        	}
    		for(int i = 0, len = headers.size(); i < len; i++) {
    			String header = headers.get(i).toString();
    			if (i != 0 && headerFields.containsKey(header)) {
    				FacilioField field = headerFields.get(header);
    				if(field.getDataTypeEnum() == FieldType.LOOKUP) {
    					Object val = getFormattedValue(modVsData, field, header);
        				if(val!=null) {
        					header=(String) val;
        				}
    				}
    			}
    			str.append(header);
    			str.append(',');
    		}
        	writer.append(StringUtils.stripEnd(str.toString(), ","));
        	writer.append('\n');
        	
        	List<Object> records = (ArrayList<Object>) table.get("records");
       
    		for(int i = 0, len = records.size(); i < len; i++) {
    			str = new StringBuilder();
    			Object recordObj = records.get(i);
    			if (recordObj instanceof List) {
    				List<Object> record = (List<Object>) recordObj;
    				for (int j = 0, rowLen = record.size(); j < rowLen; j++) {
        				Object value = record.get(j);
        				if(value instanceof Double) {
    						value = BigDecimal.valueOf((Double)value).toPlainString();
    					}
        				str.append(value != null ? value.toString() : "").append(',');
        			}
    			}
    			else if (recordObj instanceof Map) {
    				Map<String,Object> record = (Map<String,Object>) recordObj;
    				for(int j = 0, headerLen = headers.size(); j < headerLen; j++) {
    					String header = headers.get(j).toString();
    					Object value = record.containsKey(header) ? record.get(header) : "";
    					if (j == 0 && modVsData != null && !modVsData.isEmpty() && headerFields.containsKey(header)) {
    						FacilioField field = headerFields.get(header);
    	    					if(field.getDataTypeEnum() == FieldType.LOOKUP) {
    	    						Object val = getFormattedValue(modVsData, field, value);
	    	    	    				if(val!=null) {
	    	    	    					value=val;
	    	    	    				}
    	    					}
    					}
    					if(value instanceof Double) {
    						value = BigDecimal.valueOf((Double)value).toPlainString();
    					}
    					str.append("\""+value+"\"").append(',');
    	    			}
    			}
    			
    			writer.append(StringUtils.stripEnd(str.toString(), ","));
	    		writer.append('\n');
    		}
        	writer.flush();
        	writer.close();
        	
        	File file = new File(fileName);
	    FileStore fs = FileStoreFactory.getInstance().getFileStore();
	    long fileId = fs.addFile(file.getPath(), file, "application/csv");
	    
	    if (isS3Url) {
			return fs.getOrgiDownloadUrl(fileId);
		}
	    return fs.getDownloadUrl(fileId);
    }

	private static Object getFormattedValue(Map<String, Map<Long, Object>> modVsData, FacilioField field, Object value) {
		
		switch(field.getDataTypeEnum()) {

		case LOOKUP:{
			
			LookupField lookupField= (LookupField)field;
			String moduleName=lookupField.getSpecialType();
			if(!LookupSpecialTypeUtil.isSpecialType((moduleName))) {
				moduleName= lookupField.getLookupModule().getName();
			}
			Map<Long,Object> idMap= modVsData.get(moduleName);
			if(idMap!=null) {
				long val;
				if (value instanceof Map) {
					val = (long)((Map) value).get("id");
				}
				else if (value instanceof String){
					try {
						val = Long.parseLong(value.toString());
					}
					catch(NumberFormatException e) {
						return value;
					}
				}
				else {
					val = (long) value;
				}
				return idMap.get(val);
			}
			
			break;
		}
		case DATE:
		case DATE_TIME:{
			return DateTimeUtil.getFormattedTime((long)value);
		}
		case NUMBER: {
			if(value instanceof Double) {
				return BigDecimal.valueOf((Double)value).toPlainString();
			}
			return getValueFromEnum(field, value);
		}
		case BOOLEAN:case DECIMAL:case MISC:case STRING:
			break;
		}
		return null;
	}

	private static Map<String, List<Long>> getModuleVsLookupIds(List<ViewField> fields, List<? extends ModuleBaseWithCustomFields> records) throws Exception {
		
		Map<String, List<Long>> modVsIds= new HashMap<String, List<Long>>();
		for(ModuleBaseWithCustomFields record : records){
			for(ViewField vf : fields)
			{
				FacilioField field = vf.getField();
				if (field.getDataTypeEnum()==FieldType.LOOKUP) {

					vf.getParentField();
					Map<String, Object> recordProp = FieldUtil.getAsProperties(record);
					Map value = (Map) getValue(recordProp, vf);
					if(value == null || !value.containsKey("id") || (long)value.get("id") == -1){
						continue;
					}

					LookupField lookupField= (LookupField)field;
					String moduleName=lookupField.getSpecialType();
					if(!LookupSpecialTypeUtil.isSpecialType((moduleName))) {
						moduleName= lookupField.getLookupModule().getName();
					}
					List<Long> ids= modVsIds.get(moduleName);
					if(ids==null) {
						ids=new ArrayList<Long>();
						modVsIds.put(moduleName, ids);
					}
					ids.add((long)value.get("id"));
				}
			}
		}
		return modVsIds;
	}

	private static Map<String, Map<Long, Object>> getModuleData(Map<String, List<Long>> modVsIds)
			throws Exception, InstantiationException, IllegalAccessException {
		
		Map<String, Map<Long, Object>> modVsData =new HashMap<String,Map<Long,Object>>();;
		for(Map.Entry<String, List<Long>> entry:modVsIds.entrySet()) {
			
			String moduleName=entry.getKey();
			List<Long> ids=entry.getValue();
			if(LookupSpecialTypeUtil.isSpecialType(moduleName)) {
				
				Map<Long,Object> idMap= LookupSpecialTypeUtil.getPickList(moduleName,ids);
				if(idMap!=null) {
					modVsData.put(moduleName,idMap);
				}
			}
			else {
				ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				Map<Long,Object> idMap= CommonCommandUtil.getPickList(ids, bean.getModule(moduleName));
				if(idMap!=null) {
					modVsData.put(moduleName,idMap);
				}
			}
		}
		return modVsData;
	}
	
	private static Object getValue(Map<String, Object> prop, ViewField viewField) {
		FacilioField field = viewField.getField();
		FacilioField parentField = viewField.getParentField();
		Object value = null;
		if(parentField != null) {
			Object parentObj = prop.get(parentField.getName());
			if(parentObj != null) {
				value = ((Map) parentObj).get(field.getName());
			}
		} else {
			value = prop.get(field.getName());
		}
		return value;
	}
	
	private static String getValueFromEnum(FacilioField field, Object value) {
		switch(field.getName()) {
			case "alarmType": 
				return AlarmType.getType((int)value).getStringVal();
			case "sourceType":
				return SourceType.getType((int)value).getStringVal();
		}
		return null;
	}
	
	public static String exportModule(FileFormat fileFormat, String moduleName, String viewName, String filters, boolean isS3Value, boolean specialFields) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.CV_NAME, viewName);
		
		int limit = 5000;
		Map<String, String> orgInfo = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.MODULE_EXPORT_LIMIT);
		String orgLimit = orgInfo.get(FacilioConstants.OrgInfoKeys.MODULE_EXPORT_LIMIT);
		
		if (orgLimit != null && !orgLimit.isEmpty()) {
			limit = Integer.parseInt(orgLimit);
		}
		
		JSONObject pagination = new JSONObject();
		pagination.put("page", 1);
		pagination.put("perPage", limit);
		context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
		
//		context.put(FacilioConstants.ContextNames.MAX_LEVEL, 2);
		
		if (filters != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(filters);
			context.put(FacilioConstants.ContextNames.FILTERS, json);
		}
		
		Chain moduleListChain = ReadOnlyChainFactory.fetchModuleDataListChain();
		moduleListChain.execute(context);
		
		List<ModuleBaseWithCustomFields> records = (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		FacilioView view= (FacilioView)context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<ViewField> viewFields = view.getFields();
		if(moduleName.equals("alarm")) {
			Iterator<ViewField> it = viewFields.iterator();
	        while (it.hasNext()) {
	        	ViewField field = it.next();
	            if (field.getField().getName().equals("modifiedTime")) {
	                it.remove();
	            }
	        }
	        
			ViewField createdTime = new ViewField("createdTime", "Created Time");
			createdTime.setField(modBean.getField("createdTime", moduleName));
			viewFields.add(createdTime);
			ViewField modifiedTime = new ViewField("modifiedTime", "Modified Update");
			modifiedTime.setField(modBean.getField("modifiedTime", moduleName));
			viewFields.add(modifiedTime);
			ViewField acknowledgedTime = new ViewField("acknowledgedTime", "Acknowledged Time");
			acknowledgedTime.setField(modBean.getField("acknowledgedTime", moduleName));
			viewFields.add(acknowledgedTime);
			ViewField noOfEvents = new ViewField("noOfEvents", "No Of Events");
			noOfEvents.setField(modBean.getField("noOfEvents", moduleName));
			viewFields.add(noOfEvents);
		}

		if(specialFields) {
		List<Long> ids = records.stream().map(a -> a.getId()).collect(Collectors.toList());
		for (int j = 0; j < viewFields.size(); j++) { 
			if (viewFields.get(j).getField().getName().equals("noOfNotes")) {
				
				viewFields.remove(viewFields.get(j));
				
          }
			if (viewFields.get(j).getField().getName().equals("noOfTasks")) {		
				viewFields.remove(viewFields.get(j));		
          }
		}
		ViewField comment = new ViewField("comment", "Comment");
		FacilioField commentField = new FacilioField();
		commentField.setName("comment");
		commentField.setDataType(FieldType.STRING);
		commentField.setColumnName("COMMENTS");
		commentField.setModule(modBean.getModule(moduleName));
		comment.setField(commentField);
		viewFields.add(comment);		
		Map<Long, List<String>> map = new HashMap<>();
		if (ids.size() > 0) {
		List<NoteContext> notes = NotesAPI.fetchNote(ids, "ticketnotes");
		if (!(notes.isEmpty())) {
			for (int j = 0; j < notes.size(); j++) {
				if (!(notes.get(j).getCreatedBy().getEmail().contains("system+"))) {
				if (map.containsKey(notes.get(j).getParentId())){
					map.get(notes.get(j).getParentId()).add(notes.get(j).getBody());
				}
				else {
					List<String> temp = new ArrayList<>();
					temp.add(notes.get(j).getBody());
					map.put(notes.get(j).getParentId(), temp);
				}
				}
			}
			for (int i = 0; i < records.size(); i++) {
				

				
				Map<String, Object> props = new HashMap<>();
				props.put("comment", StringUtils.join(map.get(records.get(i).getId()), "\n"));
				records.get(i).addData(props);
			}
			
		}
	}

}	
		return exportData(fileFormat, modBean.getModule(moduleName), viewFields, records, isS3Value);
	}
	
}

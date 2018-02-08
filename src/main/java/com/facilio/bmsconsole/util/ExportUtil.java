package com.facilio.bmsconsole.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;

public class ExportUtil {
	
	public static String exportData(FileFormat fileFormat,FacilioModule facilioModule, List<ViewField> fields, List<? extends ModuleBaseWithCustomFields> records) throws Exception {
		String fileUrl = null;
		if(fileFormat == FileFormat.XLS){
			fileUrl=ExportUtil.exportDataAsXLS(facilioModule, fields, records);
		}
		else if(fileFormat == FileFormat.CSV){
			fileUrl=ExportUtil.exportDataAsCSV(facilioModule, fields, records);
		}
		return fileUrl;
	}
	
	@SuppressWarnings("resource")
	public static String exportDataAsXLS(FacilioModule facilioModule, List<ViewField> fields, List<? extends ModuleBaseWithCustomFields> records) throws Exception 
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

		String fileName = facilioModule.getDisplayName() + ".xls";
		FileOutputStream fileOut = new FileOutputStream(fileName);
		workbook.write(fileOut);
		fileOut.close();

		File file = new File(fileName);
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		long fileId = fs.addFile(file.getPath(), file, "application/xls");

		return fs.getPrivateUrl(fileId);
	}
	
	public static String exportDataAsCSV(FacilioModule facilioModule, List<ViewField> fields, List<? extends ModuleBaseWithCustomFields> records) throws Exception
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
	    
	    return fs.getPrivateUrl(fileId);
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
				return idMap.get((long)((Map) value).get("id"));
			}
			
			break;
		}
		case DATE:
		case DATE_TIME:{
			return DateTimeUtil.getFormattedTime((long)value);
		}
		case BOOLEAN:case DECIMAL:case MISC:case NUMBER:case STRING:
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

					FacilioField parentField = vf.getParentField();
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
	
}

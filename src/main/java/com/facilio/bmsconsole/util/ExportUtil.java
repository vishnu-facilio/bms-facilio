package com.facilio.bmsconsole.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;

public class ExportUtil {
	
	@SuppressWarnings("resource")
	public static String exportDataAsXLS(FacilioModule facilioModule, List<FacilioField> fields, List<Map<String, Object>> records) throws Exception 
	{
		HSSFWorkbook workbook = new HSSFWorkbook();
	    HSSFSheet sheet = workbook.createSheet(facilioModule.getDisplayName());
	    HSSFRow rowhead = sheet.createRow((short) 0);
	    
	    int i = 0;
	    for(FacilioField field : fields)
	    {
	    		rowhead.createCell((short) i).setCellValue(field.getDisplayName());
	    		i++;
	    }
	    
	    int rowCount = 1;
	    for(Map<String, Object> record : records)
	    {
	    		HSSFRow row = sheet.createRow((short) rowCount);
	    		i = 0;
	    		for(FacilioField field : fields)
			{
	    			Object value = record.get(field.getName());
	    			if(value != null)
	    			{
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
	
	public static String exportDataAsCSV(FacilioModule facilioModule, List<FacilioField> fields, List<Map<String, Object>> records) throws Exception
    {
		String fileName = facilioModule.getDisplayName() + ".csv";
        	FileWriter writer = new FileWriter(fileName, false);
        	
        	StringBuilder str = new StringBuilder();
        	for(FacilioField field : fields)
        	{
        		str.append(field.getDisplayName());
        		str.append(',');
        	}
        	writer.append(StringUtils.stripEnd(str.toString(), ","));
        	writer.append('\n');
        
        	for(Map<String, Object> record : records)
        	{
        		str = new StringBuilder();
	    		for(FacilioField field : fields)
			{
	    			Object value = record.get(field.getName());
	    			if(value != null)
	    			{
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
}

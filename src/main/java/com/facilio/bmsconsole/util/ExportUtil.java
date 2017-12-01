package com.facilio.bmsconsole.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;

public class ExportUtil {
	
	@SuppressWarnings("resource")
	public String exportDataAsXLS(FacilioModule facilioModule, List<FacilioField> fields, List<Map<String, Object>> records) throws Exception 
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
}

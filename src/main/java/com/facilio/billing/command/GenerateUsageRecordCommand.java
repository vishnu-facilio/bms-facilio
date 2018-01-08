package com.facilio.billing.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.billing.context.BillContext;
import com.facilio.billing.context.ExcelTemplate;
import com.facilio.billing.util.TenantBillingAPI;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.UserTemplate;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;

public class GenerateUsageRecordCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		long startTime = (long)context.get(BillContext.ContextNames.STARTTIME);
		long endTime = (long)context.get(BillContext.ContextNames.ENDTIME);
		long templateId = (long)context.get(BillContext.ContextNames.TEMPLATEID);
		Map<String,String> placeHolders  = TenantBillingAPI.FetchPlaceHolders(templateId);
		String fileURL = HandleBillGeneration(placeHolders, templateId, startTime,  endTime);
		context.put(BillContext.ContextNames.EXCEL_FILE_DOWNLOAD_URL, fileURL);
		return false;
	}
	
	public String HandleBillGeneration(Map<String,String> placeHolders,long templateId,long startTime, long endTime) throws Exception
	{
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		ExcelTemplate excelobject = (ExcelTemplate)TemplateAPI.getTemplate(orgId, templateId);
		String excelName = excelobject.getName();
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		String fileURL = null;
		try(InputStream ins = fs.readFile(excelobject.getExcelFileId())) {
			System.out.println("##### file read stream #####");
			//Workbook workbook = WorkbookFactory.create(ins);
			XSSFWorkbook workbook = new XSSFWorkbook(ins);
			System.out.println("##### workbook created #####");
			for(String key : placeHolders.keySet())
			{
				String placeHolder = placeHolders.get(key);
				System.out.println("### PlaceHoder Key  : "+key +" : Value : "+placeHolder);
				if(key.indexOf(".") != -1)
				{
					String meterName = key.substring(0,key.indexOf("."));
					String paramName = key.substring(key.indexOf(".")+1, key.indexOf("#")); 
					String dataRequired = key.substring(key.indexOf("#")+1, key.length());
					long meterId = TenantBillingAPI.GetMeterId(meterName);

					String sheetName = placeHolder.substring(placeHolder.indexOf("S_")+2, placeHolder.indexOf("_R"));
					String rowNumStr = placeHolder.substring(placeHolder.indexOf("_R_")+3, placeHolder.indexOf("_C"));
					String colNumStr = placeHolder.substring(placeHolder.indexOf("_C_")+3, placeHolder.length());
					
					int rowN = Integer.parseInt(rowNumStr);
					int cellN = Integer.parseInt(colNumStr);
					Sheet sheet = workbook.getSheet(sheetName);
					Row row = sheet.getRow(rowN);
					Cell cell = row.getCell(cellN);
					double reading = 0.0;
					if(dataRequired.equalsIgnoreCase("OR"))
					{
						reading = TenantBillingAPI.GetMeterOpenReading(meterId,paramName,startTime);
					}
					else if(dataRequired.equalsIgnoreCase("CR"))
					{
						reading = TenantBillingAPI.GetMeterCloseReading(meterId,paramName,endTime );
					}
					else
					{
						reading = TenantBillingAPI.GetMeterRun(meterId,paramName,startTime, endTime );		
					}
					System.out.println("##### Reading Before cell set : "+reading);
					cell.setCellValue(reading);
				}			
			}
			HashMap<String, Object> timeData = DateTimeUtil.getTimeData(endTime); 	
			String monthstr = Month.of((int)timeData.get("month")).name();
			int yearStr = (int)timeData.get("year");
			
			String namePrefix = excelName.substring(0,excelName.indexOf("."));
			String namesufix = excelName.substring(excelName.indexOf("."),excelName.length());
			
			String fileName = namePrefix+"_"+monthstr+"_"+yearStr+namesufix;
			System.out.println("##### ouput file name  #####"+fileName);
			FileOutputStream fileOut = new FileOutputStream(fileName);
			workbook.write(fileOut);
			fileOut.close();	    
			File file = new File(fileName);
			//File file = new File(excelName);
			System.out.println("##### ouput file created #####");
			long fileId = fs.addFile(file.getPath(), file, "application/xlsx");
			System.out.println("##### output file Id : "+fileId);
			fileURL = fs.getPrivateUrl(fileId);
			
			
			
		}
		System.out.println(">>>>> fileURL :"+fileURL);
		return fileURL;
	}

	public static void main(String args[])
	{
//		String key = "S_Sheet1_R_12_C_25";
//		String sheetName = key.substring(key.indexOf("S_")+2, key.indexOf("_R"));
//		System.out.println(">>>>>>> sheetName : "+sheetName);
//		String rowNumStr = key.substring(key.indexOf("_R_")+3, key.indexOf("_C"));
//		System.out.println(">>>>>>> row : "+rowNumStr);
//		String colNumStr = key.substring(key.indexOf("_C_")+3, key.length());
//		System.out.println(">>>>>>> col : "+colNumStr);
//		String x = "Hello_PricingSheet.xlsx";
//		String x1 = x.substring(0,x.indexOf("."));
//		String x2 = x.substring(x.indexOf("."), x.length());
//		System.out.println(">>>>>"+x2);

		
		String key = "T2S.KWH#ST";		
		String deviceName = key.substring(0,key.indexOf("."));
		String paramName = key.substring(key.indexOf(".")+1, key.indexOf("#"));
		String dataRequired = key.substring(key.indexOf("#")+1, key.length());
//		System.out.println("deviceName :"+deviceName);
//		System.out.println("paramName :"+paramName);
//		System.out.println("dataRequired :"+dataRequired);

		String key1 = "S_A-04 T2S_R_4_C_4";
		String sheetName = key1.substring(key1.indexOf("S_")+2, key1.indexOf("_R"));
		String rowNumStr = key1.substring(key1.indexOf("_R_")+3, key1.indexOf("_C"));
		String colNumStr = key1.substring(key1.indexOf("_C_")+3, key1.length());
		
		System.out.println("sheetName :"+sheetName);
		System.out.println("rowNumStr :"+rowNumStr);
		System.out.println("colNumStr :"+colNumStr);
		
	}

}

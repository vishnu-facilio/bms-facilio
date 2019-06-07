package com.facilio.billing.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.billing.context.BillContext;
import com.facilio.billing.context.ExcelTemplate;
import com.facilio.billing.util.TenantBillingAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.fs.FileInfo;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

public class GenerateUsageRecordCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		FileStoreFactory.getInstance().getFileStore();
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
		AccountUtil.getCurrentOrg().getOrgId();
		ExcelTemplate excelobject = (ExcelTemplate)TemplateAPI.getTemplate(templateId);
		excelobject.getName();
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		String fileURL = null;
		long fileId = excelobject.getExcelFileId();
		try(InputStream ins = fs.readFile(fileId)) {
			System.out.println("##### file read stream #####");
			//Workbook workbook = WorkbookFactory.create(ins);
			XSSFWorkbook workbook = new XSSFWorkbook(ins);
			
			System.out.println("##### workbook created #####");
			for(String cellfinder : placeHolders.keySet())
			{
				String meterInfo = placeHolders.get(cellfinder);
				System.out.println("### PlaceHoder cellfinder  : "+cellfinder +" : meterInfo : "+meterInfo);
				if(meterInfo.indexOf(".") != -1)
				{
					String meterName = meterInfo.substring(0,meterInfo.indexOf("."));
					String paramName = meterInfo.substring(meterInfo.indexOf(".")+1, meterInfo.indexOf("#")); 
					String dataRequired = meterInfo.substring(meterInfo.indexOf("#")+1, meterInfo.length());
					long meterId = TenantBillingAPI.GetMeterId(meterName);

					String sheetName = cellfinder.substring(cellfinder.indexOf("S_")+2, cellfinder.indexOf("_R"));
					String rowNumStr = cellfinder.substring(cellfinder.indexOf("_R_")+3, cellfinder.indexOf("_C"));
					String colNumStr = cellfinder.substring(cellfinder.indexOf("_C_")+3, cellfinder.length());
					
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
			FileInfo fileInfo = fs.getFileInfo(fileId);
			String fileName = fileInfo.getFileName();
			String namePrefix = fileName.substring(0,fileName.indexOf("."));
			String namesufix = fileName.substring(fileName.indexOf("."),fileName.length());
			
			String generatedFileName = namePrefix+"_"+monthstr+"_"+yearStr+namesufix;
			System.out.println("##### ouput file name  #####"+System.getProperty("user.dir")+generatedFileName);
			FileOutputStream fileOut = new FileOutputStream(generatedFileName);
			workbook.write(fileOut);
			fileOut.close();	    
			File file = new File(generatedFileName);
			//File file = new File(excelName);
			System.out.println("##### ouput file created #####");
			long generatedFileId = fs.addFile(file.getPath(), file, "application/xlsx");
			System.out.println("##### output file Id : "+generatedFileId);
			fileURL = fs.getPrivateUrl(generatedFileId);		
		}
		System.out.println(">>>>> fileURL :"+fileURL);
		return fileURL;
	}

	/*public static void main(String args[])
	{
		String key = "S_Sheet1_R_12_C_25";
		String sheetName = key.substring(key.indexOf("S_")+2, key.indexOf("_R"));
		System.out.println(">>>>>>> sheetName : "+sheetName);
		String rowNumStr = key.substring(key.indexOf("_R_")+3, key.indexOf("_C"));
		System.out.println(">>>>>>> row : "+rowNumStr);
		String colNumStr = key.substring(key.indexOf("_C_")+3, key.length());
		System.out.println(">>>>>>> col : "+colNumStr);
		String x = "Hello_PricingSheet.xlsx";
		String x1 = x.substring(0,x.indexOf("."));
		String x2 = x.substring(x.indexOf("."), x.length());
		System.out.println(">>>>>"+x2);

		
		String key = "T2S.KWH#ST";		
		String deviceName = key.substring(0,key.indexOf("."));
		String paramName = key.substring(key.indexOf(".")+1, key.indexOf("#"));
		String dataRequired = key.substring(key.indexOf("#")+1, key.length());
		System.out.println("deviceName :"+deviceName);
		System.out.println("paramName :"+paramName);
		System.out.println("dataRequired :"+dataRequired);

		String key1 = "S_A-04 T2S_R_4_C_4";
		String sheetName = key1.substring(key1.indexOf("S_")+2, key1.indexOf("_R"));
		String rowNumStr = key1.substring(key1.indexOf("_R_")+3, key1.indexOf("_C"));
		String colNumStr = key1.substring(key1.indexOf("_C_")+3, key1.length());
		
		System.out.println("sheetName :"+sheetName);
		System.out.println("rowNumStr :"+rowNumStr);
		System.out.println("colNumStr :"+colNumStr);
		
	}
*/
}

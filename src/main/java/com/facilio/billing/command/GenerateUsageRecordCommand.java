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
		context.put(BillContext.ContextNames.EXCELFILEDOWNLOADURL, fileURL);
		return false;
	}
	
	public String HandleBillGeneration(Map<String,String> placeHolders,long templateId,long startTime, long endTime) throws Exception
	{
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		ExcelTemplate excelobject = (ExcelTemplate)TemplateAPI.getTemplate(orgId, templateId);
		String excelName = excelobject.getName();
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		try(InputStream ins = fs.readFile(excelobject.getExcelFileId())) {
			Workbook workbook = WorkbookFactory.create(ins);
			
			for(String key : placeHolders.keySet())
			{
				String placeHolder = placeHolders.get(key);
				if(placeHolder.indexOf(".") != -1)
				{
					String meterName = placeHolder.substring(0, placeHolder.indexOf("."));
					String paramName = placeHolder.substring(placeHolder.indexOf(".")+1, placeHolder.length());
					long meterId = TenantBillingAPI.GetMeterId(meterName);
					double totalKWH = TenantBillingAPI.GetMeterRun(meterId,paramName,startTime, endTime );
					
					String sheetName = key.substring(key.indexOf("S_")+2, key.indexOf("_R"));
					String rowNumStr = key.substring(key.indexOf("_R_")+3, key.indexOf("_C"));
					String colNumStr = key.substring(key.indexOf("_C_")+3, key.length());
					int rowN = Integer.parseInt(rowNumStr);
					int cellN = Integer.parseInt(colNumStr);
					Sheet sheet = workbook.getSheet(sheetName);
					Row row = sheet.getRow(rowN);
					Cell cell = row.getCell(cellN);
					cell.setCellValue(totalKWH);	
				}			
			}
			HashMap<String, Object> timeData = DateTimeUtil.getTimeData(endTime); 	
			String monthstr = Month.of((int)timeData.get("month")).name();
			int yearStr = (int)timeData.get("year");
			
			String namePrefix = excelName.substring(0,excelName.indexOf("."));
			String namesufix = excelName.substring(excelName.indexOf("."),excelName.length());
			
			String fileName = namePrefix+"_"+monthstr+"_"+yearStr+namesufix;
			
			FileOutputStream fileOut = new FileOutputStream(fileName);
			workbook.write(fileOut);
			fileOut.close();	    
			File file = new File(fileName);
			long fileId = fs.addFile(file.getPath(), file, "application/xls");
			return fs.getPrivateUrl(fileId);
			
		}
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
		String x = "Hello_PricingSheet.xlsx";
		String x1 = x.substring(0,x.indexOf("."));
		String x2 = x.substring(x.indexOf("."), x.length());
		System.out.println(">>>>>"+x2);

	}

}

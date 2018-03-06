package com.facilio.billing.command;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.aspose.cells.CellValueType;
import com.aspose.cells.Cells;
import com.aspose.cells.FindOptions;
import com.aspose.cells.LookAtType;
import com.aspose.cells.LookInType;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.billing.context.BillContext;
import com.facilio.billing.context.ExcelTemplate;
import com.facilio.billing.util.TenantBillingAPI;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.TemplateAPI;

public class StoreExcelFileCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String templateName = (String)context.get(BillContext.ContextNames.TEMPLATENAME);
		File excelFile = (File)context.get(BillContext.ContextNames.FILE);
		String fileName = (String)context.get(BillContext.ContextNames.FILENAME);
		String contentType = (String)context.get(BillContext.ContextNames.CONTENTTYPE);
		String storeFileName = templateName+"_"+fileName;
		System.out.println(">>>> tenantName :"+templateName);
		System.out.println(">>>> fileName : "+fileName);
		System.out.println(">>>> contentType"+contentType);
		
		ExcelTemplate template = new ExcelTemplate();
		template.setExcelFile(excelFile);
		//template.setName(storeFileName);
		//template.setName(fileName);
		template.setName(templateName);
		template.setType(Template.Type.EXCEL);
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		long templateId = TemplateAPI.addExcelTemplate(orgId,template,fileName);
		template.setId(templateId);
		context.put(BillContext.ContextNames.EXCELOBJECT, template);
		HandlePlaceHolders(excelFile,templateId);
		//HandlePlaceHoldersWithStream(excelFile,templateId);
		return false;
	}
	
	public void HandlePlaceHolders(File file, long templateId) throws Exception
	{
		//XSSFWorkbook workbook = new XSSFWorkbook(file);
		//Above is working code. 
		Map<String,String> placeHolders = new HashMap();
		long sttime = System.currentTimeMillis();
		Workbook workbook = new Workbook(new FileInputStream(file));
		System.out.println("#### StoreExcelFileCommand.HandlePlaceHolders : fileName : "+file.getName());
		WorksheetCollection worksheetCollection = workbook.getWorksheets();
		for(Object worksheetObj : worksheetCollection){
			Worksheet worksheet = (Worksheet) worksheetObj;
			System.out.println("#### worksheet : "+worksheet.getName());
			Cells cells = worksheet.getCells();
			FindOptions opt = new FindOptions();
            opt.setSeachOrderByRows(true);
            opt.setSearchNext(true);
            opt.setLookInType(LookInType.VALUES);
            opt.setCaseSensitive(true);
            opt.setRegexKey(false);
            opt.setLookAtType(LookAtType.CONTAINS);
            int rows = cells.getMaxRow();
            int columns = cells.getMaxColumn();
            for(int row = cells.getMinRow(); row <= rows; row++) {
            	//com.aspose.cells.Row row1 = cells.getRow(row);
            		for(int column = cells.getMinColumn(); column <= columns; column++)
            		{
            			//Column column1 = cells.getColumn(column);
            			com.aspose.cells.Cell cell = cells.get(row, column);
            			
            			if(cell.getType() == CellValueType.IS_STRING)
            			{
            				String cellvalue = cell.getStringValue();
            				if((cellvalue.startsWith("${"))&&(cellvalue.endsWith("}")))
            				{
            					System.out.println("##### inside required cell ####");
            					String cellfinder = "S_"+worksheet.getName()+"_R_"+row+"_C_"+column;
            					String meterInfo = cellvalue.substring(2,cellvalue.lastIndexOf("}"));
            					System.out.println("#### cellfinder : "+cellfinder +", meterInfo : "+meterInfo);
            					placeHolders.put(cellfinder, meterInfo);
            				}
            				
            			}
            		}
            }
           
		}
		long endtime = System.currentTimeMillis();
		System.out.println("### Time taken for this file : "+ (endtime - sttime));
		
//		Map<String,String> placeHolders = new HashMap();
//		ArrayList sheets = new ArrayList();
//		for(int i = 0; i<workbook.getNumberOfSheets();i++)
//		{
//			String sheetName =  workbook.getSheetName(i);
//			Sheet sheet = workbook.getSheet(sheetName);
//			Iterator<Row> rowIterator = sheet.iterator();
//			while(rowIterator.hasNext())
//			{
//				Row row = rowIterator.next();
//				Iterator<Cell> cellIterator = row.cellIterator();
//				while(cellIterator.hasNext())
//				{
//					Cell cell = cellIterator.next();
//					if(cell.getCellType() == Cell.CELL_TYPE_STRING)
//					{
//						String cellvalue = cell.getStringCellValue();
//						 if((cellvalue.startsWith("${"))&&(cellvalue.endsWith("}")))
//				            {
//				            		String cellfinder = "S_"+sheet.getSheetName()+"_R_"+row.getRowNum()+"_C_"+cell.getColumnIndex();
//				            		String meterInfo = cellvalue.substring(2,cellvalue.lastIndexOf("}"));
//				            		placeHolders.put(cellfinder, meterInfo);
//				            }
//					}
//				}
//			}
//		}
//		workbook.close();
		
		TenantBillingAPI.InsertPlaceHolders(placeHolders,templateId);
	}
}

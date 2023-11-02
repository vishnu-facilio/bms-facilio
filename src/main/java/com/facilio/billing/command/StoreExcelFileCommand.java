package com.facilio.billing.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.billing.context.BillContext;
import com.facilio.billing.context.ExcelTemplate;
import com.facilio.billing.util.TenantBillingAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.TemplateAPI;
import org.apache.commons.chain.Context;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StoreExcelFileCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String templateName = (String)context.get(BillContext.ContextNames.TEMPLATENAME);
		File excelFile = (File)context.get(BillContext.ContextNames.FILE);
		String fileName = (String)context.get(BillContext.ContextNames.FILENAME);
		String contentType = (String)context.get(BillContext.ContextNames.CONTENTTYPE);
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
	
	public void HandlePlaceHolders(File file, long templateId) throws InvalidFormatException, IOException, SQLException, RuntimeException
	{
		try(XSSFWorkbook workbook = new XSSFWorkbook(file)) {

//		Workbook workbook = WorkbookFactory.create(file);
//		final OPCPackage pkg = OPCPackage.open(file);
//		final XSSFWorkbook workbook = new XSSFWorkbook(pkg);

			Map<String, String> placeHolders = new HashMap();
			new ArrayList();
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				String sheetName = workbook.getSheetName(i);
				Sheet sheet = workbook.getSheet(sheetName);
				Iterator<Row> rowIterator = sheet.iterator();
				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();
					Iterator<Cell> cellIterator = row.cellIterator();
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						if (cell.getCellType() == CellType.STRING ) {
							String cellvalue = cell.getStringCellValue();
							if ((cellvalue.startsWith("${")) && (cellvalue.endsWith("}"))) {
								String cellfinder = "S_" + sheet.getSheetName() + "_R_" + row.getRowNum() + "_C_" + cell.getColumnIndex();
								String meterInfo = cellvalue.substring(2, cellvalue.lastIndexOf("}"));
								placeHolders.put(cellfinder, meterInfo);
							}
						}
					}
				}
			}
			TenantBillingAPI.InsertPlaceHolders(placeHolders, templateId);
		}
	}
}
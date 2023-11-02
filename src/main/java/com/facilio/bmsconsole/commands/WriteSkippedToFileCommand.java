package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.actions.ImportTemplateContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.modules.FieldUtil;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.apache.commons.chain.Context;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WriteSkippedToFileCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		

		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		ImportTemplateContext importTemplateContext = (ImportTemplateContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_TEMPLATE_CONTEXT);
		List<Map<String, Object>> allRows = ImportAPI.getValidatedRows(importProcessContext.getId());
		List<ImportRowContext> rowContexts = new ArrayList<ImportRowContext>();
		
		List<ImportRowContext> nullUniqueFields = new ArrayList<ImportRowContext>();
		List<ImportRowContext> nullResources = new ArrayList<ImportRowContext>();
				
		for(Map<String, Object> row: allRows) {
			ImportProcessLogContext logContext = FieldUtil.getAsBeanFromMap(row, ImportProcessLogContext.class);
			
			if(logContext.getError_resolved() == ImportProcessContext.ImportLogErrorStatus.OTHER_ERRORS.getValue()) {
				rowContexts = logContext.getRowContexts();
			}
			else {
				continue;
			}
			for(ImportRowContext rowContext: rowContexts) {
				if(rowContext.getError_code() == ImportProcessContext.ImportRowErrorCode.NULL_UNIQUE_FIELDS.getValue()) {
					nullUniqueFields.add(rowContext);
				}
				else if(rowContext.getError_code() == ImportProcessContext.ImportRowErrorCode.NULL_RESOURCES.getValue()) {
					nullResources.add(rowContext);
				}
			}
		}


		if (!nullUniqueFields.isEmpty() || !nullResources.isEmpty()) {
			FileStore fs = FacilioFactory.getFileStore();
			try (Workbook workbook = new XSSFWorkbook()) {
				StringBuilder emailMessage = (StringBuilder) context.get(ImportAPI.ImportProcessConstants.EMAIL_MESSAGE);

				if (!nullUniqueFields.isEmpty()) {
					writeToSheet("Identity field data is mandatory", nullUniqueFields, workbook);
				}
				if (!nullResources.isEmpty()) {
					writeToSheet(importTemplateContext.getModuleMapping().get("baseModule") + "not found in the system", nullResources, workbook);
				}
				String[] oldFileNameArray = fs.getFileInfo(importProcessContext.getFileId()).getFileName().split("[.]");
				String newFileName = oldFileNameArray[0] + "_skipLog";
				File newFile = File.createTempFile(newFileName, "." + oldFileNameArray[(oldFileNameArray.length) - 1]);
				try (FileOutputStream newFileStream = new FileOutputStream(newFile)) {
					workbook.write(newFileStream);
				}

				long fileId = fs.addFile(newFileName + "." + oldFileNameArray[(oldFileNameArray.length) - 1], newFile, "application/xls");

				String fileUrl = fs.getPrivateUrl(fileId);
				emailMessage.append("<a href=" + fileUrl + "Click here to view skipped entries</a>");
				JSONObject meta = importProcessContext.getImportJobMetaJson();
				newFile.delete();
				if (meta == null || meta.isEmpty()) {
					meta = new JSONObject();
					meta.put("skippedLogFileLink", fileUrl);
				} else {
					meta.put("skippedLogFileLink", fileUrl);
				}
				importProcessContext.setImportJobMeta(meta.toJSONString());
				return false;
			}
		}
		else {
			return false;
		}
		
	}
	
	public void writeToSheet(String Header,List<ImportRowContext> data, Workbook workbook){
		Sheet sheet = null;
		if(workbook.getSheet("Error report") != null) {
			sheet = workbook.getSheet("Error report");
		}
		else {
			sheet = workbook.createSheet("Error report");
		}
		Integer offset = sheet.getPhysicalNumberOfRows();
		Row row;
		Cell cell;
		if(Header != null) {
			Font reasonFont  = workbook.createFont();
			reasonFont.setBold(true);
			reasonFont.setFontHeight((short)(14 * 20));
			reasonFont.setColor(IndexedColors.RED.getIndex());
			CellStyle reasonStyle = workbook.createCellStyle();
			reasonStyle.setFont(reasonFont);
			
			// specify reason 
			row = sheet.createRow(offset == 0 ? offset : offset + 1);
			cell = row.createCell(0);
			cell.setCellValue(Header);
			cell.setCellStyle(reasonStyle);
			offset = sheet.getPhysicalNumberOfRows();
			}
		
		
		for(int i =0; i< data.size(); i++) {
			ImportRowContext rowContext = data.get(i);
			HashMap<String, Object> colVal = rowContext.getColVal();
			List<String> columnHeadings = new ArrayList(colVal.keySet());
			if(i == 0) {
				// writing column headings
				Font headerFont = workbook.createFont();
				headerFont.setBold(true);
				headerFont.setColor(IndexedColors.BLACK.getIndex());
				CellStyle headerStyle = workbook.createCellStyle();
				
				row = sheet.createRow(offset == 0 ? 0: offset + 1);
				cell = row.createCell(0);
				cell.setCellValue("Row Number");
				headerStyle.setFont(headerFont);
				cell.setCellStyle(headerStyle);
				
				for(String Heading: columnHeadings) {
					cell = row.createCell(columnHeadings.indexOf(Heading)+ 1);
					cell.setCellValue(Heading);
					cell.setCellStyle(headerStyle);
				}
				
				// write first row values 
				row = sheet.createRow(offset + 2);
				cell = row.createCell(0);
				cell.setCellValue(rowContext.getRowNumber());
				for(String Heading: columnHeadings) {
					cell = row.createCell(columnHeadings.indexOf(Heading)+ 1);
					if(colVal.get(Heading) == null) {
						cell.setCellType(CellType.BLANK);
					}
					else {
						cell.setCellValue(colVal.get(Heading).toString());
					}
					
				}
				
			}
			else {
				row = sheet.createRow(sheet.getPhysicalNumberOfRows() + 1);
				cell = row.createCell(0);
				cell.setCellValue(rowContext.getRowNumber());
				for(String Heading: columnHeadings) {
					cell = row.createCell(columnHeadings.indexOf(Heading)+ 1);
					if(colVal.get(Heading) == null) {
						cell.setCellType(CellType.BLANK);
					}
					else {
						cell.setCellValue(colVal.get(Heading).toString());
					}
				}
			}
		}
	}

}

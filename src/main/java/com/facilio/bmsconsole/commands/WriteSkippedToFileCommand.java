package com.facilio.bmsconsole.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.actions.ImportTemplateContext;
import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.mysql.fabric.xmlrpc.base.Array;

public class WriteSkippedToFileCommand implements Command {

	Workbook workbook = new XSSFWorkbook();
	@Override
	public boolean execute(Context context) throws Exception {
		

		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		ImportTemplateContext importTemplateContext = (ImportTemplateContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_TEMPLATE_CONTEXT);
		List<Map<String, Object>> allRows = ImportAPI.getValidatedRows(importProcessContext.getId());
		List<ImportRowContext> rowContexts = new ArrayList<ImportRowContext>();
		// HashMap<Integer,HashMap<String,Object>> nullUniqueFields  = (HashMap<Integer,HashMap<String,Object>>) context.get(ImportAPI.ImportProcessConstants.NULL_UNIQUE_FIELDS);
		// HashMap<Integer,HashMap<String,Object>> nullResources  = (HashMap<Integer,HashMap<String,Object>>) context.get(ImportAPI.ImportProcessConstants.NULL_RESOURCES);
		
		List<ImportRowContext> nullUniqueFields = new ArrayList<ImportRowContext>();
		List<ImportRowContext> nullResources = new ArrayList<ImportRowContext>();
				
		for(Map<String, Object> row: allRows) {
			ImportProcessLogContext logContext = FieldUtil.getAsBeanFromMap(row, ImportProcessLogContext.class);
			ImportRowContext rowContext = new ImportRowContext();
			if(logContext.getError_resolved() == ImportProcessContext.ImportLogErrorStatus.NO_VALIDATION_REQUIRED.getValue()) {
				rowContext = logContext.getRowContexts().get(0);
			}
			else {
				rowContext = logContext.getCorrectedRow();
			}
			if(rowContext.getError_code() == ImportProcessContext.ImportRowErrorCode.NULL_UNIQUE_FIELDS.getValue()) {
				nullUniqueFields.add(rowContext);
			}
			else if(rowContext.getError_code() == ImportProcessContext.ImportRowErrorCode.NULL_RESOURCES.getValue()) {
				nullResources.add(rowContext);
			}
		}
		
;
		//		Map<String, ImportRowContext> nullUniqueFields = new HashMap<String, ImportRowContext>();
//		Map<String, ImportRowContext> nullResources = new HashMap<String, ImportRowContext>();
		
//		for(String module : groupedContext.keySet()) {
//			Map<String, ImportRowContext> rows = groupedContext.get(module);
//			Map<String, ImportRowContext> tempNullUniqueFields = rows.entrySet()
//																			.stream()
//																			.filter(row -> row.getValue().isHasNullUniqueFields() == true)
//																			.collect(Collectors.toMap(Entry::getKey, Entry::getValue))
//																			;
//			
//			nullUniqueFields.putAll(tempNullUniqueFields);
//			Map<String, ImportRowContext> tempNullResources = rows.entrySet()
//																			.stream()
//																			.filter(row -> row.getValue().isNullResource() == true)
//																			.collect(Collectors.toMap(Entry::getKey, Entry::getValue))
//																			;
//			nullResources.putAll(tempNullResources);
//			
//		}
		
		
		if(!nullUniqueFields.isEmpty() || !nullResources.isEmpty()) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			CreationHelper createHelper = workbook.getCreationHelper();
			StringBuilder emailMessage = (StringBuilder) context.get(ImportAPI.ImportProcessConstants.EMAIL_MESSAGE);
			
			if(!nullUniqueFields.isEmpty()) {
				writeToSheet("Identity field data is mandatory",nullUniqueFields, createHelper);
			}
			if(!nullResources.isEmpty()) {
				writeToSheet(importTemplateContext.getModuleMapping().get("baseModule")+ "not found in the system", nullResources, createHelper);
			}
			String[] oldFileNameArray = fs.getFileInfo(importProcessContext.getFileId()).getFileName().split("[.]");
			String newFileName = oldFileNameArray[0] + "_skipLog" ;
			File newFile = File.createTempFile(newFileName, "." + oldFileNameArray[(oldFileNameArray.length) -1]);
			FileOutputStream newFileStream = new FileOutputStream(newFile);
			workbook.write(newFileStream);
			newFileStream.close();
			
			//File newFile = new File(fs.getFileInfo(importProcessContext.getFileId()).getFilePath() + File.separator + newFileName);
			long fileId = fs.addFile(newFileName+"." + oldFileNameArray[(oldFileNameArray.length) -1] , newFile, "application/xls");
			
			String fileUrl= fs.getPrivateUrl(fileId);
			emailMessage.append("<a href="+fileUrl+"Click here to view skipped entries</a>");
			JSONObject meta = importProcessContext.getImportJobMetaJson();
			newFile.delete();
			if(meta == null || meta.isEmpty()) {
				meta = new JSONObject();
				meta.put("skippedLogFileLink", fileUrl);
			}
			else {
				meta.put("skippedLogFileLink", fileUrl);
			}
			importProcessContext.setImportJobMeta(meta.toJSONString());
			return false;	
		}
		else {
			return false;
		}
		
	}
	
	public void writeToSheet(String Header,List<ImportRowContext> data, CreationHelper createHelper){
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
						cell.setCellType(Cell.CELL_TYPE_BLANK);
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
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					}
					else {
						cell.setCellValue(colVal.get(Heading).toString());
					}
				}
			}
		}
	}

}

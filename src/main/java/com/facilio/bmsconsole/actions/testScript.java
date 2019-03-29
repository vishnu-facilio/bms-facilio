package com.facilio.bmsconsole.actions;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class testScript {

	public static final String FilePath = "/home/prashanth/Desktop/test1.xlsx";
	
	public static void main(String args []) throws Exception {
		Workbook workbook = WorkbookFactory.create(new File(FilePath));
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Iterator<Sheet> sheetIterator = workbook.sheetIterator();
		while(sheetIterator.hasNext()) {
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.rowIterator();
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Cell cel = row.getCell(1);
				if(cel.getCellTypeEnum().getCode() == Cell.CELL_TYPE_STRING) {
					System.out.println(cel.getStringCellValue());
				}
				else if(cel.getCellTypeEnum().getCode() == Cell.CELL_TYPE_NUMERIC) {
					if(HSSFDateUtil.isCellDateFormatted(cel)) {
						Date date = cel.getDateCellValue();
						System.out.println(date.getSeconds());
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(cel.getDateCellValue().getSeconds());
						System.out.println(formatter.format(calendar.getTime())); 
					}
					else {
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis((long)cel.getNumericCellValue());
						System.out.println(formatter.format(calendar.getTime())); 
					}
				}
			}
		}
		
		
		
	}
}

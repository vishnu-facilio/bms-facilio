package com.facilio.bmsconsole.actions;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import com.facilio.util.FacilioUtil;
import org.apache.poi.ss.usermodel.*;

public class testScript {

	public static final String FilePath = FacilioUtil.normalizePath("/home/prashanth/Desktop/test1.xlsx");
	
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
				if(cel.getCellType() == CellType.STRING) {
					System.out.println(cel.getStringCellValue());
				}
				else if(cel.getCellType() == CellType.NUMERIC) {
					if(DateUtil.isCellDateFormatted(cel)) {
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

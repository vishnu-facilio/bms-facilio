package com.facilio.billing.util;

import com.facilio.billing.context.ExcelObject;
import org.apache.poi.poifs.filesystem.DocumentFactoryHelper;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HandleExcelFile {

	public static void main1(String[] args) throws IOException {
		String fileName = "//Users//suresh//Downloads//PricingSheet.xlsx";
		String fileNameNew = "//Users//suresh//Downloads//PricingSheet_New.xlsx";
		 FileInputStream fis = new FileInputStream(new File(fileName));
		 XSSFWorkbook workbook = new XSSFWorkbook(fis);
		 XSSFSheet sheet1 = workbook.getSheet("Energy");
		 XSSFRow row1 = sheet1.getRow(0);
		 XSSFCell cell1 = row1.getCell(7);
		 String cellVal = cell1.toString();
		 double cellvalindouble = Double.parseDouble(cellVal);
		 System.out.println(">>>>> cell value : "+cellVal);
		 System.out.println(">>>>> cell address : "+cell1.getAddress().formatAsString());
		 cell1.setCellValue((cellvalindouble+0.1));
		 fis.close();
		 FileOutputStream fos =new FileOutputStream(new File(fileNameNew));
		 workbook.write(fos);
		 fos.close();
	}
	
	@SuppressWarnings("deprecation")
	public static void UpdateCellValue(ExcelObject excel , Object value) throws Exception
	{
		FileInputStream fis = new FileInputStream(new File(excel.getFileName()));
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet;
		if(excel.getSheetId() != -1)
		{
			sheet = workbook.getSheetAt(excel.getSheetId());
		}
		else
		{
			sheet = workbook.getSheet(excel.getSheetName());
		}
		 XSSFRow row = sheet.getRow(excel.getRowId());
		 XSSFCell cell = row.getCell(excel.getCellId());
		 FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		 switch (evaluator.evaluateInCell(cell).getCellType())
		 {
		 
         case Cell.CELL_TYPE_NUMERIC:
             cell.setCellValue((double)value);
             excel.setCellType(Cell.CELL_TYPE_NUMERIC);
             break;
         case Cell.CELL_TYPE_STRING:
        	 	cell.setCellValue((String)value);
             break;
         case Cell.CELL_TYPE_FORMULA:
        	 	cell.setCellFormula((String)value);	
        	 	break;
		 } 
         excel.setCellValue(value);
	}
	
	public static Object ReadCellValue(ExcelObject excel) throws Exception
	{
		Object value = null;
		FileInputStream fis = new FileInputStream(new File(excel.getFileName()));
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet;
		if(excel.getSheetId() != -1)
		{
			sheet = workbook.getSheetAt(excel.getSheetId());
		}
		else
		{
			sheet = workbook.getSheet(excel.getSheetName());
		}
		 XSSFRow row = sheet.getRow(excel.getRowId());
		 XSSFCell cell = row.getCell(excel.getCellId());
		 FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		 switch (evaluator.evaluateInCell(cell).getCellType())
		 {
		 
         case Cell.CELL_TYPE_NUMERIC:
             value = cell.getNumericCellValue();
             excel.setCellType(Cell.CELL_TYPE_NUMERIC);
             break;
         case Cell.CELL_TYPE_STRING:
        	 	value = cell.getStringCellValue();
             break;
         case Cell.CELL_TYPE_FORMULA:
        	 	value = cell.getCellFormula().toString();	
        	 	break;
		 } 
		 return value;
	}
	
	public  static boolean isExcel(InputStream i) throws IOException{
	    return (POIFSFileSystem.hasPOIFSHeader(i) || DocumentFactoryHelper.hasOOXMLHeader(i));    		
	}
	
	public static void handleXLSFormat(InputStream fis)
	{
		
	}
	public static void handleXSSFFormat(InputStream fis) throws IOException
	{
		Map<String,String> placeHolders = new HashMap();
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		new ArrayList();
		for(int i =0;i<workbook.getNumberOfSheets();i++)
		{
			String sheetName  = workbook.getSheetName(i);
			XSSFSheet sheet = workbook.getSheet(sheetName);
			Iterator<Row> rowIterator = sheet.iterator();
			while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();
		        //For each row, iterate through each columns
		        Iterator<Cell> cellIterator = row.cellIterator();
		        while(cellIterator.hasNext()) {

		            Cell cell = cellIterator.next();
		            
		            if(cell.getCellType() == Cell.CELL_TYPE_STRING)
		            {
		           
		            String cellvalue = cell.getStringCellValue();
		            if((cellvalue.startsWith("${"))&&(cellvalue.endsWith("}")))
		            {
		            		String cellfinder = "S"+sheet.getSheetName()+"R"+row.getRowNum()+"C"+cell.getColumnIndex();
		            		placeHolders.put(cellfinder, cellvalue);
		            }
		            }
		        }
			}
		}
		System.out.println(">>>>>>"+placeHolders);
	}
	
	public static void FetchKeyValuePair(Workbook workbook)
	{
		Map<String,String> placeHolders = new HashMap();
		new ArrayList();
		for(int i = 0; i<workbook.getNumberOfSheets();i++)
		{
			String sheetName =  workbook.getSheetName(i);
			Sheet sheet = workbook.getSheet(sheetName);
			Iterator<Row> rowIterator = sheet.iterator();
			while(rowIterator.hasNext())
			{
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				while(cellIterator.hasNext())
				{
					Cell cell = cellIterator.next();
					if(cell.getCellType() == Cell.CELL_TYPE_STRING)
					{
						String cellvalue = cell.getStringCellValue();
						 if((cellvalue.startsWith("${"))&&(cellvalue.endsWith("}")))
				            {
				            		String cellfinder = "S"+sheet.getSheetName()+"R"+row.getRowNum()+"C"+cell.getColumnIndex();
				            		placeHolders.put(cellfinder, cellvalue);
				            }
					}
				}
			}
		}
		
	}
	
	//public static void ReadExcelFile() throws Exception
	public static void main(String args[]) throws Exception
	{
		String fileName = "//Users//suresh//Downloads//PricingSheetTest.xlsx";
		InputStream fis = new FileInputStream(new File(fileName));
	     if(! fis.markSupported()) 
	     {
	                fis = new PushbackInputStream(fis, 8);
	     } 
	     handleXSSFFormat(fis);
	  /*   if(isExcel(fis))
	     {
	    	 	handleXLSFormat(fis);
	     }
	     else
	     {
	    	 	handleXSSFFormat(fis);
	     }*/
		//Workbook workbook = WorkbookFactory.create(fis);	
	}
		
}
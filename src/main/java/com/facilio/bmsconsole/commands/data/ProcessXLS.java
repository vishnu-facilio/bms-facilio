package com.facilio.bmsconsole.commands.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ProcessXLS implements Command {

	public boolean execute(Context context) throws Exception {
		
		try {
String FILE_NAME  =null;
            FileInputStream excelFile = new FileInputStream(new File(FILE_NAME));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();

            boolean columnheading = true;
            while (iterator.hasNext()) {

                Row currentRow = iterator.next();
                if(isRowEmpty(currentRow))
                {
                	continue;
                }
                Iterator<Cell> cellIterator = currentRow.iterator();
                if(columnheading)
                {
                	// process header
                	columnheading = false;
                	continue;
                }
                else
                {
                	// process columns
                }

                while (cellIterator.hasNext()) {
                	
                	if(columnheading )
                	{
                		// parse the column heading
                		columnheading = false;
                	}
                	else
                	{
                		// parse the data
                	}
                	
                    Cell currentCell = cellIterator.next();
                    //getCellTypeEnum shown as deprecated for version 3.15
                    //getCellTypeEnum ill be renamed to getCellType starting from version 4.0
                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                        System.out.print(currentCell.getStringCellValue() + "--");
                    } else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        System.out.print(currentCell.getNumericCellValue() + "--");
                    }

                }
                System.out.println();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		finally
		{
			//workbook.
		}

		return false;

	}
	
	public static boolean isRowEmpty(Row row) {
		if (row == null) {
	        return true;
	    }
	    if (row.getLastCellNum() <= 0) {
	        return true;
	    }
	    for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
	        Cell cell = row.getCell(cellNum);
	        if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK && StringUtils.isNotBlank(cell.toString())) {
	            return false;
	        }
	    }
	    return true;
	} 
	
}
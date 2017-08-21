package com.facilio.bmsconsole.commands.data;

import java.io.File;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONArray;

import com.facilio.bmsconsole.actions.ImportMetaInfo;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.transaction.FacilioConnectionPool;

public class ProcessXLS implements Command {

	
	public boolean execute(Context context) throws Exception {
		
		/*
		try {
			String FILE_NAME  =null;
          
            Workbook workbook = WorkbookFactory.create(new File(FILE_NAME));
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
		 */
		
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
	
	public static void processImport(ImportMetaInfo metainfo) throws Exception
	{
		System.out.println("All set for importing "+metainfo);
		
		HashMap fieldMapping = metainfo.getFieldMapping();
		
		Connection conn = FacilioConnectionPool.getInstance().getConnection();
		
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO Energy_Data SET ");
		
		List<String> dbCols = new ArrayList<>();
		Iterator keys = fieldMapping.keySet().iterator();
		int i = 0;
		while (keys.hasNext()) {
			String key = (String) keys.next();
			//String val = ((String[]) fieldMapping.get(key))[0];
			if (i != 0) {
				sql.append(",");
			}
			sql.append(key+"=?");
			
			dbCols.add(key);
			i++;
		}
		
		PreparedStatement pstmt = conn.prepareStatement(sql.toString());
		
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		InputStream ins = fs.readFile(metainfo.getFileId());
		
		HashMap<Integer, String> colIndex = new HashMap<Integer, String>();
		
		Workbook workbook = WorkbookFactory.create(ins);
		Sheet datatypeSheet = workbook.getSheetAt(0);

		Iterator<Row> itr = datatypeSheet.iterator();
		int rowIndex = 0;
		while (itr.hasNext()) {
			Row row = itr.next();
			if (rowIndex == 0) {
				// column heading
				
				Iterator<Cell> cellItr = row.cellIterator();
				int cellIndex = 0;
				while (cellItr.hasNext()) {
					Cell cell = cellItr.next();
					String cellValue = cell.getStringCellValue();
					colIndex.put(cellIndex, cellValue);
					cellIndex++;
				}
			}
			else {
				HashMap<String, Double> colVal = new HashMap<>();
				
				Iterator<Cell> cellItr = row.cellIterator();
				int cellIndex = 0;
				while (cellItr.hasNext()) {
					Cell cell = cellItr.next();
					
					String dbColName = colIndex.get(cellIndex);
					if (dbColName == null) {
						continue;
					}
					
					double val = 0.0;
					if (cell.getCellTypeEnum() == CellType.STRING) {
						
						String cellValue = cell.getStringCellValue();
						try {
							val = Double.parseDouble(cellValue);
						}
						catch (Exception e) {
						}
					}
					else if (cell.getCellTypeEnum() == CellType.NUMERIC || cell.getCellTypeEnum() == CellType.FORMULA) {
						
						val = cell.getNumericCellValue();
					}
					
					colVal.put(dbColName, val);
					
					cellIndex++;
				}
				
				int idx = 1;
				for (String v : dbCols) {
					String cellName = ((String[]) fieldMapping.get(v))[0];
					
					if (colVal.containsKey(cellName)) {
						pstmt.setDouble(idx, colVal.get(cellName));
					}
					else {
						pstmt.setDouble(idx, 0.0);
					}
					
					idx++;
				}
				pstmt.addBatch();
			}
			rowIndex ++;
		}
		
		pstmt.executeBatch();
		pstmt.close();
		conn.close();
		workbook.close();
	}
	
	public static JSONArray getColumnHeadings(File excelfile) throws Exception
	{
		JSONArray columnheadings = new JSONArray();
		
        Workbook workbook = WorkbookFactory.create(excelfile);
        Sheet datatypeSheet = workbook.getSheetAt(0);
        
        Iterator<Row> itr = datatypeSheet.iterator();
        while (itr.hasNext()) {
        	Row row = itr.next();
        	Iterator<Cell> cellItr = row.cellIterator();
        	while (cellItr.hasNext()) {
        		Cell cell = cellItr.next();
        		String cellValue = cell.getStringCellValue();
        		columnheadings.add(cellValue);
        	}
        	break;
        }
		workbook.close();
		
		return columnheadings;
	}
}
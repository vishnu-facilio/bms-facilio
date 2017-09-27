package com.facilio.bmsconsole.commands.data;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
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
		while (keys.hasNext()) {
			String key = (String) keys.next();
			sql.append(key+"=?");
			sql.append(",");
			dbCols.add(key);
		}
		sql.append(ReportsUtil.getAdditionalTimeSql());
		dbCols.addAll(dbCols.size(),ReportsUtil.getAdditionalTimeCols());
		
		PreparedStatement pstmt = conn.prepareStatement(sql.toString());
		
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		InputStream ins = fs.readFile(metainfo.getFileId());
		
		HashMap<Integer, String> colIndex = new HashMap<Integer, String>();
		
		Workbook workbook = WorkbookFactory.create(ins);
		Sheet datatypeSheet = workbook.getSheetAt(0);

		Iterator<Row> itr = datatypeSheet.iterator();
		boolean heading=true;
		while (itr.hasNext()) {
			Row row = itr.next();
			if (heading) {
				// column heading
				
				Iterator<Cell> cellItr = row.cellIterator();
				int cellIndex = 0;
				while (cellItr.hasNext()) {
					Cell cell = cellItr.next();
					String cellValue = cell.getStringCellValue();
					colIndex.put(cellIndex, cellValue);
					cellIndex++;
				}
				heading=false;
				continue;
			}
			HashMap<String, Object> colVal = new HashMap<>();

			Iterator<Cell> cellItr = row.cellIterator();
			int cellIndex = 0;
			while (cellItr.hasNext()) {
				Cell cell = cellItr.next();

				String cellName = colIndex.get(cellIndex);
				if (cellName == null) {
					continue;
				}

				Object val = 0.0;
				if (cell.getCellTypeEnum() == CellType.STRING) {

					val = cell.getStringCellValue();
				}
				else if (cell.getCellTypeEnum() == CellType.NUMERIC || cell.getCellTypeEnum() == CellType.FORMULA) {

					val = cell.getNumericCellValue();
				}

				colVal.put(cellName, val);

				cellIndex++;
			}

			int idx = 1;
			
			HashMap <String,Object> additionalColVals= new HashMap<String,Object>();
			for (String dbColName: dbCols) 
			{
				String cellName = (String)fieldMapping.get(dbColName);
				Object value=colVal.get(cellName);

				if (value!=null || (value=additionalColVals.get(dbColName))!=null) {
					
					//we have to make sure we need to have this column for all the collected data table.. 
					if(dbColName.equalsIgnoreCase("ADDED_TIME"))
					{
						//value=getAddedTime(value);
						additionalColVals= DateTimeUtil.getTimeData(((Double)value).longValue());	
					}
					pstmt.setObject(idx, value);
				}
				else {
					pstmt.setObject(idx, 0.0);
				}
				idx++;
			}

			pstmt.addBatch();
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
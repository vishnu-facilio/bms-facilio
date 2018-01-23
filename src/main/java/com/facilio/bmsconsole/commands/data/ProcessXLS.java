package com.facilio.bmsconsole.commands.data;

import java.io.File;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportMetaInfo;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

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
		System.out.println("All set for importing "+metainfo+" \n" + new Date(System.currentTimeMillis()));
		
		HashMap<String, String> fieldMapping = metainfo.getFieldMapping();			
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		List<ReadingContext> readingsList = new ArrayList<ReadingContext>();
		InputStream ins = fs.readFile(metainfo.getFileId());
		
		HashMap<Integer, String> colIndex = new HashMap<Integer, String>();
		
		Workbook workbook = WorkbookFactory.create(ins);
		
		for(int i=0;i<workbook.getNumberOfSheets();i++) {
			Sheet datatypeSheet = workbook.getSheetAt(i);

			Iterator<Row> itr = datatypeSheet.iterator();
			boolean heading=true;
			int row_no = 0;
			while (itr.hasNext()) {
				row_no++;
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
					else if(cell.getCellTypeEnum() == CellType.BOOLEAN) {
						val = cell.getBooleanCellValue();
					}
					colVal.put(cellName, val);

					cellIndex++;
				}
				System.out.println("Finished loading data from file  "+row_no +" rows . "+metainfo+" \n" + new Date(System.currentTimeMillis()));

				HashMap <String, Object> props = new LinkedHashMap<String,Object>();
				fieldMapping.forEach((key,value) -> 
				{
					Object cellValue=colVal.get(value);
					boolean isfilledByLookup = false;
					if(cellValue != null) {
						FacilioField facilioField = metainfo.getFacilioFieldMapping(metainfo.getModule().getName()).get(key);
						if(facilioField.getDataTypeEnum().equals(FieldType.LOOKUP)) {
							LookupField lookupField = (LookupField) facilioField;
							List<Map<String, Object>> lookupPropsList = getLookupProps(lookupField,cellValue);
							if(lookupPropsList != null) {
								Map<String, Object> lookupProps = lookupPropsList.get(0);
								props.put(key, lookupProps);
								isfilledByLookup = true;
							}
						}
					}
					if(!isfilledByLookup) {
						props.put(key, cellValue);
					}
				});
				System.out.println("Loading  ReadingContext   . "+metainfo + new Date(System.currentTimeMillis()));

				ReadingContext reading = FieldUtil.getAsBeanFromMap(props, ReadingContext.class);
				reading.setParentId(metainfo.getAssetId());
				readingsList.add(reading);
				
				System.out.println("Finished commit data  for assetid ="+metainfo.getAssetId() +" \n"+ new Date(System.currentTimeMillis()));

			}
		}
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		InsertRecordBuilder<ReadingContext> readingBuilder = new InsertRecordBuilder<ReadingContext>()
				.moduleName(metainfo.getModule().getName())
				.fields(bean.getAllFields(metainfo.getModule().getName()))
				.addRecords(readingsList);
		readingBuilder.save();
		workbook.close();
	}

	public static List<Map<String, Object>> getLookupProps(LookupField lookupField,Object value) {
		
		try {
			System.out.println("getLookupProps -- "+lookupField.getColumnName() +" facilioField.getModule() - "+lookupField.getLookupModule().getTableName() +" with value -- "+value);
			
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			ArrayList<FacilioField> fieldsList= bean.getAllFields(lookupField.getLookupModule().getName());
			fieldsList.add(FieldFactory.getIdField());
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fieldsList)
					.table(lookupField.getLookupModule().getTableName())
					.andCustomWhere("LOWER(NAME) = ?", value.toString().toLowerCase());
			
			List<Map<String, Object>> props = selectBuilder.get();
			
			if(props.isEmpty()) {
				
				HashMap <String, Object> insertProps = new LinkedHashMap<String,Object>();
				
				insertProps.put("name", value);
				
				ReadingContext reading = FieldUtil.getAsBeanFromMap(insertProps, ReadingContext.class);
				
				InsertRecordBuilder<ReadingContext> readingBuilder = new InsertRecordBuilder<ReadingContext>()
						.moduleName(lookupField.getLookupModule().getName())
						.fields(bean.getAllFields(lookupField.getLookupModule().getName()));
				
				Long id = readingBuilder.insert(reading);
				if(id != null) {
					System.out.println("inserted with ID --"+id);
					Map<String, Object> propMap = new HashMap<>();
					propMap.put("id", id);
					propMap.put("name", value);
					props.add(propMap);
				}
			}
			return props;			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
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
package com.facilio.bmsconsole.commands.data;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.ModuleLocalIdUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.modules.*;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessXLS extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(ProcessXLS.class.getName());
	private static org.apache.log4j.Logger log = LogManager.getLogger(ProcessXLS.class.getName());
	public boolean executeCommand(Context context) throws Exception {
		
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
	        if (cell != null && cell.getCellType() != CellType.BLANK && StringUtils.isNotBlank(cell.toString())) {
	            return false;
	        }
	    }
	    return true;
	} 
	
	public static void processImport(ImportProcessContext importProcessContext) throws Exception
	{
		
		
		HashMap<String, String> fieldMapping = importProcessContext.getFieldMapping();			
		FileStore fs = FacilioFactory.getFileStore();
		List<ReadingContext> readingsList = new ArrayList<ReadingContext>();
		InputStream ins = fs.readFile(importProcessContext.getFileId());
		
		HashMap<Integer, String> headerIndex = new HashMap<Integer, String>();
		
		Workbook workbook = WorkbookFactory.create(ins);
		
		for(int i=0;i<workbook.getNumberOfSheets();i++) {
			Sheet datatypeSheet = workbook.getSheetAt(i);

			Iterator<Row> rowItr = datatypeSheet.iterator();
			boolean heading=true;
			int row_no = 0;
			while (rowItr.hasNext()) {
				row_no++;
				LOGGER.severe("row_no -- "+row_no);
				Row row = rowItr.next();
				
				if(row.getPhysicalNumberOfCells() <= 0) {
					break;
				}
				if (heading) {
					// column heading
					
					Iterator<Cell> cellItr = row.cellIterator();
					int cellIndex = 0;
					while (cellItr.hasNext()) {
						Cell cell = cellItr.next();
						String cellValue = cell.getStringCellValue();
						headerIndex.put(cellIndex, cellValue);
						cellIndex++;
					}
					heading=false;
					continue;
				}
				HashMap<String, Object> colVal = new HashMap<>();

				Iterator<Cell> cellItr = row.cellIterator();
				while (cellItr.hasNext()) {
					Cell cell = cellItr.next();

					String cellName = headerIndex.get(cell.getColumnIndex());
					if (cellName == null) {
						continue;
					}

					Object val = 0.0;
					if (cell.getCellType() == CellType.STRING) {

						val = cell.getStringCellValue();
					}
					else if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
						if(DateUtil.isCellDateFormatted(cell)) {
							Date date = cell.getDateCellValue();
							 Instant date1 = date.toInstant();
							 val = date1.getEpochSecond()*1000;
						}
						else {
							val = cell.getNumericCellValue();
						}
					}
					else if(cell.getCellType() == CellType.BOOLEAN) {
						val = cell.getBooleanCellValue();
					}
					else {
						val = null;
					}
					colVal.put(cellName, val);

				}
				
				if(colVal.values() == null || colVal.values().isEmpty()) {
					break;
				}
				else {
					boolean isAllNull = true;
					for( Object value:colVal.values()) {
						if(value != null) {
							isAllNull = false;
							break;
						}
					}
					if(isAllNull) {
						break;
					}
				}
				
				LOGGER.severe("row -- "+row_no+" colVal --- "+colVal);
				

				HashMap <String, Object> props = new LinkedHashMap<String,Object>();
				
				if(importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET) || importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ENERGY_METER) || importProcessContext.getModule().getName().equals("kdm")) {
					
					Long spaceId = ImportAPI.getSpaceID(importProcessContext,colVal,fieldMapping);
					if( importProcessContext.getModule().getName().equals("kdm")) {
						props.put("site", spaceId);
						fieldMapping.remove("site");
					}
					else {
						 props.put("space", spaceId);
					}
					 if(importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ENERGY_METER)) {
						 props.put("purposeSpace", spaceId);
					 }
					 props.put("resourceType", ResourceType.ASSET.getValue());
					 
					colVal.remove("site");
					colVal.remove("building");
					colVal.remove("floor");
					colVal.remove("spaceName");
					
					
				}
				
				LOGGER.severe("\n\n importProcessContext.getFacilioFieldMapping() --- "+ importProcessContext.getFacilioFieldMapping());
				
				LOGGER.severe("\n\n colVal --- "+ colVal);
				fieldMapping.forEach((key,value) -> 
				{
					Object cellValue=colVal.get(value);
					boolean isfilledByLookup = false;
					
					if(cellValue != null && !cellValue.toString().equals("")) {
						
						FacilioField facilioField = null;
						try {
							LOGGER.severe("\n\n key --- "+key);
							facilioField = importProcessContext.getFacilioFieldMapping().get(key);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							log.info("Exception occurred ", e);
						}
						if(facilioField.getDataTypeEnum().equals(FieldType.LOOKUP)) {
							LookupField lookupField = (LookupField) facilioField;
							
							boolean isSkipSpecialLookup = false;
							
							try {
								if((importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET) || importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ENERGY_METER)) && lookupField.getName().equals("department")) {
									isSkipSpecialLookup = true;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							if(facilioField.getDisplayType().equals(FacilioField.FieldDisplayType.LOOKUP_SIMPLE) || isSkipSpecialLookup) {
								List<Map<String, Object>> lookupPropsList = getLookupProps(lookupField,cellValue);
								if(lookupPropsList != null) {
									Map<String, Object> lookupProps = lookupPropsList.get(0);
									props.put(key, lookupProps);
									isfilledByLookup = true;
								}
							}
							else if(facilioField.getDisplayType().equals(FacilioField.FieldDisplayType.LOOKUP_POPUP)) {
								Map<String, Object> specialLookupList = getSpecialLookupProps(lookupField,cellValue);
								if(specialLookupList != null) {
									props.put(key, specialLookupList);
									isfilledByLookup = true;
								}
							}
						}
						
						try {
							if(facilioField.getDataTypeEnum().equals(FieldType.DATE_TIME) || facilioField.getDataTypeEnum().equals(FieldType.DATE)) {
								if(!(cellValue instanceof Long)) {
									
									Object millis = null;
									try {
										millis = DateTimeUtil.getTime(cellValue.toString().trim(), "dd-MM-yyyy HH:mm");
									}
									catch(Exception e) {
										millis = cellValue;
										LOGGER.log(Level.SEVERE, e.getMessage());
									}
									props.put(key, millis);
									isfilledByLookup = true;
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if(!isfilledByLookup) {
						props.put(key, cellValue);
					}
				});
				LOGGER.severe("props -- "+props);
				ReadingContext reading = FieldUtil.getAsBeanFromMap(props, ReadingContext.class);
				reading.setParentId(importProcessContext.getAssetId());
				readingsList.add(reading);
				
			}
		}
		
		ProcessXLS.populateData(importProcessContext, readingsList);
		
		workbook.close();
		
		LOGGER.severe("IMPORT DONE");
	}
	
	public static void populateData(ImportProcessContext importProcessContext,List<ReadingContext> readingsEntireList) throws Exception {
		
		String moduleName=importProcessContext.getModule().getName();
		if(importProcessContext.getModule().getTypeEnum() == ModuleType.READING || importProcessContext.getModule().getTypeEnum() == ModuleType.LIVE_FORMULA || importProcessContext.getModule().getTypeEnum() == ModuleType.SCHEDULED_FORMULA || importProcessContext.getModule().getTypeEnum() == ModuleType.SYSTEM_SCHEDULED_FORMULA) {
			
			Map<String, List<ReadingContext>> readingMap= Collections.singletonMap(moduleName, readingsEntireList);
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.HISTORY_READINGS,true);
			context.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS,false);
			context.put(FacilioConstants.ContextNames.READINGS_MAP , readingMap);
			context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.IMPORT);
			FacilioChain importDataChain = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
			importDataChain.execute(context);	
		}
		else {
			
			int insertLimit = 10000;
			int splitSize = (readingsEntireList.size()/insertLimit) + 1;
			LOGGER.severe("splitSize ----- "+splitSize);
			for(int i=0 ; i < splitSize; i++) {
				
				int fromValue = i*insertLimit;
				int toValue = (i*insertLimit) + insertLimit;
				if(toValue >= readingsEntireList.size()) {
					toValue = readingsEntireList.size();
				}
				List<ReadingContext> readingsList = readingsEntireList.subList(fromValue , toValue);
				
				if(importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ENERGY_METER)) {
					for(ReadingContext reading :readingsList) {
						reading.addReading("resourceType", ResourceContext.ResourceType.ASSET.getValue());
					}
				}
				if(importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.WORK_ORDER)) {
					for(ReadingContext reading :readingsList) {
						reading.addReading("sourceType", TicketContext.SourceType.WEB_ORDER.getIndex());
					}
				}
				ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = bean.getModule(moduleName);
				InsertRecordBuilder<ReadingContext> readingBuilder = new InsertRecordBuilder<ReadingContext>()
						.moduleName(moduleName)
						.fields(bean.getAllFields(moduleName))
						.addRecords(readingsList);
				
				if(ModuleLocalIdUtil.isModuleWithLocalId(module)) {
					readingBuilder.withLocalId();
				}
				readingBuilder.save();
				
				Thread.sleep(10000L);
				
			}
			
		}
	}

	public static List<Map<String, Object>> getLookupProps(LookupField lookupField,Object value) {
		
		try {
			LOGGER.severe("getLookupProps -- "+lookupField.getColumnName() +" facilioField.getModule() - "+lookupField.getLookupModule().getTableName() +" with value -- "+value);
			
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			ArrayList<FacilioField> fieldsList = new ArrayList<FacilioField>();
			
			fieldsList= new ArrayList<> (bean.getAllFields(lookupField.getLookupModule().getName()));
			fieldsList.add(FieldFactory.getIdField(lookupField.getLookupModule()));
			/*fieldsList.add(FieldFactory.getOrgIdField(lookupField.getLookupModule()));*/
			fieldsList.add(FieldFactory.getModuleIdField(lookupField.getLookupModule()));
			
			String collumnName = "NAME";
			String fieldName = "name";
			if(lookupField.getModule().getName().equals(FacilioConstants.ContextNames.WORK_ORDER)) {
				if(lookupField.getName().equals("status")) {
					collumnName = "STATUS";
					fieldName = "status";
				}
				else if (lookupField.getName().equals("priority")) {
					collumnName = "PRIORITY";
					fieldName = "priority";
				}
			}
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fieldsList)
					.table(lookupField.getLookupModule().getTableName())
					.andCustomWhere("LOWER("+collumnName+") = ?", value.toString().toLowerCase());
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(lookupField.getLookupModule()));
			
			List<Map<String, Object>> props = selectBuilder.get();
			
			if(props.isEmpty()) {
				
				HashMap <String, Object> insertProps = new LinkedHashMap<String,Object>();
				
				insertProps.put(fieldName, value);
				insertProps.put("orgId", AccountUtil.getCurrentOrg().getId());
				insertProps.put("moduleId", lookupField.getLookupModule().getModuleId());
				
				if(lookupField.getLookupModule().getName().equals(FacilioConstants.ContextNames.ASSET_CATEGORY)) {
					insertProps.put("type", AssetCategoryContext.AssetCategoryType.MISC.getIntVal());
				}
				GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
						.table(lookupField.getLookupModule().getTableName())
						.fields(fieldsList);
				
				
				insertRecordBuilder.addRecord(insertProps);
				
				insertRecordBuilder.save();
				LOGGER.severe("insertProps --- "+insertProps);
				Long id = (Long) insertProps.get("id");
				
				if(id != null) {
					LOGGER.severe("inserted with ID --"+id);
					props.add(insertProps);
				}
			}
			return props;			
		}
		catch(Exception e) {
			log.info("Exception occurred ", e);
		}
		return null;
	}
	
	
	public static Map<String, Object> getSpecialLookupProps(LookupField lookupField,Object value) {
		
		try {
			
			switch (lookupField.getName()) {
				case "assignedBy":
				case "assignedTo":
				case "requester": {
					AppDomain appDomain = IAMAppUtil.getAppDomain(AccountUtil.getCurrentOrg().getDomain() + "." + FacilioProperties.getOccupantAppDomain());
					User user = AccountUtil.getUserBean().getUser(value.toString(), appDomain.getIdentifier());
					Map<String, Object> prop = FieldUtil.getAsProperties(user);
					return prop;
					
				}
				case "resource":
				case "servernumber": {
					long assetid = AssetsAPI.getAssetId(value.toString(), lookupField.getOrgId());
					AssetContext asset = AssetsAPI.getAssetInfo(assetid);
					
					Map<String, Object> prop1 = FieldUtil.getAsProperties(asset);
					return prop1;
				}
				
			}
					
		}
		catch(Exception e) {
			log.info("Exception occurred ", e);
		}
		return null;
	}
	
}











package com.facilio.bmsconsole.commands;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.yaml.snakeyaml.Yaml;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleFactory;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;

public class DemoSingleRollUpYearlyCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(DemoSingleRollUpYearlyCommand.class.getName());
	private static final String DEFAULT_DB_CONF_PATH = FacilioUtil.normalizePath("conf/demorolluptables.yml");

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		JSONObject jobProps = BmsJobUtil.getJobProps(321l, "DemoSingleRollUpYearlyJob");
		
		if(jobProps == null) {
			LOGGER.error("DemoSingleRollUpYearlyCommand empty jobProps");
			return false;
		}
		
		LOGGER.info("DemoSingleRollUpYearlyCommand jobProps :"+jobProps);	
		Integer startWeek =Integer.valueOf(String.valueOf((Long)jobProps.get("startWeek")));
		Integer endWeek =Integer.valueOf(String.valueOf((Long)jobProps.get("endWeek")));
		Boolean doDelete = (Boolean)jobProps.get("doDelete");
		
		if(startWeek == null || endWeek== null) {
			LOGGER.error("DemoSingleRollUpYearlyCommand - Please provide startWeek and endWeek for rollup");
			return false;
		}
		doDelete = doDelete == null ? Boolean.FALSE : doDelete;  

		
		long jobStartTime = System.currentTimeMillis();
		Long orgId=(Long) context.get(ContextNames.DEMO_ROLLUP_JOB_ORG);
		ZonedDateTime currentZdt = (ZonedDateTime) context.get(ContextNames.START_TIME);
		if(currentZdt == null) {
			currentZdt = DateTimeUtil.getDateTime();
		}
		ZonedDateTime thisYearStartZdt = DateTimeUtil.getYearStartTimeOf(currentZdt);
		
		ZonedDateTime presentWeekStartZdt = DateTimeUtil.getWeekStartTimeOf(currentZdt);
		int presentWeek = DateTimeUtil.getWeekOfWeekBasedYear(presentWeekStartZdt);
		LOGGER.info("DemoSingleRollUpYearlyCommand presentWeekStartZdt :"+presentWeekStartZdt + " presentWeekNo. : "+presentWeek);	
		List<FacilioModule> readingModules = fetchAllReadingModules();
		HashMap<String, List<String>> readingTableNamesVsColumns = fetchDemoReadingTableNamesVsColumns();
		
		if(doDelete) {
			LOGGER.info("DemoSingleRollUpYearlyCommand Starting Deletion between weeks: startWeekNo. :"+startWeek + " endWeekNo. : "+endWeek);
			
			ZonedDateTime thisYearDelStartWeekStartZdt = DateTimeUtil.getWeekStartTimeOf(thisYearStartZdt.with(DateTimeUtil.getWeekFields().weekOfWeekBasedYear(), startWeek)); // 40,41l
			ZonedDateTime thisYearDelEndWeekStartZdt = DateTimeUtil.getWeekStartTimeOf(thisYearStartZdt.with(DateTimeUtil.getWeekFields().weekOfWeekBasedYear(), endWeek)); // 43l
			ZonedDateTime thisYearDelEndWeekEndZdt = DateTimeUtil.getWeekEndTimeOf(thisYearDelEndWeekStartZdt);

			long thisYearDelStartWeekStart = thisYearDelStartWeekStartZdt.toInstant().toEpochMilli();
			long thisYearDelEndWeekEnd = thisYearDelEndWeekEndZdt.toInstant().toEpochMilli();
			deleteTtimeColumns(thisYearDelStartWeekStart,thisYearDelEndWeekEnd,readingModules,readingTableNamesVsColumns);
		}
		else {
			
			LOGGER.info("DemoSingleRollUpYearlyCommand Starting Rollup between weeks: startWeekNo. :"+startWeek + " endWeekNo. : "+endWeek);

			//38 to 43
			for(int currentWeek = startWeek; currentWeek <= endWeek; currentWeek++) 
			{	
				ZonedDateTime thisYearWeekStartZdt = DateTimeUtil.getWeekStartTimeOf(thisYearStartZdt.with(DateTimeUtil.getWeekFields().weekOfWeekBasedYear(), currentWeek));
				ZonedDateTime lastYearWeekStartZdt = DateTimeUtil.getWeekStartTimeOf(thisYearStartZdt.minusYears(2).with(DateTimeUtil.getWeekFields().weekOfWeekBasedYear(), currentWeek));
				ZonedDateTime lastYearWeekEndZdt = DateTimeUtil.getWeekEndTimeOf(lastYearWeekStartZdt);
				
				long lastYearWeekStart = lastYearWeekStartZdt.toInstant().toEpochMilli();
				long lastYearWeekEnd = lastYearWeekEndZdt.toInstant().toEpochMilli();
				long thisYearWeekStart = thisYearWeekStartZdt.toInstant().toEpochMilli();
				long weekDiff = (thisYearWeekStart - lastYearWeekStart);
				
				try {
					rollUpTtimeColumns(lastYearWeekStart,lastYearWeekEnd,weekDiff,readingModules,readingTableNamesVsColumns);
					LOGGER.info("DemoSingleRollUpYearlyCommand lastYearWeekStart :"+lastYearWeekStart + " lastYearWeekEnd : "+lastYearWeekEnd + 
							"thisYearWeekStart :"+thisYearWeekStart + " weekDiff : " +weekDiff + " CurrentWeekNo.: "+currentWeek);
				}
				catch(Exception e) {
					LOGGER.error("###Exception occurred in DemoSingleRollUpYearlyCommand. CurrentWeek is:  " + currentWeek);
					throw e;
				} 	
			}	
		}
				
		//MachineStalk to be done from 18thWeek to 25th in firstcut.
		LOGGER.info("####DemoSingleRollUpYearlyCommand time taken to complete : " + (System.currentTimeMillis()-jobStartTime));
		
		return false;
	}
	
	private void rollUpTtimeColumns(long lastYearWeekStart, long lastYearWeekEnd, long weekDiff, List<FacilioModule> readingModules, HashMap<String, List<String>> readingTableNamesVsColumns) throws Exception 
	{
		if(readingModules == null || readingModules.isEmpty() || readingTableNamesVsColumns == null || readingTableNamesVsColumns.isEmpty() || lastYearWeekStart < 0 || weekDiff < 0) {
			return;
		}
		
		LOGGER.info("####DemoSingleRollUpYearlyCommand ReadingModules size : " + readingModules.size());
		int i=0;
		long totalSize = 0;
		
		for(FacilioModule readingModule :readingModules) 
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String readingModuleTableName = readingModule.getTableName();
			
			if(readingTableNamesVsColumns.containsKey(readingModuleTableName)) 
			{
				try {
					LOGGER.info("####DemoSingleRollUpYearlyCommand Entered ReadingModuleName : " + readingModule.getName() +" ReadingModule Count: " + ++i);

					List<String> readingModuleColumnNames = readingTableNamesVsColumns.get(readingModuleTableName);
					Map<String, FacilioField> allModuleFields = FieldFactory.getAsMap(modBean.getAllFields(readingModule.getName()));
					
					SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
		    				.module(readingModule)
		    				.select(modBean.getAllFields(readingModule.getName()))
		    				.table(readingModuleTableName)
		    				.beanClass(ReadingContext.class)
		    				.andCondition(CriteriaAPI.getCondition(allModuleFields.get("ttime"), lastYearWeekStart+","+lastYearWeekEnd, DateOperators.BETWEEN))
		    				.skipUnitConversion();
					
					SelectRecordsBuilder.BatchResult<ReadingContext> bs = selectBuilder.getInBatches("ID", 20000);
					while (bs.hasNext()) 
					{
					   try {
						   List<ReadingContext> readings = bs.get();
						   if(readings != null && !readings.isEmpty()) 
						   {
							   for (ReadingContext reading: readings) 
								{
									for (String columnName :readingModuleColumnNames) 
									{
										switch(columnName) {
											case "ACTUAL_TTIME":
												reading.setActualTtime(reading.getActualTtime()+weekDiff);
												break;
											case "TTIME":
												reading.setTtime(reading.getTtime()+weekDiff);
												break;
											case "CREATED_TTIME":
												if(reading.getSysCreatedTime() != -1) {
													reading.setSysCreatedTime(reading.getSysCreatedTime()+weekDiff);
												}
												break;
											default: break;
										}
									}	
								}
								
								InsertRecordBuilder<ReadingContext> insertBuilder = new InsertRecordBuilder<ReadingContext>()
										.module(readingModule)
					    				.table(readingModuleTableName)
					    				.fields(modBean.getAllFields(readingModule.getName()));					
								insertBuilder.addRecords(readings);
								insertBuilder.save();
								
								LOGGER.info("###DemoSingleRollUpYearlyCommand " + readings.size() + " of rows updated in  " + readingModuleTableName + " successfully. lastYearWeekStart: "
										+ lastYearWeekStart + " lastYearWeekEnd: " + lastYearWeekEnd + " weekDiff: " + weekDiff);
								
								totalSize += readings.size();
						   }
						} catch (Exception e) {
							LOGGER.error("###Exception occurred in  DemoSingleRollUpYearlyCommand. TableName is:  " + readingModuleTableName + "Exception: " +e);
							throw e;
						}
					}	
				}
				catch (Exception e) {
					LOGGER.error("###Exception occurred in DemoSingleRollUpYearlyCommand. ReadingModule: " + readingModule + " readingModuleTableName " + readingModuleTableName + "Exception: " +e);
				}	
			}	
		}
		
		LOGGER.info("####DemoSingleRollUpYearlyCommand Current Week ReadingModules Inserted recordSize: " +totalSize+ " lastYearWeekStart :"+lastYearWeekStart + " lastYearWeekEnd : "+lastYearWeekEnd + 
			 " weekDiff : " +weekDiff);						
	}
	
	private List<FacilioModule> fetchAllReadingModules() throws Exception 
	{	
		List<FacilioField> selectFields = new ArrayList<FacilioField>();
		for(FacilioField field: FieldFactory.getModuleFields()) {
			if(field != null && field.getName() != null && !field.getName().equals("createdBy")) {
				selectFields.add(field);
			}
		}
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getModuleModule().getTableName())
				.select(selectFields);
		
		List<Map<String, Object>> props = builder.get();
		List<FacilioModule> readingModules = new ArrayList<FacilioModule>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				FacilioModule module = FieldUtil.getAsBeanFromMap(prop, FacilioModule.class);
				if(module.getTypeEnum().isReadingType()) {
					readingModules.add(module);
				}
			}			
		}
		return readingModules;
	}
	
	private HashMap<String, List<String>> fetchDemoReadingTableNamesVsColumns() {
		ClassLoader classLoader = DemoRollUpYearlyCommand.class.getClassLoader();
        Yaml yaml = new Yaml();
        Map<String ,List<Map<String,Object>>> readingTableList = null;
        HashMap<String, List<String>> readingTableNamesVsColumns = new HashMap<String, List<String>>();
        
        try(InputStream inputStream = classLoader.getResourceAsStream(DEFAULT_DB_CONF_PATH)) {
        	readingTableList = yaml.load(inputStream);
        }
        catch (Exception e) {
            LOGGER.error("Error occurred while reading demoRollUptables conf file. "+e.getMessage(), e);
            throw new RuntimeException("Error occurred while reading demoRollUptables conf file. "+e.getMessage());
        }
        
        for (Entry<String, List<Map<String, Object>>> tableList : readingTableList.entrySet()) {
			for (Map<String, Object> itr : tableList.getValue()) {
				String tableNamekey = (String) itr.get("tableName");
				List<String> columnList = (List<String>) itr.get("columns");
				readingTableNamesVsColumns.put(tableNamekey, columnList);		
			}
        }
        return readingTableNamesVsColumns;
	}

	private void deleteTtimeColumns(long delStart, long delEnd, List<FacilioModule> readingModules, HashMap<String, List<String>> readingTableNamesVsColumns) throws Exception 
	{
		if(readingModules == null || readingModules.isEmpty() || readingTableNamesVsColumns == null || readingTableNamesVsColumns.isEmpty() || delStart < 0 || delEnd < 0) {
			return;
		}
		
		LOGGER.info("####DemoSingleRollUpYearlyCommand ReadingModules size : " + readingModules.size());
		int i=0;		
		for(FacilioModule readingModule :readingModules) 
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String readingModuleTableName = readingModule.getTableName();
			
			if(readingTableNamesVsColumns.containsKey(readingModuleTableName)) 
			{
				try {
					LOGGER.info("####DemoSingleRollUpYearlyCommand Entered ReadingModuleName : " + readingModule.getName() +" ReadingModule Count: " + ++i);
					Map<String, FacilioField> allModuleFields = FieldFactory.getAsMap(modBean.getAllFields(readingModule.getName()));
													
					while (true) {					   
					   SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
			    				.module(readingModule)
			    				.select(Collections.singletonList(FieldFactory.getIdField(readingModule)))
			    				.table(readingModuleTableName)
			    				.beanClass(ReadingContext.class)
			    				.andCondition(CriteriaAPI.getCondition(allModuleFields.get("ttime"), delStart+","+delEnd, DateOperators.BETWEEN))
			    				.orderBy("ID")
								.limit(30000);
					   
						List<Map<String, Object>> props = selectBuilder.getAsProps();

						if (CollectionUtils.isEmpty(props)) {
							break;
						}
						List<Long> ids = props.stream().map(p -> (Long) p.get("id")).collect(Collectors.toList());
						try {
						DeleteRecordBuilder<ReadingContext> deleteBuilder = new DeleteRecordBuilder<ReadingContext>()
								.module(readingModule)
								.table(readingModule.getTableName());
						int deletionCount = deleteBuilder.batchDeleteById(ids);
						
						LOGGER.info("###DemoSingleRollUpYearlyCommand " + deletionCount + " of rows deletionCount in  " + readingModuleTableName + " successfully. delStart: "
								+ delStart + " delEnd: " + delEnd);
						}
						catch (Exception e) {
							LOGGER.error("###Exception occurred in Delete DemoSingleRollUpYearlyCommand. TableName is:  " + readingModuleTableName + "Exception: " +e);
						}
					}		
				}
				catch (Exception e) {
					LOGGER.error("###Exception occurred in DemoSingleRollUpYearlyCommand. ReadingModule: " + readingModule + " readingModuleTableName " + readingModuleTableName + "Exception: " +e);
				}	
			}	
		}
		
		LOGGER.info("####DemoSingleRollUpYearlyCommand Current Week ReadingModules Deleted");						
	}

}

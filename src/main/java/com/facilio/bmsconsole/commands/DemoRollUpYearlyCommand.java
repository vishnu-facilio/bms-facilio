package com.facilio.bmsconsole.commands;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.yaml.snakeyaml.Yaml;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.AlarmActivityType;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;

public class DemoRollUpYearlyCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(DemoRollUpYearlyCommand.class.getName());
	private static final String DEFAULT_DB_CONF_PATH = "conf/demorolluptables.yml"; 
	private static final String DEFAULT_DB_ALARM_TABLE_CONF_PATH = "conf/demoRollUpAlarmTables.yml";
	@Override
	public boolean executeCommand(Context context) throws Exception {

		long jobStartTime = System.currentTimeMillis();
		long orgId=(long) context.get(ContextNames.DEMO_ROLLUP_JOB_ORG);
		ZonedDateTime currentZdt = (ZonedDateTime) context.get(ContextNames.START_TIME);
		ZonedDateTime thisYearWeekStartZdt = DateTimeUtil.getWeekStartTimeOf(currentZdt);
		int currentWeek = thisYearWeekStartZdt.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
		ZonedDateTime lastYearWeekStartZdt = DateTimeUtil.getWeekStartTimeOf(thisYearWeekStartZdt.minusYears(1).with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, currentWeek));
		ZonedDateTime lastYearWeekEndZdt = DateTimeUtil.getWeekEndTimeOf(lastYearWeekStartZdt);
		long lastYearWeekStart = lastYearWeekStartZdt.toInstant().toEpochMilli();
		long lastYearWeekEnd = lastYearWeekEndZdt.toInstant().toEpochMilli();
		long thisYearWeekStart = thisYearWeekStartZdt.toInstant().toEpochMilli();
		long weekDiff = (thisYearWeekStart - lastYearWeekStart);

		ClassLoader classLoader = DemoRollUpYearlyCommand.class.getClassLoader();
        Yaml yaml = new Yaml();
        Map<String ,List<Map<String,Object>>> readingTableList = null;
        Map<String ,List<Map<String,Object>>> alarmsTableList = null;
        try(InputStream inputStream = classLoader.getResourceAsStream(DEFAULT_DB_CONF_PATH)) {
        	readingTableList = yaml.load(inputStream);
        }
        catch (Exception e) {
            LOGGER.error("Error occurred while reading demoRollUptables conf file. "+e.getMessage(), e);
            throw new RuntimeException("Error occurred while reading demoRollUptables conf file. "+e.getMessage());
        }
        try(InputStream inputStream = classLoader.getResourceAsStream(DEFAULT_DB_ALARM_TABLE_CONF_PATH)) {
        	alarmsTableList = yaml.load(inputStream);
        }
        catch (Exception e) {
            LOGGER.error("Error occurred while reading demoRollUptables conf file. "+e.getMessage(), e);
            throw new RuntimeException("Error occurred while reading demoRollUptables conf file. "+e.getMessage());
        }
        try (Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
        	updateTtimeColumns(readingTableList,weekDiff,lastYearWeekStart,lastYearWeekEnd,orgId,conn);
        	System.out.println("laststart :"+lastYearWeekStart + " latend : "+lastYearWeekEnd);
        	lastYearWeekStart = DateTimeUtil.addWeeks(lastYearWeekStart, -1);
        	lastYearWeekEnd = DateTimeUtil.addWeeks(lastYearWeekEnd, -1);
        	System.out.println("laststart :"+lastYearWeekStart + " latend : "+lastYearWeekEnd);
        	weekDiff = (thisYearWeekStart - lastYearWeekStart);
        	updateTtimeColumns(alarmsTableList,weekDiff,lastYearWeekStart,lastYearWeekEnd,orgId,conn);
        }
		catch(Exception e) {
			throw e;
		}
        
        try {	
//        	propagateAlarmOccurrenceToAlarms(alarmsTableList,lastYearWeekStart,lastYearWeekEnd,weekDiff,orgId);
		}
		catch (Exception e) {
			LOGGER.error("###Exception occurred in propagating AlarmOccurrence DemoRollUpYearlyJob. TableName is:  " + alarmsTableList);
			throw e;
		}
        
		LOGGER.info("####DemoRollUpYearlyJob time taken to complete : " + (System.currentTimeMillis()-jobStartTime));
		return false;
	}

	private void updateTtimeColumns(Map<String, List<Map<String, Object>>> tableListToUpdate, long weekDiff, long lastYearWeekStart, long lastYearWeekEnd, long orgId, Connection conn) throws Exception {
		for (Entry<String, List<Map<String, Object>>> tableList : tableListToUpdate.entrySet()) {
			for (Map<String, Object> itr : tableList.getValue()) {
				String tableNamekey = (String) itr.get("tableName");
				String primaryColumn = (String) itr.get("primaryColumn");
				List<String> valueList = (List<String>) itr.get("columns");
				StringBuilder sql = new StringBuilder();
				sql.append("UPDATE  ").append(tableNamekey).append("  SET  ");
				for (String columnName : valueList) {
					sql.append(columnName).append(" = ");
					sql.append("(");
					sql.append(columnName).append(" + ").append(weekDiff).append(")");
					sql.append(",");
				}
				sql.replace(sql.length() - 1, sql.length(), " ");
				sql.append(" WHERE ORGID = ").append(orgId).append(" AND ").append(primaryColumn)
				.append("  BETWEEN ").append(lastYearWeekStart).append(" AND ").append(lastYearWeekEnd);
				try (PreparedStatement pstmt = conn.prepareStatement(sql.toString());) {
					int count = pstmt.executeUpdate();
					LOGGER.info("###DemoRollUpYearlyJob " + count + " of rows updated in  " + tableNamekey + "  successfully");
				} catch (Exception e) {
					LOGGER.error("###Exception occurred in  DemoRollUpYearlyJob. TableName is:  " + tableNamekey);
					throw e;
				}	
			}
		}
	}
	
	private void propagateAlarmOccurrenceToAlarms(Map<String, List<Map<String, Object>>> tableListToUpdate, long lastYearWeekStart, long lastYearWeekEnd,long weekDiff, long orgId) throws Exception {
		
		for (Entry<String, List<Map<String, Object>>> tableList : tableListToUpdate.entrySet()) 
		{
			for (Map<String, Object> itr : tableList.getValue()) 
			{
				String tableNamekey = (String) itr.get("tableName");
				String primaryColumn = (String) itr.get("primaryColumn");
				
				if(tableNamekey.equals("AlarmOccurrence") && primaryColumn.equals("CREATED_TIME") && orgId != -1) 
				{
					AccountUtil.setCurrentAccount(orgId);
					List<BaseAlarmContext> baseAlarms = getDistinctAlarms(lastYearWeekStart+weekDiff,lastYearWeekEnd+weekDiff);
					if(baseAlarms != null && !baseAlarms.isEmpty()) 
					{		
						Map<Long, AlarmOccurrenceContext> latestAlarmOccurrenceMap = new HashMap<Long, AlarmOccurrenceContext>();
						for(BaseAlarmContext baseAlarm:baseAlarms) {
							AlarmOccurrenceContext latestAlarmoccurrence = NewAlarmAPI.getLatestAlarmOccurance(baseAlarm);
							if(latestAlarmoccurrence != null) {
								latestAlarmOccurrenceMap.put(latestAlarmoccurrence.getId(), latestAlarmoccurrence);
							}
						}
					
						NewEventAPI.updateLatestOccurrenceFromEvent(latestAlarmOccurrenceMap);
						
						if(latestAlarmOccurrenceMap != null && MapUtils.isNotEmpty(latestAlarmOccurrenceMap)) 
						{	
							for(Long id:latestAlarmOccurrenceMap.keySet()) 
							{
								AlarmOccurrenceContext latestOccurrence = latestAlarmOccurrenceMap.get(id);
								if(latestOccurrence != null && latestOccurrence.getAlarm() != null && latestOccurrence.getAlarm().getId() != -1) 
								{
									BaseAlarmContext baseAlarm = latestOccurrence.getAlarm();
									baseAlarm.setLastOccurrence(latestOccurrence);
									baseAlarm.setLastOccurrenceId(id);
									
									if(latestOccurrence.getCreatedTime() != -1 && latestOccurrence.getLastOccurredTime() != -1) {
										baseAlarm.setLastOccurredTime(latestOccurrence.getLastOccurredTime());
										baseAlarm.setLastCreatedTime(latestOccurrence.getCreatedTime());								
									}
									if(latestOccurrence.getAcknowledgedTime() != -1) {
										baseAlarm.setAcknowledgedTime(latestOccurrence.getAcknowledgedTime());
									}	
									if (latestOccurrence.getSeverity().equals(AlarmAPI.getAlarmSeverity("Clear"))) {
										baseAlarm.setLastClearedTime(latestOccurrence.getLastOccurredTime());
									}	
									NewAlarmAPI.updateBaseAlarmBuilder(baseAlarm);
								}
							}
						}
						
						LOGGER.info("###DemoRollUpYearlyJob latestAlarmOccurrenceMap " + latestAlarmOccurrenceMap + " baseAlarms " +baseAlarms);
					}
				}		       
			}
		}	
	}
	
   
   private List<BaseAlarmContext> getDistinctAlarms(long lastYearWeekStart, long lastYearWeekEnd) throws Exception
   {
		if(lastYearWeekStart != -1 && lastYearWeekEnd != -1) {	
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        	FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
        	List<FacilioField> allFields = modBean.getAllFields(module.getName());	
    		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
        	
        	FacilioField alarmField = fieldMap.get("alarm");   		
    		FacilioField selectDistinctField = alarmField.clone();
    		selectDistinctField.setColumnName("DISTINCT("+alarmField.getCompleteColumnName()+")");	
    		selectDistinctField.setModule(null);

    		SelectRecordsBuilder<AlarmOccurrenceContext> selectbuilder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
    				.select(modBean.getAllFields(module.getName()))
    				.module(module)
    				.beanClass(AlarmOccurrenceContext.class)
    				.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", lastYearWeekStart+","+lastYearWeekEnd, DateOperators.BETWEEN))
    				.select(Collections.singletonList(selectDistinctField))
    				.setAggregation();
			List<AlarmOccurrenceContext> alarmOccurrenceList = selectbuilder.get();
			
			List<BaseAlarmContext> baseAlarmList = new ArrayList<BaseAlarmContext>();
			if(alarmOccurrenceList != null && !alarmOccurrenceList.isEmpty()) {
				for(AlarmOccurrenceContext alarmOccurrence:alarmOccurrenceList) 
				{
					if(alarmOccurrence.getAlarm() != null && alarmOccurrence.getAlarm().getId() != -1) {
						baseAlarmList.add(alarmOccurrence.getAlarm());
					}
				}
				return baseAlarmList;
			}	
		}
		return null;
   }
}

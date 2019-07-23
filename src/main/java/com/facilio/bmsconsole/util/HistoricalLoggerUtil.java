package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class HistoricalLoggerUtil {
	
	
	public static void addHistoricalLogger(List<HistoricalLoggerContext> historicalLoggerList) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getHistoricalLoggerModule().getTableName())
				.fields(FieldFactory.getHistoricalLoggerFields());
		
		for(HistoricalLoggerContext historicalLogger:historicalLoggerList)
		{
		Map<String, Object> props = FieldUtil.getAsProperties(historicalLogger);
		insertBuilder.addRecord(props);
		}
		insertBuilder.save();
		
//		for(HistoricalLoggerContext historicalLogger:historicalLoggerList) {
//		historicalLogger.setId((Long) props.get("id"));
//		}
	}
	
	public static void addHistoricalLogger(HistoricalLoggerContext historicalLogger) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getHistoricalLoggerModule().getTableName())
				.fields(FieldFactory.getHistoricalLoggerFields());
	
		Map<String, Object> props = FieldUtil.getAsProperties(historicalLogger);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		historicalLogger.setId((Long) props.get("id"));
		
	}
	
	
	public static HistoricalLoggerContext getHistoricalLogger(long parentassetId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getHistoricalLoggerFields())
				.table(ModuleFactory.getHistoricalLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", ""+parentassetId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			HistoricalLoggerContext historicalLogger = FieldUtil.getAsBeanFromMap(props.get(0), HistoricalLoggerContext.class);
			return historicalLogger;
		}
		return null;
	}
	
	public static List<HistoricalLoggerContext> getParentHistoricalLogger() throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getHistoricalLoggerFields())
				.table(ModuleFactory.getHistoricalLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("LOGGER_GROUP_ID", "loggerGroupId", "", CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition("DEPENDENT_ID", "dependentId", "", CommonOperators.IS_EMPTY))
				.orderBy("STATUS");
		
				List<Map<String, Object>> props = selectBuilder.get();
				
				List<HistoricalLoggerContext> historicalLoggers = new ArrayList<HistoricalLoggerContext>();
				
				List<Long> resourceIds = new ArrayList<Long>();
				if (props != null && !props.isEmpty()) {
					for(Map<String, Object> prop : props ) {
						HistoricalLoggerContext historicalLogger = FieldUtil.getAsBeanFromMap(prop, HistoricalLoggerContext.class);
						historicalLoggers.add(historicalLogger);
						resourceIds.add(historicalLogger.getParentId());
					}
				}
				
				Map<Long, ResourceContext> resourcesMap = ResourceAPI.getResourceAsMapFromIds(resourceIds);
				
				for(HistoricalLoggerContext historicalLogger :historicalLoggers) {
					historicalLogger.setParentResourceContext(resourcesMap.get(historicalLogger.getParentId()));
				}
				return historicalLoggers;
	}
	
	public static List<HistoricalLoggerContext> getGroupedHistoricalLogger(Long loggerGroupId) throws Exception {

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
			.select(FieldFactory.getHistoricalLoggerFields())
			.table(ModuleFactory.getHistoricalLoggerModule().getTableName())
			.andCondition(CriteriaAPI.getCondition("LOGGER_GROUP_ID", "loggerGroupId", ""+loggerGroupId, NumberOperators.EQUALS));

			List<Map<String, Object>> props = selectBuilder.get();
			List<HistoricalLoggerContext> historicalLoggers = new ArrayList<HistoricalLoggerContext>();
			
			List<Long> resourceIds = new ArrayList<Long>();
			
			if (props != null && !props.isEmpty()) {
				for(Map<String, Object> prop : props ) {
					HistoricalLoggerContext historicalLogger = FieldUtil.getAsBeanFromMap(prop, HistoricalLoggerContext.class);
					historicalLoggers.add(historicalLogger);
					resourceIds.add(historicalLogger.getParentId());
				}
			}

			Map<Long, ResourceContext> resourcesMap = ResourceAPI.getResourceAsMapFromIds(resourceIds);
			
			for(HistoricalLoggerContext historicalLogger :historicalLoggers) {
				historicalLogger.setParentResourceContext(resourcesMap.get(historicalLogger.getParentId()));
			}
			return historicalLoggers;

	}
	
	public static List<HistoricalLoggerContext> getActiveHistoricalLogger(List<Long> dependentIds) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getHistoricalLoggerFields())
				.table(ModuleFactory.getHistoricalLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getConditionFromList("DEPENDENT_ID", "dependentId", dependentIds, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("STATUS", "status", ""+ HistoricalLoggerContext.Status.IN_PROGRESS.getIntVal(), NumberOperators.EQUALS));
				
				List<Map<String, Object>> props = selectBuilder.get();
				if (props != null && !props.isEmpty()) {
					List<HistoricalLoggerContext> historicalLoggers = FieldUtil.getAsBeanListFromMapList(props, HistoricalLoggerContext.class);
					return historicalLoggers;
				}
				return null;
	}
	
	public static List<HistoricalLoggerContext> getInProgressHistoricalLogger() throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getHistoricalLoggerFields())
				.table(ModuleFactory.getHistoricalLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("STATUS", "status", ""+ HistoricalLoggerContext.Status.IN_PROGRESS.getIntVal(), NumberOperators.EQUALS));
				
				List<Map<String, Object>> props = selectBuilder.get();
				if (props != null && !props.isEmpty()) {
					List<HistoricalLoggerContext> historicalLoggers = FieldUtil.getAsBeanListFromMapList(props, HistoricalLoggerContext.class);
					return historicalLoggers;
				}
				return null;
	}
	
	public static List<HistoricalLoggerContext> getAllHistoricalLogger() throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getHistoricalLoggerFields())
				.table(ModuleFactory.getHistoricalLoggerModule().getTableName());
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<HistoricalLoggerContext> historicalLoggers = FieldUtil.getAsBeanListFromMapList(props, HistoricalLoggerContext.class);
			return historicalLoggers;
		}
		return null;
	}

	public static void updateHistoricalLogger(HistoricalLoggerContext historicalLogger) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getHistoricalLoggerModule().getTableName())
				.fields(FieldFactory.getHistoricalLoggerFields())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+historicalLogger.getId(), NumberOperators.EQUALS));

		Map<String, Object> props = FieldUtil.getAsProperties(historicalLogger);
		updateBuilder.update(props);
	}
	
	
	public static void deleteHistoricalLogger(long id) throws SQLException {
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getHistoricalLoggerModule().getTableName())
		.andCondition(CriteriaAPI.getCondition("ID", "id", ""+id, NumberOperators.EQUALS));
		
	}
	
}

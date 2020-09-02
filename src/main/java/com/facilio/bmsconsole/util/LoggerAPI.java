package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.LoggerContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class LoggerAPI {
	
		public static void addLogger(FacilioModule loggerModule, List<FacilioField> fields, List<LoggerContext> loggerContextList) throws Exception {
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(loggerModule.getTableName())
					.fields(fields);
			
			for(LoggerContext loggerContext : loggerContextList)
			{
				Map<String, Object> props = FieldUtil.getAsProperties(loggerContext);
				insertBuilder.addRecord(props);
			}
			insertBuilder.save();

		}
		
		public static void addLogger(FacilioModule loggerModule, List<FacilioField> fields, LoggerContext loggerContext) throws Exception {
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(loggerModule.getTableName())
					.fields(fields);
		
			Map<String, Object> props = FieldUtil.getAsProperties(loggerContext);
			insertBuilder.addRecord(props);
			insertBuilder.save();
			
			loggerContext.setId((Long) props.get("id"));			
		}
		
		
		public static LoggerContext getLoggerById (FacilioModule loggerModule, List<FacilioField> fields, long loggerId) throws Exception {
						
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(loggerModule.getTableName())
					.andCondition(CriteriaAPI.getCondition("ID", "id", ""+loggerId, NumberOperators.EQUALS));
				
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {			
				LoggerContext loggerContext = FieldUtil.getAsBeanFromMap(props.get(0), LoggerContext.class);
				return loggerContext;
			}
			return null;
		}
		
		public static LoggerContext getLoggerByParent(FacilioModule loggerModule, List<FacilioField> fields, long parentId) throws Exception {
			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(loggerModule.getTableName())
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), ""+ parentId, NumberOperators.EQUALS));
			
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {			
				LoggerContext loggerContext = FieldUtil.getAsBeanFromMap(props.get(0), LoggerContext.class);
				return loggerContext;
			}
			return null;
		}
		
		public static List<LoggerContext> getActiveParentLoggers(FacilioModule loggerModule, List<FacilioField> fields,long parentId) throws Exception {
			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(loggerModule.getTableName())
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), ""+ parentId, NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition("STATUS", "status", ""+ LoggerContext.Status.IN_PROGRESS.getIntVal(), NumberOperators.EQUALS));
			
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				List<LoggerContext>  LoggerContextList = FieldUtil.getAsBeanListFromMapList(props, LoggerContext.class);
				return LoggerContextList;
			}
			return null;
		}
		
		public static List<LoggerContext> getActiveParentAndResourceLoggers(FacilioModule loggerModule, List<FacilioField> fields, long parentId, List<Long> resourceIds) throws Exception {
			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(loggerModule.getTableName())
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), ""+ parentId, NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), resourceIds, NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition("STATUS", "status", ""+ LoggerContext.Status.IN_PROGRESS.getIntVal(), NumberOperators.EQUALS));
			
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				List<LoggerContext>  LoggerContextList = FieldUtil.getAsBeanListFromMapList(props, LoggerContext.class);
				return LoggerContextList;
			}
			return null;
		}
		
		public static List<LoggerContext> getCurrentActiveParentAndResourceLogger(FacilioModule loggerModule, List<FacilioField> fields, long parentId, long resourceId) throws Exception {
			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(loggerModule.getTableName())
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), ""+ parentId, NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), ""+resourceId, NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition("STATUS", "status", ""+ LoggerContext.Status.IN_PROGRESS.getIntVal(), NumberOperators.EQUALS));
			
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				List<LoggerContext>  LoggerContextList = FieldUtil.getAsBeanListFromMapList(props, LoggerContext.class);
				return LoggerContextList;
			}
			return null;
		}
		
		public static List<LoggerContext> getActiveDependentParentAndResourceLogger(FacilioModule loggerModule, List<FacilioField> fields, long dependentId, long resourceId) throws Exception {
			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);		
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(loggerModule.getTableName())
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("dependentId"), ""+ dependentId, NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), ""+resourceId, NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition("STATUS", "status", ""+ LoggerContext.Status.IN_PROGRESS.getIntVal(), NumberOperators.EQUALS));
			
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				List<LoggerContext>  LoggerContextList = FieldUtil.getAsBeanListFromMapList(props, LoggerContext.class);
				return LoggerContextList;
			}
			return null;
		}
		
		public static List<LoggerContext> getActiveDependentParentAndResourceLogger(FacilioModule loggerModule, List<FacilioField> fields, List<Long> dependentIds, long resourceId) throws Exception {
			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(loggerModule.getTableName())
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("dependentId"), dependentIds, NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), ""+resourceId, NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition("STATUS", "status", ""+ LoggerContext.Status.IN_PROGRESS.getIntVal(), NumberOperators.EQUALS));
			
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				List<LoggerContext>  LoggerContextList = FieldUtil.getAsBeanListFromMapList(props, LoggerContext.class);
				return LoggerContextList;
			}
			return null;
		}

		public static Collection<LoggerContext> getAllParentLoggerAPI(FacilioModule loggerModule, List<FacilioField> loggerFields, long parentId) throws Exception {
			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(loggerFields);
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(loggerFields)
					.table(loggerModule.getTableName())
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), ""+ parentId, NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition("LOGGER_GROUP_ID", "loggerGroupId", "id", NumberOperators.EQUALS))
					.orderBy("STATUS,CREATED_TIME DESC");
					
			List<Map<String, Object>> props = selectBuilder.get();
			List<LoggerContext> parentHistoricalLoggerContextList = new ArrayList<LoggerContext>();
			if (props != null && !props.isEmpty()) {
				parentHistoricalLoggerContextList = FieldUtil.getAsBeanListFromMapList(props, LoggerContext.class);
			}
			
			LinkedHashMap<Long, LoggerContext> parentHistoricalLoggerContextMap = new LinkedHashMap<Long, LoggerContext>();
			for(LoggerContext parentHistoricalLoggerContext :parentHistoricalLoggerContextList)
			{
				parentHistoricalLoggerContextMap.put(parentHistoricalLoggerContext.getLoggerGroupId(), parentHistoricalLoggerContext);
			}
			
			FacilioField countField = BmsAggregateOperators.CommonAggregateOperator.COUNT.getSelectField(fieldMap.get("loggerGroupId"));
			countField.setName("count");
			
			List<FacilioField> selectFields = new ArrayList<FacilioField>();
			selectFields.add(fieldMap.get("loggerGroupId"));
			selectFields.add(countField);
	
			if(fieldMap.get("actionCount") != null)
			{
				FacilioField sumField = BmsAggregateOperators.NumberAggregateOperator.SUM.getSelectField(fieldMap.get("actionCount"));
				sumField.setName("sum");						
				selectFields.add(sumField);					
			}
			
			selectBuilder = new GenericSelectRecordBuilder()
					.select(selectFields)
					.table(loggerModule.getTableName())
//					.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), ""+ parentId, NumberOperators.EQUALS))
					.groupBy("LOGGER_GROUP_ID");
			
			
			List<Map<String, Object>> propsList = selectBuilder.get();
								
//					if(propsList != null && !propsList.isEmpty())
//					{
//						for(Map<String, Object> prop :propsList)
//						{
//							LoggerContext parentHistoricalLoggerContext = parentHistoricalLoggerContextMap.get((long) prop.get("loggerGroupId"));
//							if(prop.get("count") != null) {
//								parentHistoricalLoggerContext.setResourceLogCount((long) prop.get("count"));
//							}
//							if(prop.get("sum") != null)
//							{
//								parentHistoricalLoggerContext.setTotalChildActionCount(Integer.valueOf(String.valueOf(prop.get("sum"))));
//							}			
//						}		
//					}	
			
			if(propsList != null && !propsList.isEmpty())
			{
				Map<Long, Long> loggerGroupCountMap = new HashMap <Long, Long>();
				for(Map<String, Object> prop : propsList ) {
					long loggerGroupId = (long) prop.get("loggerGroupId");
					if(prop.get("count") != null) {
						loggerGroupCountMap.put(loggerGroupId, (long) prop.get("count"));
					}
				}
				
				for(Long loggerGroupId:parentHistoricalLoggerContextMap.keySet())
				{
					Long loggerCount = loggerGroupCountMap.get(loggerGroupId);
					LoggerContext parentLoggerContext = parentHistoricalLoggerContextMap.get(loggerGroupId);
					if(loggerCount !=null) {
						parentLoggerContext.setResourceLogCount(loggerCount);
					}
				
					parentLoggerContext.setStatus(LoggerContext.Status.IN_PROGRESS.getIntVal());

					List<LoggerContext> activeChildLoggers = getGroupedInProgressLoggers(loggerModule, loggerFields, loggerGroupId);
					if(activeChildLoggers == null || activeChildLoggers.isEmpty())
					{
						parentLoggerContext.setStatus(LoggerContext.Status.RESOLVED.getIntVal());
						parentLoggerContext.setResolvedLogCount(parentLoggerContext.getResourceLogCount());					
					}
					else
					{
						if(activeChildLoggers != null && parentLoggerContext.getResourceLogCount() > 0 && parentLoggerContext.getResourceLogCount() >= activeChildLoggers.size())
						{
							long resolvedResourceCount = parentLoggerContext.getResourceLogCount() - activeChildLoggers.size();
							parentLoggerContext.setResolvedLogCount(resolvedResourceCount);
						}
					}					
				}
			}				
			return parentHistoricalLoggerContextMap.values();				
		}

		public static List<LoggerContext> getGroupedLogger(FacilioModule loggerModule, List<FacilioField> fields,Long loggerGroupId) throws Exception {
	
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(loggerModule.getTableName())
				.andCondition(CriteriaAPI.getCondition("LOGGER_GROUP_ID", "loggerGroupId", ""+loggerGroupId, NumberOperators.EQUALS))
//				.andCondition(CriteriaAPI.getCondition(fieldMap.get("dependentId"), "", CommonOperators.IS_EMPTY))
				.orderBy("STATUS,CREATED_TIME DESC");
	
			List<Map<String, Object>> props = selectBuilder.get();
			List<LoggerContext> LoggerContexts = new ArrayList<LoggerContext>();
			
			List<Long> resourceIds = new ArrayList<Long>();
			
			if (props != null && !props.isEmpty()) {
				for(Map<String, Object> prop : props ) {
					LoggerContext loggerContext = FieldUtil.getAsBeanFromMap(prop, LoggerContext.class);
					LoggerContexts.add(loggerContext);
					resourceIds.add(loggerContext.getResourceId());
				}
				
				List<ResourceContext> resources = ResourceAPI.getResources(resourceIds,true);
				Map<Long, ResourceContext> resourcesMap = new LinkedHashMap<Long, ResourceContext>();
				
				for(ResourceContext resource:resources)
				{
					resourcesMap.put(resource.getId(), resource);
				}
				
				for(LoggerContext loggerContext :LoggerContexts) {
					loggerContext.setResourceContext(resourcesMap.get(loggerContext.getResourceId()));
				}
			}
			return LoggerContexts;
	
		}

		public static List<LoggerContext> getGroupedInProgressLoggers(FacilioModule loggerModule, List<FacilioField> fields,Long loggerGroupId) throws Exception {
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(loggerModule.getTableName())
				.andCondition(CriteriaAPI.getCondition("LOGGER_GROUP_ID", "loggerGroupId", ""+loggerGroupId, NumberOperators.EQUALS));
			
			Criteria subCriteria = new Criteria();
			subCriteria.addOrCondition(CriteriaAPI.getCondition("STATUS", "status", ""+ LoggerContext.Status.IN_PROGRESS.getIntVal(), NumberOperators.EQUALS));
			subCriteria.addOrCondition(CriteriaAPI.getCondition("STATUS", "status", ""+ LoggerContext.Status.FAILED.getIntVal(), NumberOperators.EQUALS));
			
			selectBuilder.andCriteria(subCriteria);
		
			List<Map<String, Object>> props = selectBuilder.get();				
			if (props != null && !props.isEmpty()) {
				List<LoggerContext> LoggerContextList = FieldUtil.getAsBeanListFromMapList(props, LoggerContext.class);
				return LoggerContextList;	
			}			
			return null;
		}
		
		public static List<LoggerContext> getAllInProgressLoggers(FacilioModule loggerModule, List<FacilioField> fields) throws Exception {
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(loggerModule.getTableName())
					.andCondition(CriteriaAPI.getCondition("STATUS", "status", ""+ LoggerContext.Status.IN_PROGRESS.getIntVal(), NumberOperators.EQUALS));
					
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				List<LoggerContext> loggerContexts = FieldUtil.getAsBeanListFromMapList(props, LoggerContext.class);
				return loggerContexts;
			}
			return null;
		}
		
		public static List<LoggerContext> getAllLoggers(FacilioModule loggerModule, List<FacilioField> fields) throws Exception {
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(loggerModule.getTableName());
					
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				List<LoggerContext> loggerContexts = FieldUtil.getAsBeanListFromMapList(props, LoggerContext.class);
				return loggerContexts;
			}
			return null;
		}
	
		public static void updateLogger(FacilioModule loggerModule, List<FacilioField> fields, LoggerContext loggerContext) throws Exception {
			
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(loggerModule.getTableName())
					.fields(fields)
					.andCondition(CriteriaAPI.getCondition("ID", "id", ""+loggerContext.getId(), NumberOperators.EQUALS));
	
			Map<String, Object> props = FieldUtil.getAsProperties(loggerContext);
			updateBuilder.update(props);
		}
		
		
		public static void deleteLogger(FacilioModule loggerModule, long id) throws SQLException {
			
			GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
			
			deleteRecordBuilder
				.table(loggerModule.getTableName())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+id, NumberOperators.EQUALS));
			
		}
		

}

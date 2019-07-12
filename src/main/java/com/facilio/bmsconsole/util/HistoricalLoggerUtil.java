package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class HistoricalLoggerUtil {
	
	
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

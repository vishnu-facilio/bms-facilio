package com.facilio.bmsconsole.util;

import java.sql.Time;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.TimelogContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;

public class TimerLogUtil {

	public static TimelogContext getLastTimerActiveLog(FacilioModule module, long parentId, long fromStatusId) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(FieldFactory.getTimeLogFields(module))
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), module))
				.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("FROM_STATUS_ID", "fromStatusId", String.valueOf(fromStatusId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("END_TIME", "endTime", null, CommonOperators.IS_EMPTY))
				.orderBy("ID DESC")
				.limit(1);
		TimelogContext timelog = FieldUtil.getAsBeanFromMap(builder.fetchFirst(),TimelogContext.class);
		return timelog;
	}
	
	public static Map<String, Object> getLastTimerLog(FacilioModule module, long parentId) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(FieldFactory.getTimeLogFields(module))
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), module))
				.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS))
				.orderBy(module.getTableName() + ".END_TIME DESC")
				.limit(1);
		
		return builder.fetchFirst();
	}

	public static void addOrUpdate(FacilioModule m, TimelogContext timeLogProp) throws Exception {
		if (timeLogProp.getId() > 0) {
			GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
					.table(m.getTableName())
					.fields(FieldFactory.getTimeLogFields(m))
					.andCondition(CriteriaAPI.getIdCondition((long) timeLogProp.getId(), m));
			Map<String,Object> timelog = FieldUtil.getAsProperties(timeLogProp);
			builder.update(timelog);
		} else {
			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
					.table(m.getTableName())
					.fields(FieldFactory.getTimeLogFields(m));
			Map<String,Object> timelog = FieldUtil.getAsProperties(timeLogProp);
			builder.insert(timelog);
		}
	}
}

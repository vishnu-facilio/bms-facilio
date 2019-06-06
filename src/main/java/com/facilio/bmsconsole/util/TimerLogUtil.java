package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;

import java.util.Map;

public class TimerLogUtil {

	public static Map<String, Object> getLastTimerActiveLog(FacilioModule module, long parentId) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(FieldFactory.getTimeLogFields(module))
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), module))
				.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("END_TIME", "endTime", null, CommonOperators.IS_EMPTY));
		
		return builder.fetchFirst();
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

	public static void addOrUpdate(FacilioModule m, Map<String, Object> timeLogProp) throws Exception {
		if (timeLogProp.containsKey("id") && ((long) timeLogProp.get("id")) > 0) {
			GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
					.table(m.getTableName())
					.fields(FieldFactory.getTimeLogFields(m))
					.andCondition(CriteriaAPI.getIdCondition((long) timeLogProp.get("id"), m));
			builder.update(timeLogProp);
		} else {
			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
					.table(m.getTableName())
					.fields(FieldFactory.getTimeLogFields(m));
			builder.insert(timeLogProp);
		}
	}
}

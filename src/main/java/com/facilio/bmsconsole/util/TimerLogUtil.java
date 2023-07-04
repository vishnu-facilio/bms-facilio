package com.facilio.bmsconsole.util;

import java.util.HashMap;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TimelogContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.v3.util.V3Util;
import org.json.simple.JSONObject;

public class TimerLogUtil {

	public static TimelogContext getLastTimerActiveLog(FacilioModule module, long parentId, long fromStatusId) throws Exception {
		ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<TimelogContext> builder = new SelectRecordsBuilder<TimelogContext>()
				.module(module)
				.beanClass(TimelogContext.class)
				.select(moduleBean.getAllFields(module.getName()))
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), module))
				.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parent", String.valueOf(parentId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("FROM_STATUS_ID", "fromStatus", String.valueOf(fromStatusId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("END_TIME", "endTime", null, CommonOperators.IS_EMPTY))
				.orderBy("ID DESC")
				.limit(1);
		TimelogContext timelog = builder.fetchFirst();
		return timelog;
	}
	
	public static Map<String, Object> getLastTimerLog(FacilioModule module, long parentId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(modBean.getAllFields(module.getName()))
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), module))
				.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS))
				.orderBy(module.getTableName() + ".END_TIME DESC")
				.limit(1);
		
		return builder.fetchFirst();
	}

	public static void addOrUpdate(FacilioModule m, TimelogContext timeLogProp) throws Exception {
		JSONObject internal = new JSONObject();
		internal.put("internal",true);
		if (timeLogProp.getId() > 0) {
			V3Util.processAndUpdateSingleRecord(m.getName(), timeLogProp.getId(),FieldUtil.getAsProperties(timeLogProp),internal,
						null, null, null, null,null,null, null,null);
		} else {
			V3Util.createRecord(m,FieldUtil.getAsProperties(timeLogProp),internal,null);
		}
	}
}

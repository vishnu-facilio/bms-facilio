package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class ShiftAPI {
	public static List<ShiftContext> getAllShifts() throws Exception {
		List<ShiftContext> shifts = new ArrayList<>();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getShiftField())
				.table(ModuleFactory.getShiftModule().getTableName())
				.andCustomWhere(ModuleFactory.getShiftModule().getTableName() + ".ORGID=?", AccountUtil.getCurrentOrg().getOrgId())
				.orderBy("name");
		List<Map<String, Object>> props = selectBuilder.get();
		StringJoiner j = new StringJoiner(",");
		if (props != null && !props.isEmpty()) {
			ShiftContext s = FieldUtil.getAsBeanFromMap(props.get(0), ShiftContext.class);
			j.add(String.valueOf(s.getBusinessHoursId()));
			shifts.add(s);
		}
		
		String businessHoursTable = ModuleFactory.getBusinessHoursModule().getTableName();
		String singleDayTable = ModuleFactory.getSingleDayBusinessHourModule().getTableName();
		selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getSingleDayBusinessHoursFields())
				.table(businessHoursTable)
				.innerJoin(singleDayTable)
				.on(businessHoursTable+".ID = "+singleDayTable+".PARENT_ID")
				.andCustomWhere(businessHoursTable+".ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCondition(CriteriaAPI.getCondition("PARENT_ID","parentId", j.toString(), NumberOperators.EQUALS))
				.orderBy("dayOfWeek");
		
		props = selectBuilder.get();
		
		List<BusinessHourContext> days = new ArrayList<>();
		Map<Long, List<BusinessHourContext>> parentIdVsContext = new HashMap<>();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop: props) {
				BusinessHourContext b = FieldUtil.getAsBeanFromMap(prop, BusinessHourContext.class);
				long id = b.getParentId();
				if (!parentIdVsContext.containsKey(id)) {
					parentIdVsContext.put(id, new ArrayList<>());
				}
				parentIdVsContext.get(id).add(b);
			}
		}
		
		shifts.forEach(s -> {
			List<BusinessHourContext> b = parentIdVsContext.get(s.getBusinessHoursId());
			s.setDays(b);
		});
		return shifts;
	}
}

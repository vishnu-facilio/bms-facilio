package com.facilio.bmsconsole.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.SingleDayBusinessHourContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

public class BusinessHoursAPI {
	
	public static long addBusinessHours(List<SingleDayBusinessHourContext> businessHours) throws SQLException, RuntimeException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> props = new HashMap<>();
		props.put("orgId", OrgInfo.getCurrentOrgInfo().getOrgid());
		GenericInsertRecordBuilder businessHoursBuilder = new GenericInsertRecordBuilder()
																.table(ModuleFactory.getBusinessHoursModule().getTableName())
																.fields(FieldFactory.getBusinessHoursFields())
																.addRecord(props);
		
		businessHoursBuilder.save();
		long parentId = (long) props.get("id");
		
		GenericInsertRecordBuilder singleDayBuilder = new GenericInsertRecordBuilder()
																.table(ModuleFactory.getSingleDayBusinessHourModule().getTableName())
																.fields(FieldFactory.getSingleDayBusinessHoursFields());
		
		for(SingleDayBusinessHourContext singleDay : businessHours) {
			singleDay.setParentId(parentId);
			singleDayBuilder.addRecord(FieldUtil.getAsProperties(singleDay));
		}
		singleDayBuilder.save();
		
		return parentId;
	}
	
	public static List<SingleDayBusinessHourContext> getBusinessHours(long id) throws Exception {
		
		String businessHoursTable = ModuleFactory.getBusinessHoursModule().getTableName();
		String singleDayTable = ModuleFactory.getSingleDayBusinessHourModule().getTableName();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getSingleDayBusinessHoursFields())
														.table(businessHoursTable)
														.innerJoin(singleDayTable)
														.on(businessHoursTable+".ID = "+singleDayTable+".PARENT_ID")
														.andCustomWhere(businessHoursTable+".ID = ?", id)
														.orderBy("dayOfWeek");
		
		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			List<SingleDayBusinessHourContext> businessHours = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				businessHours.add(FieldUtil.getAsBeanFromMap(prop, SingleDayBusinessHourContext.class));
			}
			return businessHours;
		}
		
		return null;
	}

}

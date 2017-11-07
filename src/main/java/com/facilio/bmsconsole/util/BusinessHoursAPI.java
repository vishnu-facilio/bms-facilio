package com.facilio.bmsconsole.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.BusinessHoursList;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

public class BusinessHoursAPI {
	
	public static long addBusinessHours(List<BusinessHourContext> businessHours) throws SQLException, RuntimeException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
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
		
		for(BusinessHourContext singleDay : businessHours) {
			singleDay.setParentId(parentId);
			singleDayBuilder.addRecord(FieldUtil.getAsProperties(singleDay));
		}
		singleDayBuilder.save();
		
		return parentId;
	}
	
	public static BusinessHoursList getBusinessHours(long id) throws Exception {
		
		String businessHoursTable = ModuleFactory.getBusinessHoursModule().getTableName();
		String singleDayTable = ModuleFactory.getSingleDayBusinessHourModule().getTableName();
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getSingleDayBusinessHoursFields())
														.table(businessHoursTable)
														.innerJoin(singleDayTable)
														.on(businessHoursTable+".ID = "+singleDayTable+".PARENT_ID")
														.andCustomWhere(businessHoursTable+".ORGID = ? AND "+businessHoursTable+".ID = ?", orgId, id)
														.orderBy("dayOfWeek");
		
		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			BusinessHoursList businessHours = new BusinessHoursList();
			businessHours.setOrgId(orgId);
			businessHours.setId(id);
			for(Map<String, Object> prop : props) {
				businessHours.add(FieldUtil.getAsBeanFromMap(prop, BusinessHourContext.class));
			}
			return businessHours;
		}
		
		return null;
	}

}

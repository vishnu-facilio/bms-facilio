package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.BusinessHoursList;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessHoursAPI {
	
	public static long addBusinessHours(List<BusinessHourContext> businessHours) throws SQLException, RuntimeException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> props = new HashMap<>();
		props.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
		GenericInsertRecordBuilder businessHoursBuilder = new GenericInsertRecordBuilder()
																.table(ModuleFactory.getBusinessHoursModule().getTableName())
																.fields(FieldFactory.getBusinessHoursFields())
																.addRecord(props);
		
		businessHoursBuilder.save();
		long parentId = (long) props.get("id");
		
		GenericInsertRecordBuilder singleDayBuilder = new GenericInsertRecordBuilder()
																.table(ModuleFactory.getSingleDayBusinessHourModule().getTableName())
																.fields(FieldFactory.getSingleDayBusinessHoursFields());
		
		List<Map<String, Object>> singleDayProps = new ArrayList<>();
		for(BusinessHourContext singleDay : businessHours) {
			singleDay.setParentId(parentId);
			singleDayProps.add(FieldUtil.getAsProperties(singleDay));
		}
		singleDayBuilder.addRecords(singleDayProps);
		singleDayBuilder.save();
		
		int len = businessHours.size();
		for (int i = 0; i < len; ++i) {
			businessHours.get(i).setId((long) singleDayProps.get(i).get("id"));
		}
		
		return parentId;
	}
	
	public static BusinessHoursList getBusinessHours(long id) throws Exception {
		String businessHoursTable = ModuleFactory.getBusinessHoursModule().getTableName();
		String singleDayTable = ModuleFactory.getSingleDayBusinessHourModule().getTableName();
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
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
	
	public static void deleteBusinessHours(long id) throws Exception {
		FacilioModule businessHoursTable = ModuleFactory.getBusinessHoursModule();
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(businessHoursTable.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(id, businessHoursTable))
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getOrgId(), businessHoursTable));
		
		builder.delete();
	}

}

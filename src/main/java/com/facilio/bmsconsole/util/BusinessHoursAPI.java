package com.facilio.bmsconsole.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Chain;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.BusinessHoursList;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;

public class BusinessHoursAPI {

	public static long addBusinessHours(BusinessHoursContext businessHours) throws Exception {

		Map<String, Object> props = new HashMap<>();
		props.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
		props.put("name", businessHours.getName());
		GenericInsertRecordBuilder businessHoursBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getBusinessHoursModule().getTableName())
				.fields(FieldFactory.getBusinessHoursFields()).addRecord(props);

		businessHoursBuilder.save();
		long parentId = (long) props.get("id");
		GenericInsertRecordBuilder singleDayBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getSingleDayBusinessHourModule().getTableName())
				.fields(FieldFactory.getSingleDayBusinessHoursFields());

		List<Map<String, Object>> singleDayProps = new ArrayList<>();
		List<BusinessHourContext> singleDayList = businessHours.getSingleDaybusinessHoursList();
		for (BusinessHourContext singleDay : singleDayList) {
			singleDay.setParentId(parentId);
			singleDayProps.add(FieldUtil.getAsProperties(singleDay));
		}
		singleDayBuilder.addRecords(singleDayProps);
		singleDayBuilder.save();

		int len = singleDayList.size();
		for (int i = 0; i < len; ++i) {
			singleDayList.get(i).setId((long) singleDayProps.get(i).get("id"));
		}
		return parentId;
	}

	public static long addBusinessHours(List<BusinessHourContext> businessHours) throws SQLException, RuntimeException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> props = new HashMap<>();
		props.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
		GenericInsertRecordBuilder businessHoursBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getBusinessHoursModule().getTableName())
				.fields(FieldFactory.getBusinessHoursFields()).addRecord(props);
		businessHoursBuilder.save();
		long parentId = (long) props.get("id");

		GenericInsertRecordBuilder singleDayBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getSingleDayBusinessHourModule().getTableName())
				.fields(FieldFactory.getSingleDayBusinessHoursFields());

		List<Map<String, Object>> singleDayProps = new ArrayList<>();
		for (BusinessHourContext singleDay : businessHours) {
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
				.select(FieldFactory.getSingleDayBusinessHoursFields()).table(businessHoursTable)
				.innerJoin(singleDayTable).on(businessHoursTable + ".ID = " + singleDayTable + ".PARENT_ID")
				.andCustomWhere(businessHoursTable + ".ORGID = ? AND " + businessHoursTable + ".ID = ?", orgId, id)
				.orderBy("dayOfWeek");

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			BusinessHoursList businessHours = new BusinessHoursList();
			for (Map<String, Object> prop : props) {
				businessHours.add(FieldUtil.getAsBeanFromMap(prop, BusinessHourContext.class));
			}
			return businessHours;
		}
		return null;
	}

	public static List<BusinessHoursContext> getBusinessHours(List<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getBusinessHoursModule();
		String businessHoursTable = ModuleFactory.getBusinessHoursModule().getTableName();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getBusinessHoursFields()).table(businessHoursTable)
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(ids, module)).orderBy("Id");
		List<Map<String, Object>> props = selectBuilder.get();
		List<BusinessHoursContext> businessHours = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			List<BusinessHourContext> singleDayBusinessHourList = getSingleDayBusinessHours(ids);
			for (Map<String, Object> prop : props) {
				BusinessHoursContext businessHour = FieldUtil.getAsBeanFromMap(prop, BusinessHoursContext.class);
				businessHour.setSingleDaybusinessHoursList(singleDayBusinessHourList.stream()
						.filter(bh -> bh.getParentId() == (businessHour.getId())).collect(Collectors.toList()));
				businessHours.add(businessHour);
			}
		}
		return businessHours;
	}

	public static List<BusinessHourContext> getSingleDayBusinessHours(List<Long> ids) throws Exception {// need
		FacilioModule module = ModuleFactory.getBusinessHoursModule();
		String businessHoursTable = ModuleFactory.getBusinessHoursModule().getTableName();
		String singleDayTable = ModuleFactory.getSingleDayBusinessHourModule().getTableName();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getSingleDayBusinessHoursFields()).table(businessHoursTable)
				.innerJoin(singleDayTable).on(businessHoursTable + ".ID = " + singleDayTable + ".PARENT_ID")
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(ids, module)).orderBy("dayOfWeek");
		List<Map<String, Object>> props = selectBuilder.get();
		List<BusinessHourContext> businessHours = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				businessHours.add(FieldUtil.getAsBeanFromMap(prop, BusinessHourContext.class));
			}
		}
		return businessHours;
	}

	public static void deleteBusinessHours(long id) throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, SQLException, RuntimeException {
		FacilioModule businessHoursTable = ModuleFactory.getBusinessHoursModule();
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder().table(businessHoursTable.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(id, businessHoursTable)).andCondition(
						CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getOrgId(), businessHoursTable));
		builder.delete();
	}

}

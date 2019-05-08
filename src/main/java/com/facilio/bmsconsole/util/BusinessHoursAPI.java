package com.facilio.bmsconsole.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

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


	public static void updateBusinessHours(BusinessHoursContext businessHours) throws SQLException, RuntimeException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		GenericUpdateRecordBuilder businessHoursBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getBusinessHoursModule().getTableName())
				.fields(FieldFactory.getBusinessHoursFields()).andCustomWhere("id = ?", businessHours.getId());
		Map<String, Object> props = FieldUtil.getAsProperties(businessHours);
		businessHoursBuilder.update(props);
		long parentId = businessHours.getId();

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getSingleDayBusinessHourModule().getTableName())
				.andCustomWhere("PARENT_ID = ?", parentId);
		builder.delete();

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

	public static List<BusinessHourContext> getSingleDayBusinessHours(long id) throws Exception {
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
			List<BusinessHourContext> businessHours = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				businessHours.add(FieldUtil.getAsBeanFromMap(prop, BusinessHourContext.class));
			}
			return businessHours;
		}
		else{
		return null;}
	}
	public static BusinessHoursContext getBusinessHoursById(long id) throws Exception {

		String businessHoursTable = ModuleFactory.getBusinessHoursModule().getTableName();
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getBusinessHoursFields()).table(businessHoursTable)
				.andCustomWhere(businessHoursTable + ".ORGID = ? AND " + businessHoursTable + ".ID = ?", orgId,id).orderBy("Id");
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			BusinessHoursContext businessHour = new BusinessHoursContext();
			for (Map<String, Object> prop : props) {
				 businessHour = FieldUtil.getAsBeanFromMap(prop, BusinessHoursContext.class);
				businessHour.setSingleDaybusinessHoursList(getSingleDayBusinessHours(businessHour.getId()));
				}
			return businessHour;
		} else {
			return null;
		}
	}
	public static List<BusinessHoursContext> getBusinessHours() throws Exception {

//		String businessHoursTable = ModuleFactory.getBusinessHoursModule().getTableName();
//		long orgId = AccountUtil.getCurrentOrg().getOrgId();
//		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
//				.select(FieldFactory.getBusinessHoursFields()).table(businessHoursTable)
//				.andCustomWhere(businessHoursTable + ".ORGID = ?", orgId).orderBy("Id");
//		List<Map<String, Object>> props = selectBuilder.get();
//		if (props != null && !props.isEmpty()) {
//			List<BusinessHoursContext> businessHours = new ArrayList<>();
//			for (Map<String, Object> prop : props) {
//				BusinessHoursContext businessHour = FieldUtil.getAsBeanFromMap(prop, BusinessHoursContext.class);
//				businessHour.setSingleDaybusinessHoursList(getSingleDayBusinessHours(businessHour.getId()));
//				businessHours.add(businessHour);
//			}
//			return businessHours;
//		} else {
//			return null;
//		}
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, (long)-1);
		Chain getBusinessHoursChain = ReadOnlyChainFactory.getBusinessHoursChain();
		getBusinessHoursChain.execute(context);
		List<BusinessHoursContext> businessHoursList=(List<BusinessHoursContext>) context.get(FacilioConstants.ContextNames.BUSINESS_HOUR_LIST);
		if(businessHoursList==null){
			businessHoursList=new ArrayList<BusinessHoursContext>();
		}
		return businessHoursList;
		
	}

	public static List<BusinessHourContext> getSingleDayBusinessHours() throws Exception {
		String businessHoursTable = ModuleFactory.getBusinessHoursModule().getTableName();
		String singleDayTable = ModuleFactory.getSingleDayBusinessHourModule().getTableName();
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getSingleDayBusinessHoursFields()).table(businessHoursTable)
				.innerJoin(singleDayTable).on(businessHoursTable + ".ID = " + singleDayTable + ".PARENT_ID")
				.andCustomWhere(businessHoursTable + ".ORGID = ?", orgId).orderBy("parentId,dayOfWeek");

		List<Map<String, Object>> props = selectBuilder.get();
		System.out.println("props" + props);
		if (props != null && !props.isEmpty()) {
			List<BusinessHourContext> businessHours = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				businessHours.add(FieldUtil.getAsBeanFromMap(prop, BusinessHourContext.class));
			}

			return businessHours;
		} else {
			return null;
		}
	}

	public static void deleteBusinessHours(long id) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException,
	SQLException, RuntimeException {
		FacilioModule businessHoursTable = ModuleFactory.getBusinessHoursModule();
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder().table(businessHoursTable.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(id, businessHoursTable)).andCondition(
						CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getOrgId(), businessHoursTable));
		builder.delete();
	}

}

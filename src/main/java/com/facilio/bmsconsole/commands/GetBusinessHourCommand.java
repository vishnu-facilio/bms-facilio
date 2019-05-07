package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetBusinessHourCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long id = (long) context.get(FacilioConstants.ContextNames.ID);
		if (id != -1) {
			String businessHoursTable = ModuleFactory.getBusinessHoursModule().getTableName();
			long orgId = AccountUtil.getCurrentOrg().getOrgId();
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getBusinessHoursFields()).table(businessHoursTable)
					.andCustomWhere(businessHoursTable + ".ORGID = ? AND " + businessHoursTable + ".ID = ?", orgId, id)
					.orderBy("Id");
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				BusinessHoursContext businessHour = new BusinessHoursContext();
				for (Map<String, Object> prop : props) {
					businessHour = FieldUtil.getAsBeanFromMap(prop, BusinessHoursContext.class);
					businessHour.setSingleDaybusinessHoursList(getSingleDayBusinessHours(businessHour.getId()));
				}
				context.put(FacilioConstants.ContextNames.BUSINESS_HOUR, businessHour);
            }
		} else {

			String businessHoursTable = ModuleFactory.getBusinessHoursModule().getTableName();
			long orgId = AccountUtil.getCurrentOrg().getOrgId();
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getBusinessHoursFields()).table(businessHoursTable)
					.andCustomWhere(businessHoursTable + ".ORGID = ?", orgId).orderBy("Id");
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				List<BusinessHoursContext> businessHours = new ArrayList<>();
				for (Map<String, Object> prop : props) {
					BusinessHoursContext businessHour = FieldUtil.getAsBeanFromMap(prop, BusinessHoursContext.class);
					businessHour.setSingleDaybusinessHoursList(getSingleDayBusinessHours(businessHour.getId()));
					businessHours.add(businessHour);
				}
				context.put(FacilioConstants.ContextNames.BUSINESS_HOUR_LIST, businessHours);
				
			}
		}
		return false;
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
		List<BusinessHourContext> businessHours = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				businessHours.add(FieldUtil.getAsBeanFromMap(prop, BusinessHourContext.class));
			}
		} 
		return businessHours;
	}

}

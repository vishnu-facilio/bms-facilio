package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;

public class GetBusinessHourCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> ids = new ArrayList<>();
		Long id = (Long)context.get(FacilioConstants.ContextNames.ID);
		if (id != null && id != -1) {
			ids = Collections.singletonList(id);
		}
		if (context.get(FacilioConstants.ContextNames.BUSINESS_HOUR_IDS) != null) {
			ids = (List<Long>) context.get(FacilioConstants.ContextNames.BUSINESS_HOUR_IDS);
		}
		FacilioModule module = ModuleFactory.getBusinessHoursModule();
		String businessHoursTable = ModuleFactory.getBusinessHoursModule().getTableName();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(FieldFactory.getBusinessHoursFields())
				.table(businessHoursTable).andCondition(CriteriaAPI.getCurrentOrgIdCondition(module)).orderBy("Id");
		if (!ids.isEmpty()) {
			selectBuilder.andCondition(CriteriaAPI.getIdCondition(ids, module));
		} 
		List<Map<String, Object>> props = selectBuilder.get();
		List<BusinessHoursContext> businessHours = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			List<BusinessHourContext> singleDayBusinessHourList = getSingleDayBusinessHours(ids);
			for (Map<String, Object> prop : props) {
				BusinessHoursContext businessHour = FieldUtil.getAsBeanFromMap(prop, BusinessHoursContext.class);
				businessHour.setSingleDaybusinessHoursList(singleDayBusinessHourList.stream().filter(bh -> bh.getParentId() == (businessHour.getId())).collect(Collectors.toList()));
				businessHours.add(businessHour);
				if (id != -1) {
					context.put(FacilioConstants.ContextNames.BUSINESS_HOUR, businessHour);
				}
			}
		}
		context.put(FacilioConstants.ContextNames.BUSINESS_HOUR_LIST, businessHours);
		return false;
	}

	public static List<BusinessHourContext> getSingleDayBusinessHours(List<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getBusinessHoursModule();
		String businessHoursTable = ModuleFactory.getBusinessHoursModule().getTableName();
		String singleDayTable = ModuleFactory.getSingleDayBusinessHourModule().getTableName();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getSingleDayBusinessHoursFields()).table(businessHoursTable)
					.innerJoin(singleDayTable).on(businessHoursTable + ".ID = " + singleDayTable + ".PARENT_ID")
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module)).orderBy("dayOfWeek");
		if (!ids.isEmpty()) {
			selectBuilder.andCondition(CriteriaAPI.getIdCondition(ids, module));
		} 
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

package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddSingleDayBusinessHourCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		BusinessHoursContext businessHours=(BusinessHoursContext)context.get(FacilioConstants.ContextNames.BUSINESS_HOUR);
		long parentId=(long)context.get(FacilioConstants.ContextNames.ID);
		List<BusinessHourContext> singleDayList = businessHours.getSingleDaybusinessHoursList();
		if(businessHours!=null&&parentId!=-1&&singleDayList!=null&&!singleDayList.isEmpty()){
		GenericInsertRecordBuilder singleDayBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getSingleDayBusinessHourModule().getTableName())
				.fields(FieldFactory.getSingleDayBusinessHoursFields());

		List<Map<String, Object>> singleDayProps = new ArrayList<>();
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
		return false;
	}

}

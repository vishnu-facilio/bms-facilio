package com.facilio.bmsconsole.commands;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;


public class AddShiftCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ShiftContext shift = (ShiftContext) context.get(FacilioConstants.ContextNames.SHIFT);
		long id = BusinessHoursAPI.addBusinessHours(shift.getDays());
		shift.setBusinessHoursId(id);
		Map<String, Object> props = FieldUtil.getAsProperties(shift);
		props.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
		GenericInsertRecordBuilder shiftBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getShiftModule().getTableName())
				.fields(FieldFactory.getShiftField())
				.addRecord(props);
		shiftBuilder.save();
		
		long shiftId = (long) props.get("id");
		List<BusinessHourContext> days = shift.getDays();
		
		ShiftAPI.scheduleJobs(shiftId, days);
		
		return false;
	}

}

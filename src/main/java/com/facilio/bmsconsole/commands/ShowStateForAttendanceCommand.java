package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttendanceContext;
import com.facilio.bmsconsole.context.AttendanceTransactionContext.TransactionType;
import com.facilio.bmsconsole.context.AttendanceStateContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;

public class ShowStateForAttendanceCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long userId = (long) context.get(FacilioConstants.ContextNames.USER_ID);
		long time = (long) context.get(FacilioConstants.ContextNames.TIMESTAMP);
		if (userId > 0 && time > 0) {
			long day = DateTimeUtil.getDayStartTimeOf(time);
			List<AttendanceStateContext> attendancestateList = new ArrayList<>();
			AttendanceContext attendance = getAttendance(userId, day);
			if (attendance == null) {
				attendancestateList.add(new AttendanceStateContext(TransactionType.CHECKIN.name()));
			} else if (attendance != null) {
				if (attendance.getCheckOutTime() > 0 && attendance.getLastBreakStartTime() < 0) {
					attendancestateList.add(new AttendanceStateContext(TransactionType.CHECKIN.name()));
				} else if (attendance.getCheckOutTime() < 0 && attendance.getLastBreakStartTime() > 0) {
					attendancestateList.add(new AttendanceStateContext(TransactionType.BREAKSTOP.name()));
				} else if (attendance.getLastCheckInTime() > 0 && attendance.getLastBreakStartTime() < 0) {
					attendancestateList.add(new AttendanceStateContext(TransactionType.CHECKOUT.name()));
				} else if (attendance.getLastBreakStartTime() < 0 && attendance.getCheckOutTime() < 0) {
					// checked in
					attendancestateList.add(new AttendanceStateContext(TransactionType.CHECKOUT.name()));
					attendancestateList.add(new AttendanceStateContext(TransactionType.BREAKSTART.name()));
				} else if (attendance.getLastBreakStartTime() > 0) {
					attendancestateList.add(new AttendanceStateContext(TransactionType.BREAKSTOP.name()));
				} else if (attendance.getLastBreakStartTime() < 0) {
					attendancestateList.add(new AttendanceStateContext(TransactionType.CHECKIN.name()));
					attendancestateList.add(new AttendanceStateContext(TransactionType.BREAKSTART.name()));
				} else if (attendance.getCheckOutTime() > 0) {
					attendancestateList.add(new AttendanceStateContext(TransactionType.CHECKIN.name()));
				}
			}
			context.put(FacilioConstants.ContextNames.RECORD, attendancestateList);
		}
		return false;
	}

	public AttendanceContext getAttendance(long userId, long time) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ATTENDANCE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ATTENDANCE);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<AttendanceContext> builder = new SelectRecordsBuilder<AttendanceContext>().module(module)
				.andCondition(
						CriteriaAPI.getCondition(fieldMap.get("user"), String.valueOf(userId), PickListOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("day"), DateOperators.TODAY))
				.beanClass(AttendanceContext.class).select(fields);
		List<AttendanceContext> attendance = builder.get();
		if (attendance != null && !attendance.isEmpty()) {
			return attendance.get(0);
		}

		return null;
	}
}

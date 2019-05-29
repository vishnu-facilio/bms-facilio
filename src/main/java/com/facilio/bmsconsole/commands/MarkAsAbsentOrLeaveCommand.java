package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttendanceContext;
import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.context.AttendanceContext.Status;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class MarkAsAbsentOrLeaveCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {

		List<Long> userIds = (List<Long>) context.get(FacilioConstants.ContextNames.USERS);
		if (userIds != null && !userIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ATTENDANCE);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ATTENDANCE);
			ShiftContext shift = (ShiftContext) context.get(FacilioConstants.ContextNames.SHIFT);
			long date = (long) context.get(FacilioConstants.ContextNames.DATE);
			long day = DateTimeUtil.getDayStartTimeOf(date);
			List<Long> absentUserIds = getAbsentUsers(userIds, day);
			List<AttendanceContext> attendanceList = new ArrayList<>();
			if (absentUserIds != null && !absentUserIds.isEmpty()) {
				for (Long id : absentUserIds) {
					User user = new User();
					user.setOuid(id);
					AttendanceContext attendance = new AttendanceContext();
					if (shift.isWeekend(day)) {
						attendance.setStatus(Status.LEAVE);
					} else {
						attendance.setStatus(Status.ABSENT);
					}
					attendance.setUser(user);
					attendance.setDay(day);
					attendanceList.add(attendance);
				}
			}
			if (attendanceList != null && !attendanceList.isEmpty()) {
				InsertRecordBuilder<AttendanceContext> insertBuilder = new InsertRecordBuilder<AttendanceContext>()
						.module(module).fields(fields);
				insertBuilder.addRecords(attendanceList);
				insertBuilder.save();
			}
		}
		return false;
	}

	private List<Long> getAbsentUsers(List<Long> userIds, long date) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ATTENDANCE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ATTENDANCE);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		List<Long> presentUserIds = new ArrayList<>();
		SelectRecordsBuilder<AttendanceContext> builder = new SelectRecordsBuilder<AttendanceContext>().module(module)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("user"), userIds, PickListOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("day"), String.valueOf(date), DateOperators.IS))
				.beanClass(AttendanceContext.class).select(fields);
		List<AttendanceContext> attendance = builder.get();
		if (attendance != null && !attendance.isEmpty()) {
			for (AttendanceContext att : attendance) {
				presentUserIds.add(att.getUser().getOuid());
			}
		}

		if (presentUserIds != null && !presentUserIds.isEmpty()) {
			for (Long id : presentUserIds) {
				if (userIds.contains(id)) {
					userIds.remove(id);
				}
			}
		}
		return userIds;
	}
}

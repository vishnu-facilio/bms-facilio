package com.facilio.bmsconsole.commands;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttendanceContext;
import com.facilio.bmsconsole.context.AttendanceContext.Status;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MarkAsAbsentOrLeaveCommand extends FacilioCommand {

	private boolean isWeekend(Shift shift, long timestamp) throws ParseException {
		JSONObject weekend = (JSONObject) new JSONParser().parse(shift.getWeekend());
		ZonedDateTime dateTime = DateTimeUtil.getDateTime(timestamp);
		int weekOfMonth = dateTime.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
		if (weekend != null) {
			List<Long> dayList = (List<Long>) weekend.get(String.valueOf(weekOfMonth));
			if (CollectionUtils.isNotEmpty(dayList)) {
				int i = dateTime.getDayOfWeek().getValue();
				return dayList.contains((long) i);
			}
		}
		return false;
	}

	@Override
	public boolean executeCommand(Context context) throws Exception {

		List<Long> userIds = (List<Long>) context.get(FacilioConstants.ContextNames.USERS);
		if (userIds != null && !userIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ATTENDANCE);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ATTENDANCE);
			Shift shift = (Shift) context.get(FacilioConstants.ContextNames.SHIFT);
			long date = (long) context.get(FacilioConstants.ContextNames.DATE);
			long day = DateTimeUtil.getDayStartTimeOf(date);
			List<Long> absentUserIds = getAbsentUsers(userIds, day);
			List<AttendanceContext> attendanceList = new ArrayList<>();
			if (absentUserIds != null && !absentUserIds.isEmpty()) {
				for (Long id : absentUserIds) {
					User user = new User();
					user.setOuid(id);
					AttendanceContext attendance = new AttendanceContext();
					if (isWeekend(shift, day)) {
						attendance.setStatus(Status.LEAVE);
					} else {
						attendance.setStatus(Status.ABSENT);
					}
					// Attendance to be moved to v3 shift.
					attendance.setV3Shift(shift);
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

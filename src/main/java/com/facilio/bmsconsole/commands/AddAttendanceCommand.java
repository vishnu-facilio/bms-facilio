package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttendanceContext;
import com.facilio.bmsconsole.context.AttendanceContext.Status;
import com.facilio.bmsconsole.context.AttendanceTransactionContext;
import com.facilio.bmsconsole.context.AttendanceTransactionContext.TransactionType;
import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.context.ShiftUserRelContext;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddAttendanceCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		AttendanceTransactionContext attendanceTransaction = (AttendanceTransactionContext) context
				.get(FacilioConstants.ContextNames.RECORD);
		if (attendanceTransaction != null) {
			AttendanceContext attendance = getAttendance(attendanceTransaction);
			if (attendance != null) {
				attendanceTransaction.setAttendance(attendance);
				context.put(FacilioConstants.ContextNames.RECORD, attendanceTransaction);
			}
		}
		return false;
	}

	public AttendanceContext getAttendance(AttendanceTransactionContext attendanceTransaction) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ATTENDANCE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ATTENDANCE);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		AttendanceContext att = null;
		User user;
		if (attendanceTransaction.getUser() != null) {
			user = attendanceTransaction.getUser();
		} else {
			user = AccountUtil.getCurrentUser();
		}
		SelectRecordsBuilder<AttendanceContext> builder = new SelectRecordsBuilder<AttendanceContext>().module(module)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("user"), String.valueOf(user.getOuid()),
						PickListOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("day"), DateOperators.TODAY))
				.beanClass(AttendanceContext.class).select(fields);
		List<AttendanceContext> attendance = builder.get();
		if (attendance != null && !attendance.isEmpty()) {
			att = attendance.get(0);
			if (attendanceTransaction.getTransactionTypeEnum() == TransactionType.CHECKIN) {
				att.setLastCheckInTime(attendanceTransaction.getTransactionTime());
			} else if (attendanceTransaction.getTransactionTypeEnum() == TransactionType.CHECKOUT) {
				att.setCheckOutTime(attendanceTransaction.getTransactionTime());
				long workhrs = att.getCheckOutTime() - att.getLastCheckInTime();
				if (att.getWorkingHours() > 0) {
					workhrs += att.getWorkingHours();
				}
				att.setWorkingHours(workhrs);
			}

		} else {
			AttendanceContext attendanceContext = new AttendanceContext();
			if (attendanceTransaction.getTransactionTypeEnum() == TransactionType.CHECKIN) {
				attendanceContext.setStatus(Status.PRESENT);
				List<ShiftUserRelContext> shiftusers = ShiftAPI.getShiftUserMapping(
						attendanceTransaction.getTransactionTime(), attendanceTransaction.getTransactionTime(),
						user.getOuid(), -1);
				ShiftUserRelContext shiftuser;
				if (CollectionUtils.isNotEmpty(shiftusers)) {
					shiftuser = shiftusers.get(0);
					ShiftContext shift = ShiftAPI.getShift(shiftuser.getShiftId());
					if (shift.isWeekend(attendanceTransaction.getTransactionTime())) {
						attendanceContext.setStatus(Status.HOLIDAY);
					}
				}
				attendanceContext.setCheckInTime(attendanceTransaction.getTransactionTime());
				attendanceContext.setLastCheckInTime(attendanceTransaction.getTransactionTime());
			}
			attendanceContext.setUser(AccountUtil.getCurrentUser());
			attendanceContext.setDay(attendanceTransaction.getTransactionTime());
			InsertRecordBuilder<AttendanceContext> insertBuilder = new InsertRecordBuilder<AttendanceContext>()
					.module(module).fields(fields);
			long id = insertBuilder.insert(attendanceContext);
			attendanceContext.setId(id);
			att = attendanceContext;
		}
		if (attendanceTransaction.getTransactionTypeEnum() == TransactionType.CHECKOUT) {
			att.setCheckOutTime(attendanceTransaction.getTransactionTime());
		}
		UpdateRecordBuilder<AttendanceContext> updateBuilder = new UpdateRecordBuilder<AttendanceContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(att.getId(), module));
		updateBuilder.update(att);
		return att;
	}
}

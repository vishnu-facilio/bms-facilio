package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttendanceContext;
import com.facilio.bmsconsole.context.AttendanceContext.Status;
import com.facilio.bmsconsole.context.AttendanceTransactionContext;
import com.facilio.bmsconsole.context.AttendanceTransactionContext.TransactionType;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddAttendanceCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		AttendanceTransactionContext attendanceTransaction = (AttendanceTransactionContext) context.get(FacilioConstants.ContextNames.RECORD);
		if(attendanceTransaction!=null) {
			AttendanceContext attendance = getAttendance(attendanceTransaction);
			if(attendance!=null) {
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
		User user = AccountUtil.getCurrentUser();
		SelectRecordsBuilder<AttendanceContext> builder = new SelectRecordsBuilder<AttendanceContext>()
				.module(module).andCondition(CriteriaAPI.getCondition(fieldMap.get("user"), String.valueOf(user.getOuid()), PickListOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("day"), DateOperators.TODAY))
				.beanClass(AttendanceContext.class)
				.select(fields);
		List<AttendanceContext> attendance = builder.get();
		if(attendance!=null && !attendance.isEmpty()) {
			att = attendance.get(0);
			if(attendanceTransaction.getTransactionTypeEnum() == TransactionType.CHECKIN) {
				att.setLastCheckInTime(attendanceTransaction.getTransactionTime());
			} else if(attendanceTransaction.getTransactionTypeEnum() == TransactionType.CHECKOUT) {
				att.setCheckOutTime(attendanceTransaction.getTransactionTime());
				long workhrs = att.getCheckOutTime() - att.getLastCheckInTime();
				if(att.getWorkingHours() > 0) {
					workhrs += att.getWorkingHours();
				} 
				att.setWorkingHours(workhrs);
			}
			
		} else {
			AttendanceContext attendanceContext = new AttendanceContext();
			if(attendanceTransaction.getTransactionTypeEnum() == TransactionType.CHECKIN) {
				attendanceContext.setStatus(Status.PRESENT);
				attendanceContext.setCheckInTime(attendanceTransaction.getTransactionTime());
				attendanceContext.setLastCheckInTime(attendanceTransaction.getTransactionTime());
			}
			attendanceContext.setUser(AccountUtil.getCurrentUser());
			attendanceContext.setDay(attendanceTransaction.getTransactionTime());
			InsertRecordBuilder<AttendanceContext> insertBuilder = new InsertRecordBuilder<AttendanceContext>()
					.module(module)
					.fields(fields);
			long id = insertBuilder.insert(attendanceContext);
			attendanceContext.setId(id);
			att = attendanceContext;
		} 
		if(attendanceTransaction.getTransactionTypeEnum() == TransactionType.CHECKOUT) {
			att.setCheckOutTime(attendanceTransaction.getTransactionTime());
		}
		UpdateRecordBuilder<AttendanceContext> updateBuilder = new UpdateRecordBuilder<AttendanceContext>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(att.getId(), module));
		updateBuilder.update(att);
		return att;
	}
}

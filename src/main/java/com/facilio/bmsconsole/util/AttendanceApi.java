package com.facilio.bmsconsole.util;

import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttendanceContext;
import com.facilio.bmsconsole.context.AttendanceTransactionContext;
import com.facilio.bmsconsole.context.BreakContext;
import com.facilio.bmsconsole.context.BreakTransactionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

@Deprecated
public class AttendanceApi {
	public static long addAttendanceTransaction(AttendanceTransactionContext attendanceTransaction) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ATTENDANCE_TRANSACTIONS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ATTENDANCE_TRANSACTIONS);
		InsertRecordBuilder<AttendanceTransactionContext> insertBuilder = new InsertRecordBuilder<AttendanceTransactionContext>()
				.module(module).fields(fields);
		return insertBuilder.insert(attendanceTransaction);
	}
	
	public static AttendanceContext getAttendanceForId(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ATTENDANCE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ATTENDANCE);
		SelectRecordsBuilder<AttendanceContext> selectBuilder = new SelectRecordsBuilder<AttendanceContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(AttendanceContext.class).andCondition(CriteriaAPI.getIdCondition(id, module));
		List<AttendanceContext> attendance = selectBuilder.get();
		if (attendance != null && !attendance.isEmpty()) {
			return attendance.get(0);
		}
		return null;
	}
	
	public static void updateAttendance(AttendanceContext attendance) throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ATTENDANCE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ATTENDANCE);
		UpdateRecordBuilder<AttendanceContext> updateBuilder = new UpdateRecordBuilder<AttendanceContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(attendance.getId(), module));
		updateBuilder.update(attendance);
	}
	
	public static BreakContext getBreakForId(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BREAK);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BREAK);
		SelectRecordsBuilder<BreakContext> selectBuilder = new SelectRecordsBuilder<BreakContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(BreakContext.class).andCondition(CriteriaAPI.getIdCondition(id, module));
		List<BreakContext> breaks = selectBuilder.get();
		if (breaks != null && !breaks.isEmpty()) {
			return breaks.get(0);
		}
		return null;
	}
	
	public static void addBreakTransaction(BreakTransactionContext breakTransaction) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BREAK_TRANSACTION);
		InsertRecordBuilder<BreakTransactionContext> builder = new InsertRecordBuilder<BreakTransactionContext>()
				.module(module)
				.fields(modBean.getAllFields(module.getName()));
		builder.addRecord(breakTransaction);
		builder.save();
	}
	
	public static BreakTransactionContext getBreakTransaction (long time) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BREAK_TRANSACTION);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BREAK_TRANSACTION);
		SelectRecordsBuilder<BreakTransactionContext> selectBuilder = new SelectRecordsBuilder<BreakTransactionContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(BreakTransactionContext.class)
				.andCondition(CriteriaAPI.getCondition("START_TIME", "startTime", String.valueOf(time), NumberOperators.EQUALS));
		List<BreakTransactionContext> breakTransaction = selectBuilder.get();
		if (breakTransaction != null && !breakTransaction.isEmpty()) {
			return breakTransaction.get(0);
		}
		return null;
	}
	
	public static void updateBreakTransaction(BreakTransactionContext record) throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BREAK_TRANSACTION);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BREAK_TRANSACTION);
		UpdateRecordBuilder<BreakTransactionContext> updateBuilder = new UpdateRecordBuilder<BreakTransactionContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(record.getId(), module));
		updateBuilder.update(record);
	}
	
	public static AttendanceContext getAttendance(long userid, long day) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ATTENDANCE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ATTENDANCE);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<AttendanceContext> builder = new SelectRecordsBuilder<AttendanceContext>().module(module)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("user"), String.valueOf(userid),
						PickListOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("day"), DateOperators.TODAY))
				.beanClass(AttendanceContext.class).select(fields);
		List<AttendanceContext> attendance = builder.get();
		if (attendance != null && !attendance.isEmpty()) {
			return attendance.get(0);
		}
		return null;
	}
}

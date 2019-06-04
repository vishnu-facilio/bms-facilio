package com.facilio.bmsconsole.util;

import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttendanceContext;
import com.facilio.bmsconsole.context.AttendanceTransactionContext;
import com.facilio.bmsconsole.context.BreakContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

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
}

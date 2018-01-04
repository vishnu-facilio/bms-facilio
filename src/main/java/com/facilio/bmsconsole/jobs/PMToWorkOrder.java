package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class PMToWorkOrder extends FacilioJob {

	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			long pmId = jc.getRecordId();
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
			bean.addWorkOrderFromPM(pmId);
			
			scheduleReminders(pmId, jc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void scheduleReminders(long pmId, JobContext jc) throws Exception {
		FacilioModule module = ModuleFactory.getPMReminderModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(FieldFactory.getPMReminderFields())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCustomWhere("PM_ID = ?", pmId);
		
		List<Map<String, Object>> reminderProps = selectBuilder.get();
		if(reminderProps != null && !reminderProps.isEmpty()) {
			for(Map<String, Object> reminderProp : reminderProps) {
				PMReminder reminder = FieldUtil.getAsBeanFromMap(reminderProp, PMReminder.class);
				CommonCommandUtil.schedulePMRemainder(reminder, jc.getExecutionTime(), jc.getNextExecutionTime());
			}
		}
	}
}

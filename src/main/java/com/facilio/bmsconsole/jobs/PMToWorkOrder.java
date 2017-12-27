package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.PMRemainder;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class PMToWorkOrder extends FacilioJob {

	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			long pmId = jc.getJobId();
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
			bean.addWorkOrderFromPM(pmId);
			
			scheduleRemainders(pmId, jc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void scheduleRemainders(long pmId, JobContext jc) throws Exception {
		FacilioModule module = ModuleFactory.getPMRemainderModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(FieldFactory.getPMRemainderFields())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCustomWhere("PM_ID = ?", pmId);
		
		List<Map<String, Object>> remainderProps = selectBuilder.get();
		if(remainderProps != null && !remainderProps.isEmpty()) {
			for(Map<String, Object> remainderProp : remainderProps) {
				PMRemainder remainder = FieldUtil.getAsBeanFromMap(remainderProp, PMRemainder.class);
				
				switch(remainder.getTypeEnum()) {
					case BEFORE:
						if(jc.getNextExecutionTime() != -1) {
							FacilioTimer.scheduleOneTimeJob(remainder.getId(), "PMRemainder", (jc.getNextExecutionTime()-remainder.getDuration()), "facilio");
						}
						break;
					case AFTER:
						FacilioTimer.scheduleOneTimeJob(remainder.getId(), "PMRemainder", (jc.getExecutionTime()+remainder.getDuration()), "facilio");
						break;
				}
			}
		}
	}
}

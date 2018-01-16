package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.leed.context.PMTriggerContext;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class PMToWorkOrder extends FacilioJob {

	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			long pmJobId = jc.getJobId();
			
			FacilioModule pmTriggerModule = ModuleFactory.getPMTriggersModule();
			FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
			GenericSelectRecordBuilder pmTriggerBuilder = new GenericSelectRecordBuilder()
															.select(FieldFactory.getPMTriggerFields())
															.table(pmTriggerModule.getTableName())
															.innerJoin(pmJobsModule.getTableName())
															.on(pmTriggerModule.getTableName()+".ID = "+pmJobsModule.getTableName()+".PM_TRIGGER_ID")
															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(pmTriggerModule))
															.andCondition(CriteriaAPI.getIdCondition(pmJobId, pmJobsModule));
			
			List<Map<String, Object>> props = pmTriggerBuilder.get();
			if(props != null && !props.isEmpty()) {
				PMTriggerContext pmTrigger = FieldUtil.getAsBeanFromMap(props.get(0), PMTriggerContext.class);
				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.RECORD_ID, pmTrigger.getPmId());
				context.put(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME, jc.getExecutionTime());
				context.put(FacilioConstants.ContextNames.PM_RESET_TRIGGERS, true);
				context.put(FacilioConstants.ContextNames.PM_CURRENT_TROGGER, pmTrigger);
				
				Chain executePm = FacilioChainFactory.getExecutePreventiveMaintenanceChain();
				executePm.execute(context);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMJobsContext.PMJobsStatus;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
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
			
			List<FacilioField> fields = FieldFactory.getPMJobFields();
			fields.addAll(FieldFactory.getPMTriggerFields());
			
			GenericSelectRecordBuilder pmTriggerBuilder = new GenericSelectRecordBuilder()
															.select(fields)
															.table(pmTriggerModule.getTableName())
															.innerJoin(pmJobsModule.getTableName())
															.on(pmTriggerModule.getTableName()+".ID = "+pmJobsModule.getTableName()+".PM_TRIGGER_ID")
															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(pmTriggerModule))
															.andCondition(CriteriaAPI.getIdCondition(pmJobId, pmJobsModule));
			
			List<Map<String, Object>> props = pmTriggerBuilder.get();
			if(props != null && !props.isEmpty()) {
				Map<String, Object> prop = props.get(0);
				PMJobsContext pmJob = FieldUtil.getAsBeanFromMap(prop, PMJobsContext.class);
				PMTriggerContext pmTrigger = FieldUtil.getAsBeanFromMap(prop, PMTriggerContext.class);
				pmTrigger.setId(pmJob.getPmTriggerId());
				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.STOP_PM_EXECUTION, !(pmJob.getStatusEnum() == PMJobsStatus.SCHEDULED));
				context.put(FacilioConstants.ContextNames.RECORD_ID, pmTrigger.getPmId());
				context.put(FacilioConstants.ContextNames.TEMPLATE_ID, pmJob.getTemplateId());
				context.put(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME, jc.getExecutionTime());
				context.put(FacilioConstants.ContextNames.PM_RESET_TRIGGERS, true);
				context.put(FacilioConstants.ContextNames.PM_CURRENT_TRIGGER, pmTrigger);
				context.put(FacilioConstants.ContextNames.PM_CURRENT_JOB, pmJob);
				
				Chain executePm = FacilioChainFactory.getExecutePreventiveMaintenanceChain();
				executePm.execute(context);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			CommonCommandUtil.emailException("PM Execution failed for pm job : "+jc.getJobId(), e);
			e.printStackTrace();
		}
	}
}

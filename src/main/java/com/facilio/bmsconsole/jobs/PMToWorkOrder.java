package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMJobsContext.PMJobsStatus;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class PMToWorkOrder extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(PMToWorkOrder.class.getName());

	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub
		try {
			if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SCHEDULED_WO)) {
				return;
			}
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
				
				LOGGER.debug("Executing pm job : "+pmJob.getId());
				LOGGER.debug("Executing pm job with pm id : "+pmJob.getPmId());
				LOGGER.debug("Executing pm trigger : "+pmTrigger.getId());
				LOGGER.debug("Executing pm trigger with pm id : "+pmTrigger.getPmId());
				PreventiveMaintenance pm = PreventiveMaintenanceAPI.getActivePM(pmTrigger.getPmId(), true);
				LOGGER.debug("Executing pm : "+pm);
				if(pm != null) {
					FacilioContext context = new FacilioContext();
					context.put(FacilioConstants.ContextNames.STOP_PM_EXECUTION, !(pmJob.getStatusEnum() == PMJobsStatus.SCHEDULED));
					context.put(FacilioConstants.ContextNames.RECORD_ID, pmTrigger.getPmId());
					context.put(FacilioConstants.ContextNames.TEMPLATE_ID, pmJob.getTemplateId());
					context.put(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME, pmJob.getNextExecutionTime());
					context.put(FacilioConstants.ContextNames.PM_RESET_TRIGGERS, true);
					context.put(FacilioConstants.ContextNames.PM_CURRENT_TRIGGER, pmTrigger);
					context.put(FacilioConstants.ContextNames.PM_CURRENT_JOB, pmJob);
					context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);
					Chain executePm = TransactionChainFactory.getExecutePreventiveMaintenanceChain();
					executePm.execute(context);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			CommonCommandUtil.emailException("PMToWorkOrder", "PM Execution failed for pm job : "+jc.getJobId(), e);
			LOGGER.error("PM Execution failed for pm job : ", e);
			throw e;
		}
	}
}

package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class ControlCommandExecutionJob extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(ControlCommandExecutionJob.class.getName());
	@Override
	public void execute(JobContext jc) throws Exception {
		try {
			long controlCommandId = jc.getJobId();
			
			LOGGER.info("ControlCommandExecutionJob started -- "+jc.getJobId());
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			
			SelectRecordsBuilder<ControlActionCommandContext> select = new SelectRecordsBuilder<ControlActionCommandContext>()
					.moduleName(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE)
					.beanClass(ControlActionCommandContext.class)
					.select(modBean.getAllFields(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), ControlActionCommandContext.Status.SCHEDULED.getIntVal()+"", NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getIdCondition(controlCommandId, modBean.getModule(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE)));
			
			List<ControlActionCommandContext> commands = select.get();
			
			if(commands != null && !commands.isEmpty()) {
				FacilioChain chain = TransactionChainFactory.getPushControlActionCommandChain();
				
				FacilioContext context = chain.getContext();
				
				context.put(ControlActionUtil.CONTROL_ACTION_COMMANDS, commands);
				
				chain.execute();
			}
			LOGGER.info("ControlCommandExecutionJob command executed -- "+commands);
			LOGGER.info("ControlCommandExecutionJob completed -- "+jc.getJobId());
		}
		catch(Exception e) {
			LOGGER.error("ControlCommandExecutionCreateScheduleJob Failed", e);
		}
	}

}

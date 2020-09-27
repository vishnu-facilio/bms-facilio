package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class BaseSchedulerJob extends FacilioJob {

	private static final Logger LOGGER = Logger.getLogger(BaseSchedulerJob.class.getName());
	Long jobId;
	
	@Override
	public void execute(JobContext jc) throws Exception {
		
		try {
			jobId = (Long) jc.getJobId();
			String jobName = (String) jc.getJobName();
			BaseScheduleContext baseScheduleContext = getBaseScheduleContext(jobId);
			if(baseScheduleContext != null) {	
				List<ModuleBaseWithCustomFields> childRecords = baseScheduleContext.getScheduleTypeEnum().getSchedulerTypeHandler().createRecords(baseScheduleContext);	
				if(childRecords != null && !childRecords.isEmpty()) {
					baseScheduleContext.saveRecords(childRecords);
				}
			}
			
		}
		catch(Exception e) {
			LOGGER.severe("Error occurred in BaseSchedulerJob for jobId: "+jobId+ " Exception: " +e);
			throw e;		
		}
	}
	
	private BaseScheduleContext getBaseScheduleContext(long scheduleId) throws Exception {	   
	   GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getBaseSchedulerFields())
				.table(ModuleFactory.getBaseSchedulerModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(scheduleId, ModuleFactory.getBaseSchedulerModule()));
			
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			BaseScheduleContext baseScheduleContext = FieldUtil.getAsBeanFromMap(props.get(0), BaseScheduleContext.class);
			return baseScheduleContext;
		}
		return null;
	}
}

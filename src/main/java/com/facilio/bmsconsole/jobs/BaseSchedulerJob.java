package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
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
			List<BaseScheduleContext> baseSchedules = getBaseSchedules();

			JSONObject jobProps = BmsJobUtil.getJobProps(jobId, jobName);
		    Boolean isUpdate = (jobProps != null) ? (Boolean) jobProps.getOrDefault("isUpdate", true) : true;
		    Boolean saveAsV3 = (jobProps != null) ? (Boolean) jobProps.getOrDefault("saveAsV3", false) : false;

			if(baseSchedules != null && !baseSchedules.isEmpty()) {	
				for(BaseScheduleContext baseScheduleContext: baseSchedules) {
					try {
						List<Map<String, Object>> parentRecordProps = baseScheduleContext.fetchParent();
						List<? extends ModuleBaseWithCustomFields> childRecords = baseScheduleContext.getScheduleTypeEnum().getSchedulerTypeHandler().createRecords(baseScheduleContext, isUpdate, parentRecordProps, false);	
						if(childRecords != null && !childRecords.isEmpty()) {
							if(saveAsV3) {
								baseScheduleContext.saveAsV3Records(childRecords);
							}
							else {
								baseScheduleContext.saveRecords(childRecords);
							}
						}
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE, "Exception while base scheduling job " +jobName+ " ,jobId: "+jobId+ " baseScheduleContext: "+baseScheduleContext);
						CommonCommandUtil.emailException("Exception while base scheduling job " +jobName, "JobId: "+jobId+ " baseScheduleContext: "+baseScheduleContext,e);
						throw e;
					}
				}		
			}
		}
		catch(Exception e) {
			LOGGER.severe("Error occurred in BaseSchedulerJob for jobId: "+jobId+ " Exception: " +e);
			throw e;		
		}
	}
	
	private List<BaseScheduleContext> getBaseSchedules() throws Exception {	   
	   GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getBaseSchedulerFields())
				.table(ModuleFactory.getBaseSchedulerModule().getTableName());
			
	   	Criteria subCriteria = new Criteria();
		subCriteria.addOrCondition(CriteriaAPI.getCondition("END_TIME", "endTime", "" + System.currentTimeMillis(), NumberOperators.GREATER_THAN));
		subCriteria.addOrCondition(CriteriaAPI.getCondition("END_TIME", "endTime", "-1", CommonOperators.IS_EMPTY));
		selectBuilder.andCriteria(subCriteria);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			List<BaseScheduleContext> baseSchedules = FieldUtil.getAsBeanListFromMapList(props, BaseScheduleContext.class);	
			return baseSchedules;
		}
		return null;
	}
	
}

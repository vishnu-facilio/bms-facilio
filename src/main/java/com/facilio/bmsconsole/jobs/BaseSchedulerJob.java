package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseSchedulerJob extends FacilioJob {

	private static final Logger LOGGER = Logger.getLogger(BaseSchedulerJob.class.getName());
	Long jobId;
	
	@Override
	public void execute(JobContext jc) throws Exception {
		
		try {
			jobId = (Long) jc.getJobId();
			String jobName = (String) jc.getJobName();
			List<BaseScheduleContext> baseSchedules = getBaseSchedules(jobId);

			JSONObject jobProps = BmsJobUtil.getJobProps(jobId, jobName);
		    Boolean isUpdate = (jobProps != null) ? (Boolean) jobProps.getOrDefault("isUpdate", false) : false;
		    Boolean saveAsV3 = (jobProps != null) ? (Boolean) jobProps.getOrDefault("saveAsV3", false) : false;
		    Boolean saveAsV3PreCreate = (jobProps != null) ? (Boolean) jobProps.getOrDefault("saveAsV3PreCreate", false) : false;

			if(baseSchedules != null && !baseSchedules.isEmpty()) {
				for(BaseScheduleContext baseScheduleContext: baseSchedules) {
					try {
						List<Map<String, Object>> parentRecordProps = baseScheduleContext.fetchParent();
						List<? extends ModuleBaseWithCustomFields> childRecords = baseScheduleContext.getScheduleTypeEnum().getSchedulerTypeHandler().createRecords(baseScheduleContext, isUpdate, parentRecordProps, false);	
						if(childRecords != null && !childRecords.isEmpty()) {
							if(saveAsV3) {
								baseScheduleContext.saveAsV3Records(childRecords);
							}
							else if(saveAsV3PreCreate) {
								baseScheduleContext.saveAsV3PreCreate(childRecords);
							}
							else {
								baseScheduleContext.saveRecords(childRecords);
							}
						}
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE, "Exception while base scheduling job " +jobName+ " ,jobId: "+jobId+ " baseScheduleContext: "+baseScheduleContext,e);
						CommonCommandUtil.emailException("Exception while base scheduling job " +jobName, "JobId: "+jobId+ " baseScheduleContext: "+baseScheduleContext,e);
					}
				}		
			}
		}
		catch(Exception e) {
			LOGGER.severe("Error occurred in BaseSchedulerJob for jobId: "+jobId+ " Exception: " +e);
		}
	}
	
	private List<BaseScheduleContext> getBaseSchedules(long jobId) throws Exception {	   
	   GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getBaseSchedulerFields())
				.table(ModuleFactory.getBaseSchedulerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("SCHEDULE_TYPE", "scheduleType", ""+jobId, NumberOperators.EQUALS));
			
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
